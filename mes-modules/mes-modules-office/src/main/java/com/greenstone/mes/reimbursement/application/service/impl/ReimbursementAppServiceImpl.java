package com.greenstone.mes.reimbursement.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.ces.dto.cmd.AppStatusChangeCmd;
import com.greenstone.mes.common.core.enums.FormError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.application.service.ProcessInstanceService;
import com.greenstone.mes.external.dto.result.ProcessRunResult;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.wxcp.domain.helper.WxMsgService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.form.domain.helper.FormHelper;
import com.greenstone.mes.form.domain.service.AbstractFormDataService;
import com.greenstone.mes.form.dto.query.FormDataQuery;
import com.greenstone.mes.form.infrastructure.annotation.FormService;
import com.greenstone.mes.reimbursement.application.assembler.ReimbursementAppAssembler;
import com.greenstone.mes.reimbursement.application.dto.ReimbursementAppFuzzyQuery;
import com.greenstone.mes.reimbursement.application.dto.result.ReimbursementAppResult;
import com.greenstone.mes.reimbursement.application.helper.ReimbursementHelper;
import com.greenstone.mes.reimbursement.application.service.ReimbursementAppService;
import com.greenstone.mes.reimbursement.domain.entity.ReimbursementApplication;
import com.greenstone.mes.reimbursement.domain.repository.ReimbursementAppRepository;
import com.greenstone.mes.reimbursement.infrastructure.mapper.ReimbursementApplicationMapper;
import com.greenstone.mes.reimbursement.infrastructure.persistence.ReimbursementAppDO;
import com.greenstone.mes.system.api.RemoteParamService;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.domain.BillParam;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import com.greenstone.mes.wxcp.infrastructure.config.WxConfig;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.templatecard.HorizontalContent;
import me.chanjar.weixin.cp.bean.templatecard.TemplateCardButton;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@FormService("reimbursement_application")
@Slf4j
@Service
public class ReimbursementAppServiceImpl extends AbstractFormDataService<ReimbursementApplication, ReimbursementAppDO, ReimbursementApplicationMapper> implements ReimbursementAppService {

    private final ReimbursementAppRepository reimbursementAppRepository;
    private final ReimbursementAppAssembler assembler;
    private final RemoteSystemService systemService;
    private final RemoteUserService userService;
    private final RemoteParamService paramService;
    private final WxMsgService wxMsgService;
    private final WxConfig wxConfig;
    private final ReimbursementHelper reimbursementHelper;

    public ReimbursementAppServiceImpl(ReimbursementAppRepository reimbursementAppRepository, ReimbursementAppAssembler assembler,
                                       FormHelper formHelper, ProcessInstanceService processInstanceService,
                                       RemoteSystemService systemService, RemoteUserService userService,
                                       RemoteParamService paramService, WxMsgService wxMsgService,
                                       WxConfig wxConfig, ReimbursementHelper reimbursementHelper) {
        super(reimbursementAppRepository, processInstanceService, formHelper);
        this.reimbursementAppRepository = reimbursementAppRepository;
        this.assembler = assembler;
        this.systemService = systemService;
        this.userService = userService;
        this.paramService = paramService;
        this.wxMsgService = wxMsgService;
        this.wxConfig = wxConfig;
        this.reimbursementHelper = reimbursementHelper;
    }

    @Override
    public ReimbursementAppResult detail(String serialNo) {
        ReimbursementApplication application = reimbursementAppRepository.detail(serialNo);
        return assembler.toReimbursementAppResult(application);
    }

    @Override
    public List<ReimbursementAppResult> list(ReimbursementAppFuzzyQuery fuzzyQuery) {
        List<ReimbursementApplication> applications = reimbursementAppRepository.list(fuzzyQuery);
        return assembler.toReimbursementAppResults(applications);
    }

    @Override
    public void delete(List<String> serialNos) {
        reimbursementAppRepository.delete(serialNos);
    }

