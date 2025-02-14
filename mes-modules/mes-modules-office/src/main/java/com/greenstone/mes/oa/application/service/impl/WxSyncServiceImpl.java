package com.greenstone.mes.oa.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.TreeUtils;
import com.greenstone.mes.common.core.utils.bean.BeanUtils;
import com.greenstone.mes.oa.application.assembler.AttendanceAssembler;
import com.greenstone.mes.oa.application.dto.OaWxCpDept;
import com.greenstone.mes.oa.application.helper.ApprovalHelper;
import com.greenstone.mes.oa.application.helper.WorkWxHelper;
import com.greenstone.mes.oa.application.service.ApprovalService;
import com.greenstone.mes.oa.application.service.OaWxScheduleService;
import com.greenstone.mes.oa.application.service.WxSyncService;
import com.greenstone.mes.oa.domain.OaWxScheduleDO;
import com.greenstone.mes.oa.domain.repository.WxApprovalRepository;
import com.greenstone.mes.oa.dto.OaWxUserImportDto;
import com.greenstone.mes.oa.infrastructure.enums.WxCp;
import com.greenstone.mes.oa.request.OaSyncApprovalCmd;
import com.greenstone.mes.system.api.RemoteDeptService;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.domain.SysDept;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.dto.result.UserAddResult;
import com.greenstone.mes.wxcp.domain.helper.WxDeptService;
import com.greenstone.mes.wxcp.domain.helper.WxOaService;
import com.greenstone.mes.wxcp.domain.helper.WxUserService;
import com.greenstone.mes.wxcp.domain.helper.WxcpService;
import com.greenstone.mes.wxcp.domain.types.*;
import com.greenstone.mes.wxcp.infrastructure.config.WxConfig;
import com.greenstone.mes.wxcp.infrastructure.config.WxCpProperties;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpDepart;
import me.chanjar.weixin.cp.bean.WxCpUser;
import me.chanjar.weixin.cp.bean.oa.WxCpApprovalDetailResult;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinSchedule;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2022-06-08-9:53
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WxSyncServiceImpl implements WxSyncService {

    private final WxcpService externalWxService;
    private final WxOaService externalWxOaService;
    private final WxDeptService externalWxDeptService;
    private final WxUserService externalWxUserService;
    private final RemoteSystemService remoteSystemService;
    private final OaWxScheduleService oaWxScheduleService;
    private final WxConfig wxConfig;
    private final AttendanceAssembler attendanceAssembler;
    private final ApprovalService approvalService;
    private final RemoteUserService userService;
    private final RemoteDeptService deptService;
    private final WorkWxHelper workWxHelper;
    private final WxApprovalRepository wxApprovalRepository;


    public UserAddResult addUserByPhoneNum(String phoneNum) {
        WxCpAndUser cpAndUser = getWxUserByPhoneNumber(phoneNum);
        if (cpAndUser == null) {
            log.warn("user with phone {} is not exist.", phoneNum);
            return UserAddResult.builder().success(false).build();
        } else {
            WxCpUser wxUser = cpAndUser.getWxUser();
            SysDept sysDept = deptService.getSysDept(SysDept.builder().cpId(cpAndUser.getCpId().id()).wxDeptId(Long.valueOf(wxUser.getMainDepartment())).build());
            SysUser sysUser = syncWxUser(cpAndUser.getCpId(), wxUser, sysDept);
            sysUser.setPhonenumber(phoneNum);
            remoteSystemService.updateUser(sysUser);
            log.info("update user with phone {}.", phoneNum);
            return UserAddResult.builder().success(true).build();
        }
    }

    @Override
    public UserAddResult addUserByWxUserId(String cpId, String wxUserId) {
        WxCpAndUser cpAndUser = getWxUserByWxUserId(cpId, wxUserId);
        if (cpAndUser == null) {
            log.warn("user with wxUserId {} is not exist.", wxUserId);
            return UserAddResult.builder().success(false).build();
        } else {
            WxCpUser wxUser = cpAndUser.getWxUser();
            SysDept sysDept = deptService.getSysDept(SysDept.builder().cpId(cpAndUser.getCpId().id()).wxDeptId(Long.valueOf(wxUser.getMainDepartment())).build());
            SysUser sysUser = syncWxUser(cpAndUser.getCpId(), wxUser, sysDept);
            remoteSystemService.updateUser(sysUser);
            log.info("update user with wxUserId {}.", wxUserId);
            return UserAddResult.builder().success(true).build();
        }
    }

    public void clearDeletedUser() {
        log.info("clear all wxCp deleted user");
        Map<String, WxCpService> serviceMap = externalWxService.getServiceMap(wxConfig.getAgentId(WxConfig.ATTENDANCE));
        for (String ciPid : serviceMap.keySet()) {
            clearDeletedUser(new CpId(ciPid));
        }
    }

    /**
     * 清理已经在企业微信删除的用户
     * 只对绑定了企业微信的用户有效
     *
     * @param cpId 企业ID
     */
    public void clearDeletedUser(CpId cpId) {
        log.info("clear wxCp {} deleted user", cpId.id());
        List<SysUser> sysUsers = userService.getUsers(SysUser.builder().mainWxcpId(cpId.id()).deleted(false).build());
        List<WxCpUser> wxCpUsers = externalWxUserService.listAllUser(cpId);
        for (SysUser sysUser : sysUsers) {
            boolean userExist = wxCpUsers.stream().anyMatch(wu -> wu.getUserId().equals(sysUser.getWxUserId()) && cpId.id().equals(sysUser.getMainWxcpId()));
            if (!userExist) {
                userService.remove(new Long[]{sysUser.getUserId()});
                log.info("user deleted, id: {}, name: {} cause not in wxCp with cpId: {} ", sysUser.getUserId(), sysUser.getNickName(), cpId.id());
            }
        }
    }

    /**
     * 将没有绑定企业微信的用户，根据手机号绑定企业微信
     * <p>
     * 每次最多处理50个账号
     */
    public void bindWxCpWithPhoneNum() {
        List<SysUser> sysUsers = userService.listAll();
        int num = 0;
        for (SysUser sysUser : sysUsers) {
            if (num > 50) {
                break;
            }
            if (StrUtil.isEmpty(sysUser.getMainWxcpId()) || StrUtil.isEmpty(sysUser.getWxUserId())
                    && StrUtil.isNotEmpty(sysUser.getPhonenumber())) {
                WxCpAndUser cpAndUser = getWxUserByPhoneNumber(sysUser.getPhonenumber());
                if (cpAndUser == null) {
                    log.warn("user {} {} not belong any wxcp.", sysUser.getUserId(), sysUser.getNickName());
                    userService.remove(new Long[]{sysUser.getUserId()});
                    log.warn("user {} {} has been deleted.", sysUser.getUserId(), sysUser.getNickName());
                } else {
                    sysUser.setMainWxcpId(cpAndUser.getCpId().id());
                    sysUser.setWxUserId(cpAndUser.getWxUser().getUserId());
                    log.info("band user {} {} with wx {} {}.", sysUser.getUserId(), sysUser.getNickName(), cpAndUser.getCpId().id(), cpAndUser.getWxUser().getUserId());
                }
                num++;
            }
        }
    }

    public WxCpAndUser getWxUserByPhoneNumber(String phoneNumber) {
        WxUserId wxUserId = null;
        CpId userCpId = null;
        List<String> cpIds = List.of("wx1dee7aa3b2526c66", "ww22045bf5ac4e9de5");
        for (String cpId : cpIds) {
            if (wxUserId == null) {
                try {
                    String wxUserIdStr = externalWxUserService.getUserId(new CpId(cpId), new PhoneNum(phoneNumber));
                    wxUserId = new WxUserId(wxUserIdStr);
                    if (wxUserId.id() != null) {
                        userCpId = new CpId(cpId);
                        log.info("find cpId: {}, wxUserId: {} with phone: {}", cpId, wxUserId, phoneNumber);
                    }
                } catch (RuntimeException e) {
                    // ignore
                }
            }
        }
        if (wxUserId != null) {
            WxCpUser wxUser = externalWxUserService.getUser(userCpId, wxUserId);
            return WxCpAndUser.builder().cpId(userCpId).wxUser(wxUser).build();
        } else {
            log.info("can not find wx user with phone: {}", phoneNumber);
            return null;
        }
    }

    public WxCpAndUser getWxUserByWxUserId(String cpId, String wxUserId) {
        CpId userCpId = new CpId(cpId);
        WxUserId userId = new WxUserId(wxUserId);
        if (wxUserId != null) {
            WxCpUser wxUser = externalWxUserService.getUser(userCpId, userId);
            return WxCpAndUser.builder().cpId(userCpId).wxUser(wxUser).build();
        } else {
            log.info("can not find wx user with wxUserId: null");
            return null;
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WxCpAndUser {
        private CpId cpId;
        private WxCpUser wxUser;
    }

    /**
     * 同步配置中的所有企业微信部门
     */
    @Override
    public void syncWxDeptWithConfig() {
        log.info("sync wx dept with config");
        // 通讯录应用无法访问获取部门列表的接口，这里使用打卡应用获取
        Map<String, WxCpService> serviceMap = externalWxService.getServiceMap(wxConfig.getAgentId(WxConfig.ATTENDANCE));
        serviceMap.forEach((cpId, wxCpService) -> syncWxDeptWithCpid(new CpId(cpId)));
    }

    /**
     * 同步一个企业微信的所有部门
     *
     * @param cpId 企业ID
     */
    @Override
    public void syncWxDeptWithCpid(CpId cpId) {
        log.info("sync wxDept with cpId {}", cpId);
        List<WxCpDepart> deptList = externalWxDeptService.listDept(cpId, null);
        List<OaWxCpDept> wxCpDeptList = new ArrayList<>();
        for (WxCpDepart wxCpDepart : deptList) {
            OaWxCpDept wxCpDept = new OaWxCpDept();
            BeanUtils.copyBeanProp(wxCpDept, wxCpDepart);
            wxCpDeptList.add(wxCpDept);
        }
        List<OaWxCpDept> deptTree = TreeUtils.toTree(wxCpDeptList);
        syncDept(cpId, deptTree);
    }

    /**
     * 同步部门（部门已经按照父子关系形成树状结构）
     *
     * @param wxDeptTree 部门树
     */
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

    /**
     * 同步（添加或更新）单个企业微信部门
     *
     * @param cpId   企业id
     * @param wxDept 微信部门信息
     * @return 是否同步成功
     */
    public boolean syncWxDept(CpId cpId, WxCpDepart wxDept) {
        if (wxDept == null) {
            log.error("sync wxDept failed cause wxDept which given is null");
            return false;
        }
        log.info("wxDept cpId: {}, deptId: {}, name: {} sync start", cpId.id(), wxDept.getId(), wxDept.getName());
        // 若不是企业微信根部门，则查找系统父部门，若系统中不存在，则忽略此部门
        SysDept parentDept = null;
        if (1L != wxDept.getId()) {
            parentDept = deptService.getSysDept(SysDept.builder().cpId(cpId.id())
                    .wxDeptId(wxDept.getParentId()).build());
            if (parentDept == null) {
                log.warn("parent wxDept is not in system, with cpId: {} and wxDeptId: {} ", cpId, wxDept.getParentId());
                return false;
            }
        }
        // 查找系统中本部门，若不存在，则新增
        SysDept sysDept = deptService.getSysDept(SysDept.builder().cpId(cpId.id())
                .wxDeptId(wxDept.getId()).build());
        if (sysDept == null) {
            log.info("wxDept with with cpId: {} and wxDeptId: {} is not in system，then add it", cpId, wxDept.getId());
            sysDept = SysDept.builder().wxDeptId(wxDept.getId())
                    .cpId(cpId.id())
                    .parentId(parentDept == null ? 0L : parentDept.getDeptId())
                    .deptName(wxDept.getName())
                    .ancestors(parentDept == null ? "0" : parentDept.getAncestors() + "," + parentDept.getDeptId())
                    .orderNum(String.valueOf(wxDept.getOrder())).build();
            sysDept.setLeader(Arrays.toString(wxDept.getDepartmentLeader()));
            sysDept.setStatus("0");
            sysDept.setDelFlag("0");
            deptService.addDept(sysDept);
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

    /**
     * 同步配置中的所有企业微信的用户
     */
    @Override
    public void syncWxUserWithConfig() {
        log.info("sync wx user with config");
        Map<String, WxCpService> serviceMap = externalWxService.getServiceMap(wxConfig.getAgentId(WxConfig.CONTACTS));
        serviceMap.forEach((cpId, wxCpService) -> syncWxUserWithCpId(new CpId(cpId)));
    }

    /**
     * 同步一个企业微信的所有部门
     *
     * @param cpId 企业ID
     */
    public void syncWxUserWithCpId(CpId cpId) {
        // 企业微信没有批量获取用户详情的接口，所以通过获取部门成员详情来同步
        log.info("sync wx user with cpId {}", cpId);
        List<WxCpDepart> deptList = externalWxDeptService.listDept(cpId, null);
        for (WxCpDepart depart : deptList) {
            // 查找企业微信部门对应的系统部门，若找不到则先进行部门同步
            SysDept sysDept = deptService.getSysDept(SysDept.builder().cpId(cpId.id()).wxDeptId(depart.getId()).build());
            // 同步部门的用户
            List<WxCpUser> wxUserList = externalWxUserService.listUser(cpId, new WxDeptId(depart.getId()), false);
            for (WxCpUser wxCpUser : wxUserList) {
                syncWxUser(cpId, wxCpUser, sysDept);
            }
        }
    }

    /**
     * 同步一个企业微信的用户
     *
     * @param cpId    企业ID
     * @param wxUser  企业微信用户
     * @param sysDept 系统部门
     */
    public SysUser syncWxUser(CpId cpId, WxCpUser wxUser, SysDept sysDept) {
        log.info("sync wxUser, cpId: {}, wxUserId: {}, name: {}", cpId.id(), wxUser.getUserId(), wxUser.getName());
        if (sysDept == null) {
            syncWxDeptWithCpid(cpId);
            Long wxDeptId = Long.valueOf(wxUser.getMainDepartment());
            sysDept = deptService.getSysDept(SysDept.builder().cpId(cpId.id()).wxDeptId(wxDeptId).build());
            if (sysDept == null) {
                log.error("dept sync failed with cpId {} and wxDeptId {}", cpId.id(), wxDeptId);
                log.error("stop user sync with cpId {} and wxDeptId {}", cpId.id(), wxDeptId);
                throw new ServiceException(StrUtil.format("can not sync dept with cpId {} and wxDeptId {}", cpId.id(), wxDeptId));
            }
        }
        SysUser sysUser = userService.getUser(SysUser.builder().mainWxcpId(cpId.id()).wxUserId(wxUser.getUserId()).build());
        if (sysUser == null) {
            SysUser finalSysUser = new SysUser();
            wxUser.getExtAttrs().stream().filter(a -> a.getName().equals("工号")).findFirst().ifPresent(a -> finalSysUser.setEmployeeNo(a.getTextValue()));
            finalSysUser.setDeptId(sysDept.getDeptId());
            finalSysUser.setUserName(UUID.fastUUID().toString(true));
            finalSysUser.setNickName(wxUser.getName());
            finalSysUser.setMainWxcpId(cpId.id());
            finalSysUser.setWxUserId(wxUser.getUserId());
            finalSysUser.setPosition(wxUser.getPosition());
            finalSysUser.setSex(ApprovalHelper.getSysGenderFromWx(wxUser.getGender()));
            finalSysUser.setAvatar(wxUser.getAvatar());
            finalSysUser.setPassword("123456");
            finalSysUser.setDeleted(false);
            remoteSystemService.insertUser(finalSysUser);
            log.info("user added {}", finalSysUser);
            return finalSysUser;
        } else {
            wxUser.getExtAttrs().stream().filter(a -> a.getName().equals("工号")).findFirst().ifPresent(a -> sysUser.setEmployeeNo(a.getTextValue()));
            sysUser.setDeptId(sysDept.getDeptId());
            sysUser.setPosition(wxUser.getPosition());
            sysUser.setAvatar(wxUser.getAvatar());
            sysUser.setNickName(wxUser.getName());
            sysUser.setDeleted(false);
            userService.basicsEdit(sysUser);
            log.info("user updated: {}", sysUser);
            return sysUser;
        }
    }


    /**
     * 同步企业微信审批数据
     */
    @Override
    public void syncApproval(OaSyncApprovalCmd oaSyncApprovalCmd) {
        log.info("syncApproval params:{}", oaSyncApprovalCmd);
        if (StrUtil.isEmpty(oaSyncApprovalCmd.getCpId())) {
            oaSyncApprovalCmd.setCpId(WxCp.AUTOMATION.getCpId());
        }
        // 查询同步记录最新时间，如果没有则同步当月0点到new date()
        Long lastSyncSec = wxApprovalRepository.lastSync(oaSyncApprovalCmd.getCpId());
        if (Objects.isNull(oaSyncApprovalCmd.getStartDate())) {
            oaSyncApprovalCmd.setStartDate(DateUtil.date(lastSyncSec * 1000));
        }
        if (Objects.isNull(oaSyncApprovalCmd.getEndDate())) {
            oaSyncApprovalCmd.setEndDate(new Date());
        }
        log.info("start sync approval data :{}", oaSyncApprovalCmd);
        // 审批模板
        List<WxCpProperties.SpTemplate> spTemplates = new ArrayList<>();
        List<WxCpProperties.CpSpTemplate> cpSpTemplates = wxConfig.getCpSpTemplates();
        Optional<WxCpProperties.CpSpTemplate> find = cpSpTemplates.stream().filter(a -> a.getCpId().equals(oaSyncApprovalCmd.getCpId())).findFirst();
        if (find.isPresent()) {
            spTemplates = find.get().getSpTemplates();
        }
        if (StrUtil.isNotEmpty(oaSyncApprovalCmd.getTemplateName())) {
            WxCpProperties.SpTemplate template = wxConfig.getTemplate(oaSyncApprovalCmd.getCpId(), oaSyncApprovalCmd.getTemplateName());
            if (Objects.nonNull(template)) {
                spTemplates.clear();
                spTemplates.add(template);
            }
        }
        for (WxCpProperties.SpTemplate spTemplate : spTemplates) {
            List<String> approvalNoList = externalWxOaService.listApprovalNo(new CpId(oaSyncApprovalCmd.getCpId()),
                    oaSyncApprovalCmd.getStartDate(), oaSyncApprovalCmd.getEndDate(), spTemplate);
            log.info("{} Found {} approval no.", spTemplate, approvalNoList.size());
            if (CollectionUtil.isNotEmpty(approvalNoList)) {
                for (String spNo : approvalNoList) {
                    WxCpApprovalDetailResult approvalDetail = externalWxOaService.getApprovalDetail(new CpId(oaSyncApprovalCmd.getCpId()), new SpNo(spNo));
                    approvalService.sync(new CpId(oaSyncApprovalCmd.getCpId()), approvalDetail.getInfo());
                }
            }
        }
        wxApprovalRepository.insertApprovalSyncRecord(oaSyncApprovalCmd.getCpId(), oaSyncApprovalCmd.getStartDate().getTime() / 1000, oaSyncApprovalCmd.getEndDate().getTime() / 1000);
        log.info("sync approval end");
    }

    @Override
    public void syncApprovalOfAuditing(OaSyncApprovalCmd oaSyncApprovalCmd) {
        if (Objects.isNull(oaSyncApprovalCmd.getStartDate())) {
            oaSyncApprovalCmd.setStartDate(DateUtil.beginOfMonth(new Date()));
        }
        if (Objects.isNull(oaSyncApprovalCmd.getEndDate())) {
            oaSyncApprovalCmd.setEndDate(new Date());
        }
        if (StrUtil.isEmpty(oaSyncApprovalCmd.getCpId())) {
            oaSyncApprovalCmd.setCpId(WxCp.AUTOMATION.getCpId());
        }
        List<String> approvalNoList = approvalService.listApprovalOfAuditing(oaSyncApprovalCmd.getStartDate(), oaSyncApprovalCmd.getEndDate(), oaSyncApprovalCmd.getCpId());
        log.info("Found {} auditing approval no.", approvalNoList.size());
        if (CollectionUtil.isNotEmpty(approvalNoList)) {
            for (String spNo : approvalNoList) {
                WxCpApprovalDetailResult approvalDetail = externalWxOaService.getApprovalDetail(new CpId(oaSyncApprovalCmd.getCpId()), new SpNo(spNo));
                approvalService.sync(new CpId(oaSyncApprovalCmd.getCpId()), approvalDetail.getInfo());
            }
        }
    }

    /**
     * 同步企业微信审批数据
     */
    @Override
    @Async
    public void syncSchedule(Date startDate, Date endDate) {
        List<String> wxUserIds;
        for (WxCp wxCp : WxCp.values()) {
            List<WxCpUser> allUsers = externalWxUserService.listAllUser(new CpId(wxCp.getCpId()));
            wxUserIds = allUsers.stream().map(WxCpUser::getUserId).toList();
            // 拿到班次信息
            List<WxCpCheckinSchedule> wxSchedules = externalWxOaService.listSchedule(new CpId(wxCp.getCpId()), startDate, endDate, wxUserIds);
            log.info("sync schedule size: {}", wxSchedules.size());
            if (CollUtil.isNotEmpty(wxSchedules)) {
                List<OaWxScheduleDO> scheduleDO = attendanceAssembler.toScheduleDO(new CpId(wxCp.getCpId()), wxSchedules);
                List<OaWxScheduleDO> scheduleDOList = oaWxScheduleService.listScheduleDO(new CpId(scheduleDO.get(0).getCpId()), startDate, endDate, wxUserIds);
                log.info("insert or update schedule size: {}", scheduleDO.size());
                for (OaWxScheduleDO wxScheduleDO : scheduleDO) {
                    Optional<OaWxScheduleDO> find = scheduleDOList.stream().filter(a -> a.getUserId().equals(wxScheduleDO.getUserId()) && Objects.equals(a.getScheduleDate(), wxScheduleDO.getScheduleDate())).findFirst();
                    // 根据条件，判断更新or新增
                    if (find.isPresent()) {
                        wxScheduleDO.setId(find.get().getId());
                        oaWxScheduleService.updateById(wxScheduleDO);
                    } else {
                        oaWxScheduleService.save(wxScheduleDO);
                    }
                }
            }
        }
    }

    public List<SysDept> getAllSysDeptList() {
        // 获取系统中所有的部门信息
        R<List<SysDept>> r = remoteSystemService.getDeptList(null);
        if (r.isNotPresent()) {
            log.error("Error get sys dept {}", r.getMsg());
            throw new ServiceException("获取系统部门失败: " + r.getMsg());
        }
        return r.getData();
    }

    public SysDept getSysDeptByName(String deptName) {
        // 获取系统中所有的部门信息
        R<SysDept> sysDeptR = remoteSystemService.getDeptInfo(deptName);
        if (sysDeptR.isNotPresent()) {
            return null;
        }
        return sysDeptR.getData();
    }

    public List<SysUser> getAllSysUserList() {
        R<List<SysUser>> r = remoteSystemService.listAllUser();
        if (r.isNotPresent()) {
            log.error("Error get sys user {}", r.getMsg());
            throw new ServiceException("获取系统用户失败: " + r.getMsg());
        }
        return r.getData();
    }

    public void insertOrUpdateDept(List<SysDept> sysDeptList, String deptName, Long parentId, String ancestors, boolean isUpdate) {
        // 查找系统中是否已经存在该部门
        Optional<SysDept> deptOptional = sysDeptList.stream().filter(d -> d.getDeptName().equals(deptName)).findFirst();
        // 设置部门属性
        SysDept sysDept = deptOptional.orElseGet(SysDept::new);
        sysDept.setParentId(parentId);
        sysDept.setAncestors(ancestors);
        sysDept.setDeptName(deptName);
        sysDept.setStatus("0");
        sysDept.setDelFlag("0");
        if (deptOptional.isPresent() && isUpdate) {
            remoteSystemService.updateDept(sysDept);
            log.info("update dept {}", JSON.toJSONString(sysDept));
        } else {
            sysDept.setOrderNum("0");
            remoteSystemService.insertDept(sysDept);
            log.info("insert dept {}", JSON.toJSONString(sysDept));
        }
    }

    public SysDept getRootDept(String cpId, List<SysDept> sysDeptList) {
        SysDept rootDept = new SysDept();
        for (WxCp wxCp : WxCp.values()) {
            if (wxCp.getCpId().equals(cpId)) {
                Optional<SysDept> find = sysDeptList.stream().filter(a -> a.getDeptName().equals(wxCp.getCpName())).findFirst();
                if (find.isPresent()) {
                    rootDept = find.get();
                } else {
                    log.error("Error get sys dept {}", wxCp.getCpName());
                    throw new ServiceException(BizError.E50002, wxCp.getCpName());
                }
            }
        }
        return rootDept;
    }

    public void syncWxDeptByImport(String cpId, List<OaWxUserImportDto> importReqList) {
        // 系统中的部门和用户
        List<SysDept> sysDeptList = getAllSysDeptList();
        SysDept rootDept = getRootDept(cpId, sysDeptList);
        Map<String, List<OaWxUserImportDto>> deptMap = importReqList.stream().collect(Collectors.groupingBy(OaWxUserImportDto::getDept));
        deptMap.forEach((deptName, userImport) -> {
            String[] deptArr = deptName.split("/");
            Long parentId = rootDept.getDeptId();
            String ancestors = "0,100," + parentId;
            for (String dept : deptArr) {
                if (rootDept.getDeptName().contains(dept) || dept.contains(rootDept.getDeptName())) {
                    continue;
                }
                insertOrUpdateDept(sysDeptList, dept, parentId, ancestors, true);
                SysDept sysDeptByName = getSysDeptByName(dept);
                if (sysDeptByName != null) {
                    parentId = sysDeptByName.getDeptId();
                    ancestors += "," + parentId;
                } else {
                    log.error("Error get sys dept {}", dept);
                    throw new ServiceException(BizError.E50003, dept);
                }
            }
        });
    }

    public void syncWxUserByImport(String cpId, List<OaWxUserImportDto> importReqList) {
        // 系统中的部门和用户
        List<SysDept> sysDeptList = getAllSysDeptList();
        List<SysUser> userList = getAllSysUserList();
        for (OaWxUserImportDto wxUserImportCmd : importReqList) {
            if (StrUtil.isEmpty(wxUserImportCmd.getPhone()) || wxUserImportCmd.getPhone().length() != 11) {
                log.warn("Sync ignore no mobile user {}", wxUserImportCmd.getName());
            } else {
                // 对应部门
                SysDept sysDept;
                String[] deptArr = wxUserImportCmd.getDept().split("/");
                if (deptArr.length >= 1) {
                    sysDept = sysDeptList.stream().filter(d -> d.getDeptName().equals(deptArr[deptArr.length - 1])).findFirst().orElse(null);
                } else {
                    log.error("Error get sys dept {}", wxUserImportCmd.getDept());
                    throw new ServiceException(BizError.E50003, wxUserImportCmd.getDept());
                }
                // 对应人员
                SysUser userFind = findSystemUser(userList, wxUserImportCmd);

                SysUser sysUser = new SysUser();
                sysUser.setWxUserId(wxUserImportCmd.getUserId());
                sysUser.setMainWxcpId(cpId);
                sysUser.setDeptId(sysDept == null ? null : sysDept.getDeptId());
                sysUser.setPhonenumber(wxUserImportCmd.getPhone());
                sysUser.setEmail(wxUserImportCmd.getEmail());
                sysUser.setNickName(wxUserImportCmd.getName());
                sysUser.setSex(ApprovalHelper.getSysGenderFromWxStr(wxUserImportCmd.getSex()));
                sysUser.setUserName(wxUserImportCmd.getPhone());
                sysUser.setEmployeeNo(wxUserImportCmd.getEmployeeNo());
                sysUser.setPosition(wxUserImportCmd.getPosition());
                // 不存在该人员：新增
                if (Objects.isNull(userFind)) {
                    sysUser.setPassword("123456");
                    remoteSystemService.insertUser(sysUser);
                    log.info("insert user {}", JSON.toJSONString(sysUser));
                } else {
                    sysUser.setUserId(userFind.getUserId());
                    remoteSystemService.updateUser(sysUser);
                    log.info("update user {}", JSON.toJSONString(sysUser));
                }
            }
        }
    }

    private SysUser findSystemUser(List<SysUser> userList, OaWxUserImportDto wxUserImportCmd) {
        Optional<SysUser> userOptional = userList.stream().filter(u -> wxUserImportCmd.getUserId().equals(u.getWxUserId()) || wxUserImportCmd.getPhone().equals(u.getPhonenumber()) || wxUserImportCmd.getPhone().equals(u.getUserName())).findFirst();
        return userOptional.orElse(null);
    }

    @Override
    public void importSyncWxUser(String cpId, List<OaWxUserImportDto> importReqList) {
        log.info("import wx user start");
        // 同步部门
        syncWxDeptByImport(cpId, importReqList);
        // 同步人员
        syncWxUserByImport(cpId, importReqList);
    }

}
