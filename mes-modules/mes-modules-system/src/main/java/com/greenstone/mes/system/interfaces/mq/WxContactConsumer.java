package com.greenstone.mes.system.interfaces.mq;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.greenstone.mes.common.core.annotation.TreeChildren;
import com.greenstone.mes.common.core.annotation.TreeId;
import com.greenstone.mes.common.core.annotation.TreeParentId;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.TreeUtils;
import com.greenstone.mes.common.core.utils.bean.BeanUtils;
import com.greenstone.mes.mq.consts.MqConst;
import com.greenstone.mes.mq.producer.MsgProducer;
import com.greenstone.mes.oa.enums.WxMsgType;
import com.greenstone.mes.oa.request.WxMsgSendCmd;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.domain.service.UserService;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.domain.service.ISysDeptService;
import com.greenstone.mes.system.domain.service.SysUserService;
import com.greenstone.mes.system.infrastructure.po.DeptPo;
import com.greenstone.mes.wxcp.domain.helper.WxDeptService;
import com.greenstone.mes.wxcp.domain.helper.WxUserService;
import com.greenstone.mes.wxcp.domain.helper.WxcpService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.WxDeptId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import com.greenstone.mes.wxcp.infrastructure.config.WxConfig;
import com.greenstone.mes.wxcp.infrastructure.config.WxCpProperties;
import com.greenstone.mes.wxcp.infrastructure.constant.WxcpConst;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpMessageService;
import me.chanjar.weixin.cp.api.WxCpOAuth2Service;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.Gender;
import me.chanjar.weixin.cp.bean.WxCpDepart;
import me.chanjar.weixin.cp.bean.WxCpUser;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Component
public class WxContactConsumer {

    private final WxUserService wxUserService;
    private final WxDeptService wxDeptService;
    private final WxcpService wxcpService;
    private final ISysDeptService deptService;
    private final SysUserService sysUserService;
    private final WxConfig wxConfig;
    private final MsgProducer<User> msgProducer;
    private final UserService userService;
    private final WxCpProperties wxCpProperties;


    @KafkaListener(topics = MqConst.Topic.WX_CALLBACK_CONTACT, groupId = MqConst.Group.SYSTEM)
    public void onMessage(WxCpXmlMessage wxMessage) {
        log.info("Receive msg, topic: {}, content: {}", MqConst.Topic.WX_CALLBACK_CONTACT, JSON.toJSONString(wxMessage));
        syncWxContact(wxMessage);
    }