    @Transactional
    @Override
    public void changeStatus(AppStatusChangeCmd statusChangeCmd) {
        log.info("Reimbursement Application status change, {}", statusChangeCmd);
        if (statusChangeCmd.getStatus() == ProcessStatus.COMMITTED) {
            statusChangeCmd.setStatus(ProcessStatus.COMMITTED);
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                ReimbursementApplication appFound = reimbursementAppRepository.get(serialNo);
                if (appFound == null) {
                    throw new ServiceException(FormError.E70101);
                }
                if (appFound.getStatus() != ProcessStatus.DRAFT) {
                    throw new ServiceException(FormError.E70102);
                }
            }
            reimbursementAppRepository.changeStatus(statusChangeCmd.getSerialNos(), statusChangeCmd.getStatus());
        } else if (statusChangeCmd.getStatus() == ProcessStatus.APPROVED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                ReimbursementApplication appFound = reimbursementAppRepository.get(serialNo);
                if (appFound == null) {
                    throw new ServiceException(FormError.E70101);
                }
                if (appFound.getStatus() != ProcessStatus.COMMITTED) {
                    throw new ServiceException(FormError.E70104);
                }
                appFound.setApprovedTime(LocalDateTime.now());
                appFound.setStatus(ProcessStatus.APPROVED);
                reimbursementAppRepository.updateById(appFound);
                // 发送通知卡片
                sendMsg(appFound);
            }
        } else if (statusChangeCmd.getStatus() == ProcessStatus.REJECTED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                ReimbursementApplication appFound = reimbursementAppRepository.get(serialNo);
                if (appFound == null) {
                    throw new ServiceException(FormError.E70101);
                }
                if (appFound.getStatus() != ProcessStatus.COMMITTED) {
                    throw new ServiceException(FormError.E70104);
                }
                appFound.setStatus(ProcessStatus.REJECTED);
                appFound.setApprovedTime(LocalDateTime.now());
                reimbursementAppRepository.updateById(appFound);
                // 发送通知卡片
                sendMsg(appFound);
            }
        } else {
            throw new ServiceException("不允许此操作");
        }
    }

    @Override
    public void afterApproved(ProcessRunResult processResult) {
        log.info("发送报销通过通知卡片");
    }

    @Override
    public void afterRejected(ProcessRunResult processResult) {
        log.info("发送报销驳回通知卡片");
    }


    @Override
    public List<ReimbursementApplication> query(FormDataQuery query) {
        return null;
    }

    @Override
    public void beforeSaveDraft(ReimbursementApplication entity) {
        checkApproveBy(entity.getApprovedById(), entity.getApprovedBy());
        entity.setAppliedTime(LocalDateTime.now());
        entity.setAppliedBy(SecurityUtils.getLoginUser().getUser().getNickName());
        entity.setAppliedById(SecurityUtils.getUserId());
        entity.setAppliedByNo(SecurityUtils.getLoginUser().getUser().getEmployeeNo());

        if (StrUtil.isEmpty(entity.getSerialNo())) {
            SerialNoNextCmd nextCmd = SerialNoNextCmd.builder().type("reimbursement_application").prefix("RBA" + DateUtil.dateSerialStrNow()).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);
            entity.setSerialNo(serialNoR.getSerialNo());
        }
    }

    @Override
    public ReimbursementApplication insertOrUpdateDraft(ReimbursementApplication entity) {
        reimbursementAppRepository.save(entity);
        return null;
    }

    @Override
    public void afterSaveDraft(ReimbursementApplication entity) {
        log.info("保存草稿后暂无操作");
    }

    public void checkApproveBy(Long userId, String userName) {
        SysUser userinfo = userService.userinfo(userId);
        if (Objects.isNull(userinfo)) {
            throw new ServiceException(StrUtil.format("报销申请:审批人不存在，姓名：{}，id：{}", userName, userId));
        }
        List<BillParam> billParams = paramService.list("reimbursement_application");
        if (CollUtil.isEmpty(billParams)) {
            throw new ServiceException(StrUtil.format("报销申请:审批人范围未配置，billType : reimbursement_application"));
        }
        List<Long> approveBys = StrUtil.split(billParams.get(0).getParamValue(), ',', -1, true, Long::valueOf);
        if (CollUtil.isEmpty(approveBys) || !approveBys.contains(userId)) {
            throw new ServiceException(StrUtil.format("报销申请:未配置该审批人，姓名：{}，id:{}", userName, userId));
        }
    }

    @Override
    public void beforeSaveCommit(ReimbursementApplication entity) {
        checkApproveBy(entity.getApprovedById(), entity.getApprovedBy());
        entity.setAppliedTime(LocalDateTime.now());
        entity.setAppliedBy(SecurityUtils.getLoginUser().getUser().getNickName());
        entity.setAppliedById(SecurityUtils.getUserId());
        entity.setAppliedByNo(SecurityUtils.getLoginUser().getUser().getEmployeeNo());

        entity.setSubmitTime(LocalDateTime.now());
        entity.setSubmitBy(SecurityUtils.getLoginUser().getUser().getNickName());
        entity.setSubmitById(SecurityUtils.getUserId());

        if (StrUtil.isEmpty(entity.getSerialNo())) {
            SerialNoNextCmd nextCmd = SerialNoNextCmd.builder().type("reimbursement_application").prefix("RBA" + DateUtil.dateSerialStrNow()).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);
            entity.setSerialNo(serialNoR.getSerialNo());
        }
    }

    @Override
    public ReimbursementApplication insertOrUpdateCommit(ReimbursementApplication entity) {
        reimbursementAppRepository.save(entity);
        return entity;
    }

    @Override
    public void afterSaveCommit(ReimbursementApplication entity) {
        // 发生审批卡片
        SysUser user = userService.getUser(SysUser.builder().userId(entity.getApprovedById()).build());
        String mainTitleTitle = reimbursementHelper.getMainTitleTitle(entity.getApprovedBy());
        String taskId = reimbursementHelper.genTaskId(entity.getSerialNo());
        TemplateCardButton button1 = TemplateCardButton.builder().text("驳回").style(3).key("reject").build();
        TemplateCardButton button2 = TemplateCardButton.builder().text("同意").style(2).key("approve").build();
        List<HorizontalContent> contents = reimbursementHelper.getContents(entity.getType().getName(), entity.getReason(), entity.getTotal());
        // 组装企业微信消息
        WxCpMessage msg = WxCpMessage.TEMPLATECARD().toUser(user.getWxUserId()).agentId(wxConfig.getAgentId(user.getMainWxcpId(), WxConfig.SYSTEM))
                .cardType(WxConsts.TemplateCardType.BUTTON_INTERACTION).mainTitleTitle(mainTitleTitle).taskId(taskId)
                .buttons(List.of(button1, button2)).horizontalContents(contents).cardActionType(1).cardActionUrl(reimbursementHelper.getDetailUrl()).build();
        log.info("审批消息：{}", msg);
        wxMsgService.sendMsg(new CpId(user.getMainWxcpId()), wxConfig.getAgentId(user.getMainWxcpId(), WxConfig.SYSTEM), msg);
    }

    @Override
    public void startProcess(ReimbursementApplication entity) {
        log.info("不启用流程");
    }

    public void sendMsg(ReimbursementApplication appFound) {
        // 发送通知卡片
        SysUser user = userService.getUser(SysUser.builder().userId(appFound.getAppliedById()).build());
        String title = reimbursementHelper.getTitle(appFound.getStatus(), appFound.getApprovedBy());
        String description = reimbursementHelper.getDescription(appFound.getType().getName(), appFound.getReason(), appFound.getTotal());
        String url = reimbursementHelper.getDetailUrl();
        String btnTxt = reimbursementHelper.getBtnTxt();
        // 组装企业微信消息
        WxCpMessage msg = WxCpMessage.TEXTCARD().toUser(user.getWxUserId()).url(url).title(title).description(description).btnTxt(btnTxt).build();
        log.info("审批消息：{}", msg);
        wxMsgService.sendMsg(new CpId(user.getMainWxcpId()), wxConfig.getAgentId(user.getMainWxcpId(), WxConfig.SYSTEM), msg);
    }
}
