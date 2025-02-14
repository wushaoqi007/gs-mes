package com.greenstone.mes.ces.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteOaService;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.ces.application.assembler.ApplicationAssembler;
import com.greenstone.mes.ces.application.dto.cmd.ApplicationRemoveCmd;
import com.greenstone.mes.ces.application.dto.cmd.CesApplicationAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.CesApplicationEditCmd;
import com.greenstone.mes.ces.application.dto.event.OrderAddE;
import com.greenstone.mes.ces.application.dto.event.ReceiptAddE;
import com.greenstone.mes.ces.application.dto.query.ApplicationFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.CesApplicationResult;
import com.greenstone.mes.ces.application.dto.result.CesApplicationWaitHandleResult;
import com.greenstone.mes.ces.application.service.CesApplicationService;
import com.greenstone.mes.ces.domain.entity.CesApplication;
import com.greenstone.mes.ces.domain.entity.CesApplicationItem;
import com.greenstone.mes.ces.domain.repository.CesApplicationRepository;
import com.greenstone.mes.ces.domain.repository.ItemSpecRepository;
import com.greenstone.mes.ces.dto.cmd.AppNoticeCmd;
import com.greenstone.mes.ces.dto.cmd.AppStatusChangeCmd;
import com.greenstone.mes.ces.dto.cmd.StateChangeCmd;
import com.greenstone.mes.common.core.enums.FormError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.application.service.ProcessInstanceService;
import com.greenstone.mes.external.dto.cmd.ProcessStartCmd;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.form.dto.cmd.ProcessResult;
import com.greenstone.mes.oa.request.WxMsgSendCmd;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/22 8:08
 */
@Slf4j
@Service
@AllArgsConstructor
public class CesApplicationServiceImpl implements CesApplicationService {

    private final CesApplicationRepository applicationRepository;
    private final ApplicationAssembler assembler;
    private final RemoteSystemService systemService;
    private final ProcessInstanceService flowService;
    private final RemoteOaService oaService;
    private final ItemSpecRepository itemSpecRepository;

    @Override
    public void add(CesApplicationAddCmd addCmd) {
        log.info("CesApplicationAddCmd params:{}", addCmd);
        CesApplication application = assembler.toCesApplication(addCmd);
        SerialNoNextCmd nextCmd =
                SerialNoNextCmd.builder().type("purchase_consumable_application").prefix("PCA" + DateUtil.dateSerialStrNow()).build();
        SerialNoR serialNoR = systemService.getNextSn(nextCmd);

        application.setSerialNo(serialNoR.getSerialNo());
        application.setStatus(addCmd.isCommit() ? ProcessStatus.APPROVING : ProcessStatus.DRAFT);
        application.setAppliedBy(SecurityUtils.getLoginUser().getUser().getUserId());
        application.setAppliedByName(SecurityUtils.getLoginUser().getUser().getNickName());
        application.setAppliedTime(LocalDateTime.now());
        // 校验：去除自定义物品，只能选物品档案中物品
        for (CesApplicationItem item : application.getItems()) {
            if (!itemSpecRepository.existByItemCode(item.getItemCode())) {
                throw new ServiceException(FormError.E70105);
            }
        }
        log.info("add CesApplication params:{}", application);
        applicationRepository.add(application);

        if (addCmd.isCommit()) {
            ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("consumable").serialNo(serialNoR.getSerialNo()).build();
            log.info("commit ProcessStartCmd params:{}", startCmd);
            flowService.createAndRun(startCmd);
        }
    }

    @Transactional
    @Override
    public void edit(CesApplicationEditCmd editCmd) {
        log.info("CesApplicationEditCmd params:{}", editCmd);
        CesApplication appFound = applicationRepository.get(editCmd.getSerialNo());
        if (appFound == null) {
            throw new ServiceException(FormError.E70101);
        }
        if (appFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(FormError.E70102);
        }

        if (editCmd.isCommit()) {
            ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("consumable").serialNo(editCmd.getSerialNo()).build();
            log.info("commit ProcessStartCmd params:{}", startCmd);
            flowService.createAndRun(startCmd);
        }
        CesApplication application = assembler.toCesApplication(editCmd);
        application.setStatus(editCmd.isCommit() ? ProcessStatus.APPROVING : ProcessStatus.DRAFT);
        for (CesApplicationItem item : application.getItems()) {
            item.setSerialNo(editCmd.getSerialNo());
            if (!itemSpecRepository.existByItemCode(item.getItemCode())) {
                throw new ServiceException(FormError.E70105);
            }
        }
        log.info("edit CesApplication params:{}", application);
        applicationRepository.edit(application);
    }