    /**
     * @param wxMessage 企业微信消息
     */
    public void syncWxContact(WxCpXmlMessage wxMessage) {
        // 通讯录变更通知的ToUserName就是企业ID
        String cpId = wxMessage.getToUserName();

        switch (wxMessage.getChangeType()) {
            // 企业微信中创建或更新用户
            case WxcpConst.ContactChangeType.CREATE_USER,
                    WxcpConst.ContactChangeType.UPDATE_USER -> {
                log.info("wx contact sync: create or update user ");
                WxCpUser wxUser = wxUserService.getUser(new CpId(cpId), new WxUserId(wxMessage.getUserId()));
                DeptPo dept = deptService.getSysDept(DeptPo.builder().cpId(cpId).wxDeptId(Long.valueOf(wxUser.getMainDepartment())).build());
                syncWxUser(new CpId(cpId), wxUser, dept);
                // 新用户需要在自主授权之后更新手机号和邮箱
                wxCpOauth2(wxMessage);
            }
            // 企业微信中删除用户，在系统中软删除
            case WxcpConst.ContactChangeType.DELETE_USER -> {
                log.info("wx contact sync: delete user");
                SysUser userSelect = SysUser.builder().wxUserId(wxMessage.getUserId()).mainWxcpId(cpId).build();
                SysUser existUser = sysUserService.getSysUser(userSelect);
                if (existUser == null) {
                    log.error("this user not in gs system ignored: {}", wxMessage);
                    return;
                }
                SysUser userStatusUpdate = SysUser.builder().userId(existUser.getUserId()).deleted(true).build();
                sysUserService.updateUserStatus(userStatusUpdate);
                log.info("disable user, userId: {}, cpId: {}, wxUserId: {}", existUser.getUserId(), existUser.getMainWxcpId(), wxMessage.getUserId());

                try {
                    msgProducer.send(MqConst.Topic.USER_DELETE, userService.getUserById(existUser.getUserId()));
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // 企业微信中新建或更新部门
            case WxcpConst.ContactChangeType.CREATE_PARTY,
                    WxcpConst.ContactChangeType.UPDATE_PARTY -> {
                log.info("wx contact sync: create or update dept");
                // 获取企业微信部门信息，若获取不到则不同步
                WxCpDepart wxDept = wxDeptService.getDept(new CpId(cpId), new WxDeptId(Long.valueOf(wxMessage.getId())));
                if (wxDept == null) {
                    log.error("wxDept with id {} is not exist", wxMessage.getId());
                    return;
                }
                boolean syncSucceed = syncWxDept(new CpId(cpId), wxDept);
                if (!syncSucceed) {
                    syncWxDeptWithCpid(new CpId(cpId));
                }
            }
            // 企业微信中删除部门
            case WxcpConst.ContactChangeType.DELETE_PARTY -> {
                log.info("wx contact sync: delete dept");
                // 查找系统中的部门，若不存在，则不做处理
                DeptPo sysDept = deptService.getSysDept(DeptPo.builder().cpId(cpId)
                        .wxDeptId(Long.valueOf(wxMessage.getId())).build());
                if (sysDept == null) {
                    log.warn("dept with wxDeptId {} is not in system", wxMessage.getId());
                } else {
                    deptService.deleteDeptById(sysDept.getDeptId());
                }
            }
            default -> log.error("unsupported ContactChangeType {}", wxMessage.getChangeType());
        }
    }

    /**
     * 同步一个企业微信的用户
     *
     * @param cpId    企业ID
     * @param wxUser  企业微信用户
     * @param sysDept 系统部门
     */
    public SysUser syncWxUser(CpId cpId, WxCpUser wxUser, DeptPo sysDept) {
        log.info("sync wxUser, cpId: {}, wxUserId: {}, name: {}", cpId.id(), wxUser.getUserId(), wxUser.getName());
        if (sysDept == null) {
            syncWxDeptWithCpid(cpId);
            Long wxDeptId = Long.valueOf(wxUser.getMainDepartment());
            sysDept = deptService.getSysDept(DeptPo.builder().cpId(cpId.id()).wxDeptId(wxDeptId).build());
            if (sysDept == null) {
                log.error("dept sync failed with cpId {} and wxDeptId {}", cpId.id(), wxDeptId);
                log.error("stop user sync with cpId {} and wxDeptId {}", cpId.id(), wxDeptId);
                throw new ServiceException(StrUtil.format("can not sync dept with cpId {} and wxDeptId {}", cpId.id(), wxDeptId));
            }
        }
        SysUser sysUser = sysUserService.getSysUser(SysUser.builder().mainWxcpId(cpId.id()).wxUserId(wxUser.getUserId()).build());
        if (sysUser == null) {
            SysUser finalSysUser = new SysUser();
            wxUser.getExtAttrs().stream().filter(a -> a.getName().equals("工号")).findFirst().ifPresent(a -> finalSysUser.setEmployeeNo(a.getTextValue()));
            finalSysUser.setDeptId(sysDept.getDeptId());
            finalSysUser.setUserName(UUID.fastUUID().toString(true));
            finalSysUser.setNickName(wxUser.getName());
            finalSysUser.setMainWxcpId(cpId.id());
            finalSysUser.setWxUserId(wxUser.getUserId());
            finalSysUser.setPosition(wxUser.getPosition());
            finalSysUser.setSex(getSysGenderFromWx(wxUser.getGender()));
            finalSysUser.setAvatar(wxUser.getAvatar());
            finalSysUser.setPassword("$2a$10$N4GaNHytnjI.GZn9cwlideDuRow52mrA4/DHiJ2cWkWyPwKWkdg..");
            finalSysUser.setDeleted(false);
            sysUserService.createUser(finalSysUser);
            log.info("user added {}", finalSysUser);
            return finalSysUser;
        } else {
            wxUser.getExtAttrs().stream().filter(a -> a.getName().equals("工号")).findFirst().ifPresent(a -> sysUser.setEmployeeNo(a.getTextValue()));
            sysUser.setDeptId(sysDept.getDeptId());
            sysUser.setPosition(wxUser.getPosition());
            sysUser.setAvatar(wxUser.getAvatar());
            sysUser.setNickName(wxUser.getName());
            sysUser.setDeleted(false);
            sysUserService.updateUserProfile(sysUser);
            log.info("user updated: {}", sysUser);
            return sysUser;
        }
    }


    public String getSysGenderFromWx(Gender gender) {
        if (gender == null) {
            return "2";
        }
        if ("1".equals(gender.getCode())) {
            return "0";
        } else if ("2".equals(gender.getCode())) {
            return "1";
        } else {
            return "2";
        }
    }

    public void syncWxDeptWithCpid(CpId cpId) {
        log.info("sync wxDept with cpId {}", cpId);
        List<WxCpDepart> deptList = wxDeptService.listDept(cpId, null);
        List<OaWxCpDept> wxCpDeptList = new ArrayList<>();
        for (WxCpDepart wxCpDepart : deptList) {
            OaWxCpDept wxCpDept = new OaWxCpDept();
            BeanUtils.copyBeanProp(wxCpDept, wxCpDepart);
            wxCpDeptList.add(wxCpDept);
        }
        List<OaWxCpDept> deptTree = TreeUtils.toTree(wxCpDeptList);
        syncDept(cpId, deptTree);
    }

    private void syncDept(CpId cpId, List<OaWxCpDept> wxDeptTree) {
        if (CollUtil.isEmpty(wxDeptTree)) {
            return;
        }
        for (OaWxCpDept wxCpDepart : wxDeptTree) {
            boolean syncSucceed = syncWxDept(cpId, wxCpDepart);
            if (syncSucceed) {
                syncDept(cpId, wxCpDepart.getChildList());
            } else {
                log.error("wxDept sync failed, cpId: {}, deptId: {}, name: {}", cpId.id(), wxCpDepart.getId(), wxCpDepart.getName());
            }
        }
    }

    public boolean syncWxDept(CpId cpId, WxCpDepart wxDept) {
        if (wxDept == null) {
            log.error("sync wxDept failed cause wxDept which given is null");
            return false;
        }
        log.info("wxDept cpId: {}, deptId: {}, name: {} sync start", cpId.id(), wxDept.getId(), wxDept.getName());
        // 若不是企业微信根部门，则查找系统父部门，若系统中不存在，则忽略此部门
        DeptPo parentDept = null;
        if (1L != wxDept.getId()) {
            parentDept = deptService.getSysDept(DeptPo.builder().cpId(cpId.id())
                    .wxDeptId(wxDept.getParentId()).build());
            if (parentDept == null) {
                log.warn("parent wxDept is not in system, with cpId: {} and wxDeptId: {} ", cpId, wxDept.getParentId());
                return false;
            }
        }
        // 查找系统中本部门，若不存在，则新增
        DeptPo sysDept = deptService.getSysDept(DeptPo.builder().cpId(cpId.id())
                .wxDeptId(wxDept.getId()).build());
        if (sysDept == null) {
            log.info("wxDept with with cpId: {} and wxDeptId: {} is not in system，then add it", cpId, wxDept.getId());
            sysDept = DeptPo.builder().wxDeptId(wxDept.getId())
                    .cpId(cpId.id())
                    .parentId(parentDept == null ? 0L : parentDept.getDeptId())
                    .deptName(wxDept.getName())
                    .ancestors(parentDept == null ? "0" : parentDept.getAncestors() + "," + parentDept.getDeptId())
                    .orderNum(String.valueOf(wxDept.getOrder())).build();
            sysDept.setLeader(Arrays.toString(wxDept.getDepartmentLeader()));
            sysDept.setStatus("0");
            sysDept.setDelFlag("0");
            deptService.insertDept(sysDept);
            log.info("dept added {}", sysDept);
        }
        // 若存在，则更新
        else {
            log.info("wxDept with with cpId: {} and wxDeptId: {} is exist in system，then add it", cpId, wxDept.getId());
            sysDept.setDeptName(wxDept.getName());
            sysDept.setOrderNum(String.valueOf(wxDept.getOrder()));
            if (parentDept != null) {
                sysDept.setAncestors(parentDept.getAncestors() + "," + parentDept.getDeptId());
                sysDept.setParentId(parentDept.getDeptId());
            } else {
                sysDept.setAncestors("0");
                sysDept.setParentId(0L);
            }
            deptService.updateDept(sysDept);
            log.info("dept updated {}", sysDept);
        }
        return true;
    }

    public void wxCpOauth2(WxCpXmlMessage wxMessage) {
        String cpId = wxMessage.getToUserName();

        // 获取授权链接
        WxCpService wxCpService = wxcpService.getWxCpService(wxCpProperties.getDefaultAgentId());
        WxCpOAuth2Service oauth2Service = wxCpService.getOauth2Service();
        String state = cpId + "-" + wxMessage.getMsgType() + "-" + wxMessage.getChangeType();
        String url = oauth2Service.buildAuthorizationUrl(wxConfig.getOauth2RedirectUri(), state, WxConsts.OAuth2Scope.SNSAPI_PRIVATEINFO);
        log.info("auth url:{}", url);
        // 发送链接
        WxMsgSendCmd msgSendCmd = WxMsgSendCmd.builder().agentId(wxConfig.getAgentId(cpId, WxConfig.SYSTEM))
                .cpId(cpId)
                .url(url)
                .msgType(WxMsgType.TEXT_CARD)
                .title("用户信息同步")
                .content("请点击卡片进行授权，同步完成后可使用手机号作为账号登录系统。")
                .toUser(Collections.singletonList(WxMsgSendCmd.WxMsgUser.builder().wxUserId(wxMessage.getUserId()).build())).build();
        List<WxCpMessage> wxCpMessages = assembleTextCard(msgSendCmd);
        for (WxCpMessage wxCpMessage : wxCpMessages) {
            try {
                WxCpMessageService messageService = wxCpService.getMessageService();
                messageService.send(wxCpMessage);
            } catch (WxErrorException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public List<WxCpMessage> assembleTextCard(WxMsgSendCmd msgSendCmd) {
        List<WxCpMessage> messageList = new ArrayList<>();
        for (WxMsgSendCmd.WxMsgUser user : msgSendCmd.getToUser()) {
            WxCpMessage message = new WxCpMessage();
            message.setToUser(user.getWxUserId());
            // 部门信息
//        message.setToParty(deptId);
            message.setMsgType(msgSendCmd.getMsgType().getType());
            message.setAgentId(msgSendCmd.getAgentId());
            message.setTitle(StrUtil.isEmpty(msgSendCmd.getTitle()) ? "系统提醒" : msgSendCmd.getTitle());
            message.setDescription(StrUtil.isEmpty(msgSendCmd.getContent()) ? "格林司通管理系统已上线，欢迎访问" : msgSendCmd.getContent());
            message.setUrl(StrUtil.isEmpty(msgSendCmd.getUrl()) ? wxConfig.getOauth2RedirectUri() : msgSendCmd.getUrl());
            message.setEnableIdTrans(false);
            message.setEnableDuplicateCheck(false);
            message.setDuplicateCheckInterval(1800);
            messageList.add(message);
        }
        return messageList;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OaWxCpDept extends WxCpDepart {

        @Serial
        private static final long serialVersionUID = 6317413579521528L;
        @TreeId
        private Long id;

        @TreeParentId
        private Long parentId;

        @TreeChildren
        private List<OaWxCpDept> childList;


    }

}
