package com.greenstone.mes.market.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.ces.dto.cmd.AppStatusChangeCmd;
import com.greenstone.mes.common.core.enums.FormError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.dto.result.MailSendResult;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.mail.api.RemoteMailService;
import com.greenstone.mes.mail.cmd.MailAddress;
import com.greenstone.mes.mail.cmd.MailAttachment;
import com.greenstone.mes.mail.cmd.MailSendCmd;
import com.greenstone.mes.market.application.assembler.MarketAppAssembler;
import com.greenstone.mes.market.application.dto.MarketAppSaveCmd;
import com.greenstone.mes.market.application.dto.query.MarketAppFuzzyQuery;
import com.greenstone.mes.market.application.dto.result.MarketAppResult;
import com.greenstone.mes.market.application.service.MarketAppDataService;
import com.greenstone.mes.market.domain.entity.MarketApplication;
import com.greenstone.mes.market.domain.repository.MarketAppRepository;
import com.greenstone.mes.market.infrastructure.config.BuyerConfig;
import com.greenstone.mes.market.infrastructure.constant.MarketAppParam;
import com.greenstone.mes.office.ces.constant.UserParamKey;
import com.greenstone.mes.system.api.RemoteParamService;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.consts.BusinessKey;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.cmd.UserParamSaveCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import com.greenstone.mes.table.infrastructure.config.LinkConfig;
import com.greenstone.mes.workflow.cmd.FlowCommitCmd;
import com.greenstone.mes.workflow.mq.ApprovalChangeMsg;
import com.greenstone.mes.workflow.resp.FlowCommitResp;
import com.greenstone.mes.wxcp.api.RemoteWorkflowService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@Slf4j
@Service
public class MarketAppDataServiceImpl implements MarketAppDataService {

    private final MarketAppRepository marketAppRepository;
    private final MarketAppAssembler assembler;
    private final RemoteSystemService systemService;
    private final RemoteUserService userService;
    private final BuyerConfig buyerConfig;
    private final RemoteParamService paramService;
    private final RemoteMailService mailService;
    private final RemoteWorkflowService workflowService;
    private final LinkConfig linkConfig;


    @Override
    public MarketAppResult detail(String serialNo) {
        MarketApplication application = marketAppRepository.detail(serialNo);
        return assembler.toMarketAppResult(application);
    }

    @Override
    public List<MarketAppResult> list(MarketAppFuzzyQuery fuzzyQuery) {
        List<MarketApplication> applications = marketAppRepository.list(fuzzyQuery);
        return assembler.toMarketAppResults(applications);
    }

    @Override
    public void delete(List<String> serialNos) {
        marketAppRepository.delete(serialNos);
    }

    @Override
    public MarketAppResult getById(String id) {
        MarketApplication application = marketAppRepository.getById(id);
        return assembler.toMarketAppResult(application);
    }