    @Override
    public void statusChange(AppStatusChangeCmd statusChangeCmd) {
        log.info("AppStatusChangeCmd params:{}", statusChangeCmd);
        if (statusChangeCmd.getStatus() == ProcessStatus.APPROVING) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                CesApplication appFound = applicationRepository.get(serialNo);
                if (appFound == null) {
                    throw new ServiceException(FormError.E70101);
                }
                if (appFound.getStatus() != ProcessStatus.DRAFT) {
                    throw new ServiceException(FormError.E70102);
                }
                applicationRepository.changeStatus(CesApplication.builder().status(statusChangeCmd.getStatus()).serialNo(serialNo).build());
                ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("consumable").serialNo(serialNo).build();
                flowService.createAndRun(startCmd);
            }
        } else if (statusChangeCmd.getStatus() == ProcessStatus.CLOSED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                CesApplication appFound = applicationRepository.get(serialNo);
                if (appFound == null) {
                    throw new ServiceException(FormError.E70101);
                }
                if (!(appFound.getStatus() == ProcessStatus.APPROVED || appFound.getStatus() == ProcessStatus.ISSUED)) {
                    throw new ServiceException(FormError.E70103);
                }
            }
            applicationRepository.statusChange(statusChangeCmd);
        } else {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                CesApplication appFound = applicationRepository.get(serialNo);
                if (appFound == null) {
                    throw new ServiceException(FormError.E70101);
                }
            }
            applicationRepository.statusChange(statusChangeCmd);
        }
    }

    @Transactional
    @Override
    public void remove(ApplicationRemoveCmd removeCmd) {
        log.info("ApplicationRemoveCmd params:{}", removeCmd);
        applicationRepository.remove(removeCmd.getSerialNos());
    }

    @Override
    public List<CesApplicationResult> list(ApplicationFuzzyQuery query) {
        log.info("ApplicationFuzzyQuery params:{}", query);
        List<CesApplication> applications = applicationRepository.list(query);
        return assembler.toCesApplicationListRs(applications);
    }

    @Override
    public List<CesApplicationWaitHandleResult> waitHandle(ApplicationFuzzyQuery query) {
        log.info("ApplicationFuzzyQuery params:{}", query);
        List<CesApplication> applications = applicationRepository.waitHandle(query);
        return assembler.toCesApplicationWaitHandleListRs(applications);
    }

    @Override
    public void approved(ProcessResult processResult) {
        log.info("FormDataApprovedCmd params:{}", processResult);
        applicationRepository.statusChange(AppStatusChangeCmd.builder().serialNos(List.of(processResult.getSerialNo()))
                .status(ProcessStatus.APPROVED).build());
    }

    @Override
    public void changeState(StateChangeCmd changeCmd) {
        log.info("StateChangeCmd params:{}", changeCmd);
        CesApplication changeEntity = CesApplication.builder().serialNo(changeCmd.getSerialNo()).status(changeCmd.getState()).build();
        applicationRepository.changeStatus(changeEntity);
    }

    @Override
    public CesApplicationResult detail(String serialNo) {
        log.info("detail params:{}", serialNo);
        CesApplication application = applicationRepository.detail(serialNo);
        return assembler.toCesApplicationListR(application);
    }

    @Override
    public void orderAddEvent(OrderAddE eventData) {
        log.info("OrderAddE params:{}", eventData);
        applicationRepository.updateNum(assembler.toCesApplicationFromOrderE(eventData, eventData.getItems()));
    }

    @Override
    public void receiptAddEvent(ReceiptAddE eventData) {
        log.info("ReceiptAddE params:{}", eventData);
        CesApplication needNotice = applicationRepository.updateNum(assembler.toCesApplicationFromReceiptE(eventData, eventData.getItems()));
        // 齐货后自动企业微信通知
        if (needNotice != null && CollUtil.isNotEmpty(needNotice.getItems())) {
            StringBuilder content = new StringBuilder(StrUtil.format("【到货通知】您的物品申请已到货，取件单号为{}。具体有以下物品到货：\r\n", needNotice.getSerialNo()));
            for (CesApplicationItem item : needNotice.getItems()) {
                content.append(StrUtil.format("{}，到货数量：{};\r\n",
                        item.getItemName(), item.getReadyNum()));
            }
            WxMsgSendCmd msgSendCmd = WxMsgSendCmd.builder()
                    .toUser(List.of(WxMsgSendCmd.WxMsgUser.builder().sysUserId(needNotice.getAppliedBy()).build()))
                    .content(content.toString()).build();
            oaService.sendMsgToWx(msgSendCmd);
        }
    }

    @Override
    public void notice(AppNoticeCmd noticeCmd) {
        for (String serialNo : noticeCmd.getSerialNos()) {
            CesApplication appFound = applicationRepository.get(serialNo);
            if (appFound == null) {
                throw new ServiceException(FormError.E70101, serialNo);
            }
            String content = StrUtil.format("【到货通知】您的物品申请已到货，取件单号为{}", appFound.getSerialNo());
            WxMsgSendCmd msgSendCmd = WxMsgSendCmd.builder()
                    .toUser(List.of(WxMsgSendCmd.WxMsgUser.builder().sysUserId(appFound.getAppliedBy()).build()))
                    .content(content).build();
            oaService.sendMsgToWx(msgSendCmd);
        }

    }

}
