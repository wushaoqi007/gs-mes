package com.greenstone.mes.mail.domain.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.external.dto.result.DomainResult;
import com.greenstone.mes.external.dto.result.MailboxChangeResult;
import com.greenstone.mes.mail.cmd.MailBoxAddCmd;
import com.greenstone.mes.mail.cmd.MailBoxDeleteCmd;
import com.greenstone.mes.mail.cmd.MailBoxEditCmd;
import com.greenstone.mes.mail.consts.MailConst;
import com.greenstone.mes.mail.domain.service.MailBoxService;
import com.greenstone.mes.mail.external.MailBoxExternalService;
import com.greenstone.mes.mail.external.dto.MailboxCreate;
import com.greenstone.mes.mail.external.dto.MailboxOriginResult;
import com.greenstone.mes.mail.external.dto.MailboxUpdate;
import com.greenstone.mes.mail.infrastructure.config.MailConfig;
import com.greenstone.mes.mail.infrastructure.mapper.MailBoxMapper;
import com.greenstone.mes.mail.infrastructure.persistence.MailBox;
import com.greenstone.mes.mail.interfaces.rest.query.MailboxQuery;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.wxcp.domain.helper.WxcpService;
import com.greenstone.mes.wxcp.infrastructure.config.WxCpProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class MailBoxServiceImpl implements MailBoxService {

    private final MailBoxMapper mailBoxMapper;

    private final WxcpService wxcpService;

    private final WxCpProperties wxCpProperties;

    private final MailBoxExternalService mailBoxExternalService;

    private final MailConfig mailConfig;

    private final RemoteUserService userService;

    @Override
    public MailboxChangeResult createMailBox(MailBoxAddCmd addCmd) {
        User user = userService.getById(addCmd.getUserId());
        if (user == null) {
            throw new ServiceException("使用人不存在");
        }

        String email = addCmd.getLocalPart() + "@" + mailConfig.getDomain();

        LambdaQueryWrapper<MailBox> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(MailBox::getEmail, email);
        MailBox existMail = mailBoxMapper.findOneWithOutLogic(queryWrapper);
        // 处理已经有邮箱记录的情况
        if (existMail != null) {
            // 如果已经逻辑删除，则恢复邮箱记录
            if (existMail.getDeleted() || existMail.getExpirationTime() != null) {
                LambdaUpdateWrapper<MailBox> updateWrapper = Wrappers.lambdaUpdate();
                updateWrapper.eq(MailBox::getEmail, email);
                updateWrapper.set(MailBox::getDeleted, false);
                updateWrapper.set(MailBox::getExpirationTime, null);
                mailBoxMapper.recoverBatch(updateWrapper);
            }
        }
        // 还没有记录的话，保存邮箱信息到数据库
        else {
            MailBox mailBox = MailBox.builder().email(email)
                    .name(addCmd.getName())
                    .quota(addCmd.getQuota())
                    .quotaUsed(0L)
                    .percentInUse(0L)
                    .type(addCmd.getMailboxType())
                    .userId(user.getUserId())
                    .wxCpId(user.getWxCpId())
                    .wxUserId(user.getWxUserId())
                    .nickName(user.getNickName())
                    .employeeNo(user.getEmployeeNo()).build();
            mailBoxMapper.insert(mailBox);
        }


        // 创建邮箱
        MailboxCreate create = new MailboxCreate();
        create.setLocalPart(addCmd.getLocalPart());
        create.setDomain(mailConfig.getDomain());
        create.setName(addCmd.getName());
        create.setPassword(addCmd.getPassword());
        create.setPassword2(addCmd.getPassword2());
        create.setQuota(addCmd.getQuota());
        MailboxChangeResult mailBox = mailBoxExternalService.createMailBox(create);
        if (!mailBox.isSuccess()) {
            throw new ServiceException(mailBox.getMessage());
        }
        return mailBox;
    }

    @Override
    public MailboxChangeResult createMailBoxForNewUser(MailBoxAddCmd addCmd) {
        User user = userService.getById(addCmd.getUserId());

        log.info("邮箱：开始给新成员创建邮箱：{}", addCmd);
        MailboxOriginResult mailBox = mailBoxExternalService.getMailBox(addCmd.getLocalPart() + "@" + mailConfig.getDomain());
        // 如果已经有同名邮箱，则在邮箱后面增加工号的数字
        if (mailBox != null && mailBox.getLocalPart() != null) {
            log.info("成员姓名拼音邮箱已经存在，改用姓名拼音加工号的邮箱");
            if (StrUtil.isEmpty(user.getEmployeeNo())) {
                log.info("还未设置工号，忽略此次邮箱创建");
            } else {
                int employeeNo = Integer.parseInt(user.getEmployeeNo().substring(1));
                addCmd.setLocalPart(addCmd.getLocalPart() + employeeNo);
                mailBox = mailBoxExternalService.getMailBox(addCmd.getLocalPart() + "@" + mailConfig.getDomain());
                if (mailBox != null && mailBox.getLocalPart() != null) {
                    log.info("邮箱创建失败，邮箱 {} 已存在", addCmd.getLocalPart() + "@" + mailConfig.getDomain());
                }
            }

        }
        MailboxChangeResult mailBoxResult = createMailBox(addCmd);
        log.info("邮箱：邮箱创建完成: {}", mailBoxResult);

        return mailBoxResult;
    }

    @Override
    public MailboxChangeResult deleteMailBox(MailBoxDeleteCmd deleteCmd) {
        // 删除邮箱
        MailboxChangeResult changeResult = mailBoxExternalService.deleteMailBox(deleteCmd.getEmail());

        if (!changeResult.isSuccess()) {
            throw new ServiceException(changeResult.getMessage());
        }

        // 更新邮箱信息
        mailBoxMapper.deleteById(MailBox.builder().email(deleteCmd.getEmail()).deleteReason(deleteCmd.getReason()).build());

        String content = StrUtil.format("邮箱 {} 已经删除", deleteCmd.getEmail());
        WxCpMessage msg = WxCpMessage.TEXTCARD().title("邮箱删除提醒").description(content).url("https://mail.wxgreenstone.com:6443")
                .btnTxt("管理邮箱").toUser("gongqi|gurenkai|LiangDongQin|ZhaXiangAi").agentId(wxCpProperties.getDefaultAgentId()).build();
        try {
            WxCpService wxCpService = wxcpService.getWxCpService(wxCpProperties.getDefaultAgentId());
            wxCpService.getMessageService().send(msg);
        } catch (WxErrorException e) {
            log.error("发送消息失败", e);
            throw new RuntimeException(e);
        }

        return changeResult;
    }

    @Override
    public void delayedDeleteMailBox(String mailAddress, int delayDays) {
        log.info("setMailExpirationTime: {} {}", mailAddress, delayDays);

        MailBox mailBox = mailBoxMapper.selectById(mailAddress);
        if (mailBox == null) {
            log.error("延迟删除邮箱失败，邮箱 {} 不存在", mailAddress);
            return;
        }

        LocalDateTime expirationTime = LocalDateTime.now().plusDays(delayDays);
        mailBoxMapper.updateById(MailBox.builder().email(mailAddress).expirationTime(expirationTime).build());

        String content = StrUtil.format("邮箱 {} 将于 {} 天后删除", mailAddress, delayDays);
        WxCpMessage msg = WxCpMessage.TEXTCARD().title("邮箱删除提醒").description(content).url("https://mail.wxgreenstone.com:6443")
                .btnTxt("管理邮箱").toUser("gongqi|gurenkai|LiangDongQin|ZhaXiangAi").agentId(wxCpProperties.getDefaultAgentId()).build();
        try {
            WxCpService wxCpService = wxcpService.getWxCpService(wxCpProperties.getDefaultAgentId());
            wxCpService.getMessageService().send(msg);
        } catch (WxErrorException e) {
            log.error("发送消息失败", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteExpirationMailBox() {
        log.info("开始删除到期的邮箱");
        LambdaQueryWrapper<MailBox> queryWrapper = Wrappers.lambdaQuery(MailBox.class).le(MailBox::getExpirationTime, LocalDateTime.now());
        List<MailBox> mailBoxes = mailBoxMapper.selectList(queryWrapper);
        for (MailBox mailBox : mailBoxes) {
            deleteMailBox(MailBoxDeleteCmd.builder().email(mailBox.getEmail()).build());
        }
        log.info("完成删除到期的邮箱");
    }

    @Override
    public MailBox getMailBox(String email) {
        return mailBoxMapper.selectById(email);
    }

    @Override
    public MailboxChangeResult editMailBox(MailBoxEditCmd editCmd) {
        if (StrUtil.isNotBlank(editCmd.getPassword()) && !editCmd.getPassword().equals(editCmd.getPassword2())) {
            throw new RuntimeException("密码不一致");
        }
        MailBox mailBox = mailBoxMapper.selectById(editCmd.getEmail());
        if (mailBox == null) {
            throw new RuntimeException("邮箱不存在" + editCmd.getEmail());
        }

        MailboxUpdate update = new MailboxUpdate();
        update.setItems(List.of(editCmd.getEmail()));
        MailboxUpdate.Attr attr =
                MailboxUpdate.Attr.builder().name(StrUtil.isNotBlank(editCmd.getName()) ? editCmd.getName() : null)
                        .quota(editCmd.getQuota() == null ? null : String.valueOf(editCmd.getQuota()))
                        .password(editCmd.getPassword())
                        .password2(editCmd.getPassword2())
                        .tags(editCmd.getTags()).build();
        update.setAttr(attr);

        MailboxChangeResult changeResult = mailBoxExternalService.editMailBox(update);

        if (!changeResult.isSuccess()) {
            throw new ServiceException(changeResult.getMessage());
        }

        MailBox updateMailbox = new MailBox();
        updateMailbox.setEmail(editCmd.getEmail());
        updateMailbox.setQuota(editCmd.getQuota() == null ? null : editCmd.getQuota());
        updateMailbox.setName(StrUtil.isNotBlank(editCmd.getName()) ? editCmd.getName() : null);
        updateMailbox.setType(StrUtil.isNotBlank(editCmd.getMailboxType()) ? editCmd.getMailboxType() : null);

        if (editCmd.getUserId() != null) {
            User user = userService.getById(editCmd.getUserId());
            updateMailbox.setUserId(user.getUserId());
            updateMailbox.setEmployeeNo(user.getEmployeeNo());
            updateMailbox.setWxCpId(user.getWxCpId());
            updateMailbox.setWxUserId(user.getWxUserId());
        }

        mailBoxMapper.updateById(updateMailbox);

        if (MailConst.MailBoxType.PERSONAL.equals(mailBox.getType())) {
            if (StrUtil.isNotEmpty(mailBox.getWxUserId()) && StrUtil.isNotEmpty(mailBox.getWxCpId())) {
                // 发送容量变更的通知
                if (editCmd.getQuota() != null && !editCmd.getQuota().equals(mailBox.getQuota())) {
                    String content = StrUtil.format("您的邮箱 {} 容量已变更为 {} MB。", editCmd.getEmail(), editCmd.getQuota());
                    WxCpMessage msg = WxCpMessage.TEXTCARD().title("邮箱容量变更").description(content).url("https://mes.wxgreenstone.com/docs/%E9%82%AE%E7%AE%B1/%E6%96%B0%E5%91%98%E5%B7%A5/")
                            .btnTxt("使用方法").toUser(mailBox.getWxUserId()).agentId(wxCpProperties.getDefaultAgentId()).build();
                    try {
                        WxCpService wxCpService = wxcpService.getWxCpService(wxCpProperties.getDefaultAgentId());
                        wxCpService.getMessageService().send(msg);
                    } catch (WxErrorException e) {
                        throw new ServiceException(e.getMessage());
                    }
                }

                // 密码重置通知
                if (StrUtil.isNotBlank(editCmd.getPassword())) {
                    String content = StrUtil.format("您的邮箱 {} 密码已重置为 {}", editCmd.getEmail(), editCmd.getPassword());
                    WxCpMessage msg = WxCpMessage.TEXTCARD().title("邮箱密码重置成功").description(content).url("https://mes.wxgreenstone" +
                                    ".com/docs/%E9%82%AE%E7%AE%B1/%E6%96%B0%E5%91%98%E5%B7%A5/")
                            .btnTxt("使用方法").toUser(mailBox.getWxUserId()).agentId(wxCpProperties.getDefaultAgentId()).build();
                    try {
                        WxCpService wxCpService = wxcpService.getWxCpService(wxCpProperties.getDefaultAgentId());
                        wxCpService.getMessageService().send(msg);
                    } catch (WxErrorException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }

        return changeResult;
    }

    @Override
    public void syncMailBoxes() {
        log.info("邮箱同步：开始从邮件服务器同步邮箱信息到系统");
        DomainResult domain = mailBoxExternalService.getDomain(mailConfig.getDomain());

        List<MailboxOriginResult> mailBoxes = mailBoxExternalService.getMailBoxesByDomain(mailConfig.getDomain());
        log.info("邮箱同步：获取到 {} 个邮箱，开始同步", mailBoxes.size());
        for (MailboxOriginResult mailBoxR : mailBoxes) {
            syncMailBox(mailBoxR, domain);
        }
        log.info("邮箱同步：同步完成");
    }

    @Override
    public List<MailBox> getMailboxes(MailboxQuery mailboxQuery) {
        LambdaQueryWrapper<MailBox> query = Wrappers.lambdaQuery();
        query.like(StrUtil.isNotBlank(mailboxQuery.getEmail()), MailBox::getEmail, mailboxQuery.getEmail());
        query.eq(StrUtil.isNotBlank(mailboxQuery.getMailboxType()), MailBox::getType, mailboxQuery.getMailboxType());
        query.eq(mailboxQuery.getUserId() != null, MailBox::getUserId, mailboxQuery.getUserId());
        return mailBoxMapper.selectList(query);
    }

    private void syncMailBox(MailboxOriginResult mailBoxR) {
        syncMailBox(mailBoxR, null);
    }

    private void syncMailBox(MailboxOriginResult mailBoxR, DomainResult domain) {
        if (domain == null) {
            domain = mailBoxExternalService.getDomain(mailConfig.getDomain());
        }
        List<MailBox> mailBoxes = mailBoxMapper.selectWithOutLogic(Wrappers.query(MailBox.builder().email(mailBoxR.getUsername()).build()));
        log.info("邮箱同步：同步邮箱 {}", mailBoxR.getUsername());
        long gibQuota = mailBoxR.getQuota() == 0L ? domain.getMaxMibQuotaForMbox() : mailBoxR.getMibQuota().intValue();

        if (CollUtil.isEmpty(mailBoxes)) {
            User user = userService.getByMail(mailBoxR.getUsername());

            MailBox mailBox = new MailBox();
            mailBox.setEmail(mailBoxR.getUsername());
            mailBox.setName(mailBoxR.getName());
            mailBox.setQuota(gibQuota);
            mailBox.setQuotaUsed(mailBoxR.getMibQuotaUsed());
            mailBox.setPercentInUse(mailBoxR.getCalcPercentInUse());
            mailBox.setType(user == null ? "public" : "personal");
            if (user != null) {
                mailBox.setUserId(user.getUserId());
                mailBox.setNickName(user.getNickName());
                mailBox.setEmployeeNo(user.getEmployeeNo());
                mailBox.setWxCpId(user.getWxCpId());
                mailBox.setWxUserId(user.getWxUserId());
            }
            mailBoxMapper.insert(mailBox);
        } else {
            if (mailBoxes.get(0).getDeleted()){
                log.info("忽略已经删除的邮箱");
                return;
            }
            MailBox mailBox = new MailBox();
            mailBox.setEmail(mailBoxR.getUsername());
            mailBox.setQuota(gibQuota);
            mailBox.setQuotaUsed(mailBoxR.getMibQuotaUsed());
            mailBox.setPercentInUse(mailBoxR.getCalcPercentInUse());

            mailBoxMapper.updateById(mailBox);
        }
    }

}