    @Transactional
    @Override
    public void changeStatus(AppStatusChangeCmd statusChangeCmd) {
        log.info("Market purchase status change, {}", statusChangeCmd);
        if (statusChangeCmd.getStatus() == ProcessStatus.COMMITTED) {
            statusChangeCmd.setStatus(ProcessStatus.APPROVING);
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                MarketApplication appFound = marketAppRepository.get(serialNo);
                if (appFound == null) {
                    throw new ServiceException(FormError.E70101);
                }
                if (appFound.getStatus() != ProcessStatus.DRAFT) {
                    throw new ServiceException(FormError.E70102);
                }
                marketAppRepository.changeStatus(statusChangeCmd.getSerialNos(), statusChangeCmd.getStatus());
            }
        }
        if (statusChangeCmd.getStatus() != ProcessStatus.COMMITTED && statusChangeCmd.getStatus() != ProcessStatus.APPROVED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                MarketApplication appFound = marketAppRepository.get(serialNo);
                if (appFound == null) {
                    throw new ServiceException(FormError.E70101);
                }
            }
            marketAppRepository.changeStatus(statusChangeCmd.getSerialNos(), statusChangeCmd.getStatus());
        }

    }

    @Override
    public void approval(ApprovalChangeMsg approvalChangeMsg) {
        MarketApplication marketApplication = marketAppRepository.getBySpNo(approvalChangeMsg.getInstanceNo());
        if (approvalChangeMsg.getStatus() == ProcessStatus.FINISH || approvalChangeMsg.getStatus() == ProcessStatus.REJECTED) {
            marketApplication.setStatus(approvalChangeMsg.getStatus());
            marketApplication.setApproveTime(LocalDateTime.now());
            marketAppRepository.updateMarketApplication(marketApplication);
        }
        if (approvalChangeMsg.getStatus() == ProcessStatus.REVOKED) {
            marketApplication.setStatus(approvalChangeMsg.getStatus());
            marketAppRepository.updateMarketApplication(marketApplication);
        }
        // 审批完成发送邮件
        if (approvalChangeMsg.getStatus() == ProcessStatus.FINISH) {
            sendMail(marketApplication, approvalChangeMsg.getRemark() == null ? "无" : approvalChangeMsg.getRemark());
        }
    }

    @Override
    public void mailResult(MailSendResult mailSendResult) {
        if (StrUtil.isNotEmpty(mailSendResult.getErrorMsg()) && mailSendResult.getErrorMsg().length() > 255) {
            // 错误信息太长，截取中文部分
            mailSendResult.setErrorMsg(mailSendResult.getErrorMsg().substring(0, mailSendResult.getErrorMsg().indexOf(":")));
        }
        marketAppRepository.saveMailResult(mailSendResult);
    }

    @Transactional
    @Override
    public void saveDraft(MarketAppSaveCmd addCmd) {
        MarketApplication entity = assembler.fromMarketAppSaveCmd(addCmd);
        entityExistCheck(entity);
        entity.setStatus(ProcessStatus.DRAFT);
        beforeSaveDraft(entity);
        insertOrUpdateDraft(entity);
        afterSaveDraft(entity);
    }

    @Transactional
    @Override
    public void saveCommit(MarketAppSaveCmd editCmd) {
        MarketApplication entity = assembler.fromMarketAppSaveCmd(editCmd);
        entityExistCheck(entity);
        entity.setStatus(ProcessStatus.WAIT_APPROVE);
        beforeSaveCommit(entity);
        insertOrUpdateCommit(entity);
        afterSaveCommit(entity);
    }

    private void entityExistCheck(MarketApplication entity) {
        if (StrUtil.isNotEmpty(entity.getId())) {
            MarketApplication marketApp = marketAppRepository.getById(entity.getId());
            if (null == marketApp) {
                log.error("所选单据不存在，id: {}", entity.getId());
                throw new ServiceException(FormError.E70101);
            }
            if (marketApp.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(FormError.E70102);
            }
        }
    }

    public void sendMail(MarketApplication marketApplication, String approveRemark) {
        // 默认抄送给工艺部和生产部
        List<MailAddress> cc = defaultEmailCC();

        List<Long> copyTo = marketApplication.getCopyTo();
        for (Long userId : copyTo) {
            SysUser sysUser = userService.userinfo(userId);
            cc.add(new MailAddress(sysUser.getEmail(), sysUser.getNickName()));
        }
        SysUser approver = userService.userinfo(marketApplication.getApprovers().get(0));
        String title = StrUtil.format("【{} {}】{}", marketApplication.getCreateBy(), marketApplication.getSerialNo(), marketApplication.getTitle());
        // 添加审批时的备注
        String remark = StrFormatter.format("审批人：{} <br> 审批内容：{} <br><br>——————————————————————————————————<br><br>", approver.getNickName(), approveRemark);
        marketApplication.setContent(remark + marketApplication.getContent());

        cc = cc.stream().filter(address -> StrUtil.isNotBlank(address.getAddress())).toList();

        List<MailAttachment> mailAttachments = marketApplication.getAttachments().stream().map(a -> new MailAttachment(a.getName(), a.getPath())).toList();
        MailSendCmd mailSendCmd = MailSendCmd.builder().businessKey(BusinessKey.MARKET_APPLY).serialNo(marketApplication.getSerialNo()).subject(title).content(marketApplication.getContent()).to(List.of(new MailAddress(buyerConfig.getBuyer(), null))).cc(cc).attachments(mailAttachments).html(true).build();
        mailService.sendAsync(mailSendCmd);
    }

    public List<MailAddress> defaultEmailCC() {
        List<MailAddress> mailAddresses = new ArrayList<>();
        if (StrUtil.isNotEmpty(MarketAppParam.MARKET_APPLY_EMAIL_CC)) {
            String[] split = MarketAppParam.MARKET_APPLY_EMAIL_CC.split(";");
            for (String email : split) {
                mailAddresses.add(assembleMailAddress(email));
            }
        }
        return mailAddresses;
    }

    public MailAddress assembleMailAddress(String email) {
        String personal = null;
        if (email.contains("-")) {
            String[] emailWithName = email.split("-");
            email = emailWithName[0];
            personal = emailWithName[1];
        }
        return new MailAddress(email, personal);
    }


    public void beforeSaveDraft(MarketApplication entity) {
        entity.setAppliedBy(SecurityUtils.getUserId());
        entity.setAppliedByName(SecurityUtils.getLoginUser().getUser().getNickName());
        entity.setAppliedTime(LocalDateTime.now());

        entity.setDeptId(SecurityUtils.getLoginUser().getUser().getDeptId());

        if (StrUtil.isEmpty(entity.getSerialNo())) {
            SerialNoNextCmd nextCmd = SerialNoNextCmd.builder().type("purchase_application").prefix("PMA" + DateUtil.dateSerialStrNow()).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);
            entity.setSerialNo(serialNoR.getSerialNo());
        }
    }

    public void insertOrUpdateDraft(MarketApplication entity) {
        entity.getAttachments().forEach(a -> a.setSerialNo(entity.getSerialNo()));
        marketAppRepository.save(entity);
    }

    public void afterSaveDraft(MarketApplication entity) {
        saveUserParam(entity);
    }

    public void beforeSaveCommit(MarketApplication entity) {
        entity.setAppliedBy(SecurityUtils.getUserId());
        entity.setAppliedByName(SecurityUtils.getLoginUser().getUser().getNickName());
        entity.setAppliedTime(LocalDateTime.now());

        entity.setSubmitBy(SecurityUtils.getLoginUser().getUser().getNickName());
        entity.setSubmitById(SecurityUtils.getUserId());
        entity.setSubmitTime(LocalDateTime.now());
        entity.setDeptId(SecurityUtils.getLoginUser().getUser().getDeptId());

        if (StrUtil.isEmpty(entity.getSerialNo())) {
            SerialNoNextCmd nextCmd = SerialNoNextCmd.builder().type("purchase_application").prefix("PMA" + DateUtil.dateSerialStrNow()).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);
            entity.setSerialNo(serialNoR.getSerialNo());
        }
    }

    public void insertOrUpdateCommit(MarketApplication entity) {
        entity.getAttachments().forEach(a -> a.setSerialNo(entity.getSerialNo()));
        marketAppRepository.save(entity);
    }

    public void afterSaveCommit(MarketApplication entity) {
        saveUserParam(entity);
        // 发送企业微信审批
        sendWxApproval(entity);
    }


    private void saveUserParam(MarketApplication application) {
        UserParamSaveCmd approverParam = UserParamSaveCmd.builder().billType("purchase_application").paramKey(UserParamKey.approvers).paramValue(StrUtil.join(",", application.getApprovers())).build();
        UserParamSaveCmd copyParam = UserParamSaveCmd.builder().billType("purchase_application").paramKey(UserParamKey.copyTo).paramValue(StrUtil.join(",", application.getCopyTo())).build();
        List<UserParamSaveCmd> saveCmds = List.of(approverParam, copyParam);
        paramService.saveUserParam(saveCmds);
    }

    private void sendWxApproval(MarketApplication application) {
        FlowCommitCmd commitCmd = FlowCommitCmd.builder().businessKey(BusinessKey.MARKET_APPLY).applyUserId(application.getAppliedBy()).build();
        List<FlowCommitCmd.Attr> attrs = new ArrayList<>();
        commitCmd.setAttrs(attrs);
        attrs.add(FlowCommitCmd.Attr.builder().name("serialNo").value(application.getSerialNo()).build());
        attrs.add(FlowCommitCmd.Attr.builder().name("expectDay").value(LocalDateTimeUtil.format(application.getExpectedArrivalTime(), "yyyy-MM-dd")).build());
        attrs.add(FlowCommitCmd.Attr.builder().name("title").value(application.getTitle()).build());
        attrs.add(FlowCommitCmd.Attr.builder().name("approverId").value(CollUtil.join(application.getApprovers(), ",")).build());
        attrs.add(FlowCommitCmd.Attr.builder().name("copyUserIds").value(CollUtil.join(application.getCopyTo(), ",")).build());
        String detailLink = linkConfig.getDetailLink("100000082", application.getId());
        attrs.add(FlowCommitCmd.Attr.builder().name("detailLink").value(detailLink).build());
        attrs.add(FlowCommitCmd.Attr.builder().name("attachment").value(application.getAttachments().get(0).getPath()).build());
        attrs.add(FlowCommitCmd.Attr.builder().name("id").value(application.getId()).build());

        try {
            log.debug("提交企业微信审批: {}", commitCmd);
            FlowCommitResp commitResp = workflowService.commit(commitCmd);
            // 保存审批编号
            application.setSpNo(commitResp.getInstanceNo());
            log.info("审批提交成功, 审批编号: " + commitResp.getInstanceNo());
        } catch (Exception e) {
            throw new RuntimeException("提交企业微信审批失败", e);
        }
        marketAppRepository.updateMarketApplication(application);
    }
}
