package com.greenstone.mes.ces.application.service.impl;

import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.ces.application.assembler.WarehouseInAssembler;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseIORemoveCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseInAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseInEditCmd;
import com.greenstone.mes.ces.application.dto.event.WarehouseUpdateE;
import com.greenstone.mes.ces.application.dto.query.WarehouseIOFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.WarehouseInResult;
import com.greenstone.mes.ces.application.event.WarehouseStockEvent;
import com.greenstone.mes.ces.application.service.WarehouseInService;
import com.greenstone.mes.ces.domain.entity.WarehouseIn;
import com.greenstone.mes.ces.domain.entity.WarehouseInDetail;
import com.greenstone.mes.ces.domain.repository.ItemSpecRepository;
import com.greenstone.mes.ces.domain.repository.WarehouseInRepository;
import com.greenstone.mes.ces.dto.cmd.WarehouseIOStatusChangeCmd;
import com.greenstone.mes.common.core.enums.FormError;
import com.greenstone.mes.common.core.enums.WarehouseIOError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.application.service.ProcessInstanceService;
import com.greenstone.mes.external.dto.cmd.ProcessStartCmd;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.form.dto.cmd.ProcessResult;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class WarehouseInServiceImpl implements WarehouseInService {

    private final WarehouseInRepository warehouseInRepository;
    private final WarehouseInAssembler assembler;
    private final RemoteSystemService systemService;
    private final ProcessInstanceService flowService;
    private final ApplicationEventPublisher eventPublisher;
    private final ItemSpecRepository itemSpecRepository;

    @Override
    public List<WarehouseInResult> list(WarehouseIOFuzzyQuery query) {
        log.info("WarehouseIOFuzzyQuery params:{}", query);
        List<WarehouseIn> warehouseIns = warehouseInRepository.list(query);
        return assembler.toWarehouseInRs(warehouseIns);
    }

    @Override
    public WarehouseInResult detail(String serialNo) {
        log.info("detail params:{}", serialNo);
        WarehouseIn warehouseIn = warehouseInRepository.detail(serialNo);
        return assembler.toWarehouseInR(warehouseIn);
    }

    @Override
    public void add(WarehouseInAddCmd addCmd) {
        log.info("WarehouseInAddCmd params:{}", addCmd);
        WarehouseIn warehouseIn = assembler.toWarehouseIn(addCmd);
        SerialNoNextCmd nextCmd =
                SerialNoNextCmd.builder().type("purchase_warehouse_in").prefix("PWI" + DateUtil.dateSerialStrNow()).build();
        SerialNoR serialNoR = systemService.getNextSn(nextCmd);

        warehouseIn.setSerialNo(serialNoR.getSerialNo());
        // 由收货单自动创建的不设置状态
        if (!addCmd.isAutoCreate()) {
            warehouseIn.setStatus(addCmd.isCommit() ? ProcessStatus.COMMITTED : ProcessStatus.DRAFT);
        }
        warehouseIn.setSponsorId(SecurityUtils.getLoginUser().getUser().getUserId());
        warehouseIn.setSponsorName(SecurityUtils.getLoginUser().getUser().getNickName());
        // 校验：去除自定义物品，只能选物品档案中物品
        for (WarehouseInDetail item : warehouseIn.getItems()) {
            if (!itemSpecRepository.existByItemCode(item.getItemCode())) {
                throw new ServiceException(FormError.E70105);
            }
        }
        log.info("add WarehouseIn params:{}", warehouseIn);
        warehouseInRepository.add(warehouseIn);

        if (addCmd.isCommit()) {
            ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("purchase_warehouse_in").serialNo(serialNoR.getSerialNo()).build();
            log.info("commit params:{}", startCmd);
            flowService.createAndRun(startCmd);
        }
        if (addCmd.isAutoCreate()) {
            // 库存记录
            eventPublisher.publishEvent(new WarehouseStockEvent(assembler.toWarehouseInStockEvent(warehouseIn)));
        }
    }

    @Transactional
    @Override
    public void edit(WarehouseInEditCmd editCmd) {
        log.info("WarehouseInEditCmd params:{}", editCmd);
        WarehouseIn warehouseInFound = warehouseInRepository.get(editCmd.getSerialNo());
        if (warehouseInFound == null) {
            throw new ServiceException(WarehouseIOError.E120101);
        }
        if (warehouseInFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(WarehouseIOError.E120102);
        }

        if (editCmd.isCommit()) {
            ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("purchase_warehouse_in").serialNo(editCmd.getSerialNo()).build();
            flowService.createAndRun(startCmd);
        }
        WarehouseIn warehouseIn = assembler.toWarehouseIn(editCmd);
        warehouseIn.setStatus(editCmd.isCommit() ? ProcessStatus.COMMITTED : ProcessStatus.DRAFT);
        for (WarehouseInDetail item : warehouseIn.getItems()) {
            item.setSerialNo(editCmd.getSerialNo());
            if (!itemSpecRepository.existByItemCode(item.getItemCode())) {
                throw new ServiceException(FormError.E70105);
            }
        }
        log.info("edit WarehouseIn params:{}", warehouseIn);
        warehouseInRepository.edit(warehouseIn);
    }

    @Override
    public void statusChange(WarehouseIOStatusChangeCmd statusChangeCmd) {
        log.info("WarehouseIOStatusChangeCmd params:{}", statusChangeCmd);
        if (statusChangeCmd.getStatus() == ProcessStatus.COMMITTED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                WarehouseIn warehouseInFound = warehouseInRepository.get(serialNo);
                if (warehouseInFound == null) {
                    throw new ServiceException(WarehouseIOError.E120101);
                }
                if (warehouseInFound.getStatus() != ProcessStatus.DRAFT) {
                    throw new ServiceException(WarehouseIOError.E120102);
                }
                warehouseInRepository.changeStatus(WarehouseIn.builder().status(statusChangeCmd.getStatus()).serialNo(serialNo).build());
                ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("purchase_warehouse_in").serialNo(serialNo).build();
                flowService.createAndRun(startCmd);
            }
        } else if (statusChangeCmd.getStatus() == ProcessStatus.CLOSED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                WarehouseIn warehouseInFound = warehouseInRepository.get(serialNo);
                if (warehouseInFound == null) {
                    throw new ServiceException(WarehouseIOError.E120101);
                }
                if (warehouseInFound.getStatus() != ProcessStatus.APPROVED) {
                    throw new ServiceException(WarehouseIOError.E120103);
                }
            }
            warehouseInRepository.statusChange(statusChangeCmd);
        } else if (statusChangeCmd.getStatus() == ProcessStatus.APPROVED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                WarehouseIn warehouseInFound = warehouseInRepository.detail(serialNo);
                if (warehouseInFound == null) {
                    throw new ServiceException(WarehouseIOError.E120101);
                }
                warehouseInRepository.changeStatus(WarehouseIn.builder().status(statusChangeCmd.getStatus()).serialNo(serialNo).build());
                // 防止多次点击
                if (warehouseInFound.getStatus() != ProcessStatus.APPROVED) {
                    // 库存记录
                    eventPublisher.publishEvent(new WarehouseStockEvent(assembler.toWarehouseInStockEvent(warehouseInFound)));
                }
            }
        } else {
            warehouseInRepository.statusChange(statusChangeCmd);
        }
    }

    @Transactional
    @Override
    public void remove(WarehouseIORemoveCmd removeCmd) {
        log.info("WarehouseIORemoveCmd params:{}", removeCmd);
        warehouseInRepository.remove(removeCmd.getSerialNos());
    }

    @Override
    public void warehouseUpdateEvent(WarehouseUpdateE eventData) {
        log.info("WarehouseUpdateE params:{}", eventData);
        warehouseInRepository.moveTo(eventData.getFromWarehouseCode(), eventData.getToWarehouseCode());
    }

    @Override
    public void approved(ProcessResult processResult) {
        WarehouseIn changeCmd = WarehouseIn.builder().serialNo(processResult.getSerialNo())
                .status(ProcessStatus.APPROVED).build();
        warehouseInRepository.changeStatus(changeCmd);
    }

}
