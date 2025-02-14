package com.greenstone.mes.ces.application.service.impl;

import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.ces.application.assembler.WarehouseOutAssembler;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseIORemoveCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseOutAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseOutEditCmd;
import com.greenstone.mes.ces.application.dto.event.WarehouseUpdateE;
import com.greenstone.mes.ces.application.dto.query.WarehouseIOFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.WarehouseOutResult;
import com.greenstone.mes.ces.application.event.WarehouseStockEvent;
import com.greenstone.mes.ces.application.service.WarehouseOutService;
import com.greenstone.mes.ces.domain.entity.WarehouseOut;
import com.greenstone.mes.ces.domain.entity.WarehouseOutDetail;
import com.greenstone.mes.ces.domain.entity.WarehouseStock;
import com.greenstone.mes.ces.domain.repository.WarehouseOutRepository;
import com.greenstone.mes.ces.domain.repository.WarehouseStockRepository;
import com.greenstone.mes.ces.dto.cmd.WarehouseIOStatusChangeCmd;
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
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class WarehouseOutServiceImpl implements WarehouseOutService {

    private final WarehouseOutRepository warehouseOutRepository;
    private final WarehouseOutAssembler assembler;
    private final RemoteSystemService systemService;
    private final ProcessInstanceService flowService;
    private final ApplicationEventPublisher eventPublisher;
    private final WarehouseStockRepository stockRepository;

    @Override
    public List<WarehouseOutResult> list(WarehouseIOFuzzyQuery query) {
        log.info("WarehouseIOFuzzyQuery params:{}", query);
        List<WarehouseOut> warehouseOuts = warehouseOutRepository.list(query);
        return assembler.toWarehouseOutRs(warehouseOuts);
    }

    @Override
    public WarehouseOutResult detail(String serialNo) {
        log.info("detail params:{}", serialNo);
        WarehouseOut warehouseOut = warehouseOutRepository.detail(serialNo);
        return assembler.toWarehouseOutR(warehouseOut);
    }

    @Override
    public void add(WarehouseOutAddCmd addCmd) {
        log.info("WarehouseOutAddCmd params:{}", addCmd);
        WarehouseOut warehouseOut = assembler.toWarehouseOut(addCmd);
        check(warehouseOut);

        SerialNoNextCmd nextCmd =
                SerialNoNextCmd.builder().type("purchase_warehouse_out").prefix("PWO" + DateUtil.dateSerialStrNow()).build();
        SerialNoR serialNoR = systemService.getNextSn(nextCmd);

        warehouseOut.setSerialNo(serialNoR.getSerialNo());
        warehouseOut.setStatus(addCmd.isCommit() ? ProcessStatus.COMMITTED : ProcessStatus.DRAFT);
        warehouseOut.setSponsorId(SecurityUtils.getLoginUser().getUser().getUserId());
        warehouseOut.setSponsorName(SecurityUtils.getLoginUser().getUser().getNickName());
        log.info("add WarehouseOut params:{}", warehouseOut);
        warehouseOutRepository.add(warehouseOut);

        if (addCmd.isCommit()) {
            ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("purchase_warehouse_out").serialNo(serialNoR.getSerialNo()).build();
            log.info("commit params:{}", startCmd);
            flowService.createAndRun(startCmd);
        }

        if (addCmd.isAutoCreate()) {
            // 库存记录
            eventPublisher.publishEvent(new WarehouseStockEvent(assembler.toWarehouseOutStockEvent(warehouseOut)));
        }
    }

    public void check(WarehouseOut warehouseOut) {
        for (WarehouseOutDetail item : warehouseOut.getItems()) {
            // 校验：库存检查，不能出库存不足的物品
            WarehouseStock stock = stockRepository.getByWarehouseAndItem(warehouseOut.getWarehouseCode(), item.getItemCode());
            if (Objects.isNull(stock) || stock.getNumber() < item.getOutStockNum()) {
                throw new ServiceException(WarehouseIOError.E120105);
            }
        }
    }

    @Transactional
    @Override
    public void edit(WarehouseOutEditCmd editCmd) {
        log.info("WarehouseOutEditCmd params:{}", editCmd);
        WarehouseOut warehouseOutFound = warehouseOutRepository.get(editCmd.getSerialNo());
        if (warehouseOutFound == null) {
            throw new ServiceException(WarehouseIOError.E120101);
        }
        if (warehouseOutFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(WarehouseIOError.E120102);
        }

        if (editCmd.isCommit()) {
            ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("purchase_warehouse_out").serialNo(editCmd.getSerialNo()).build();
            flowService.createAndRun(startCmd);
        }
        WarehouseOut warehouseOut = assembler.toWarehouseOut(editCmd);
        check(warehouseOut);

        warehouseOut.setStatus(editCmd.isCommit() ? ProcessStatus.COMMITTED : ProcessStatus.DRAFT);
        log.info("edit WarehouseOut params:{}", warehouseOut);
        warehouseOutRepository.edit(warehouseOut);
    }

    @Override
    public void statusChange(WarehouseIOStatusChangeCmd statusChangeCmd) {
        log.info("WarehouseIOStatusChangeCmd params:{}", statusChangeCmd);
        if (statusChangeCmd.getStatus() == ProcessStatus.COMMITTED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                WarehouseOut warehouseOutFound = warehouseOutRepository.get(serialNo);
                if (warehouseOutFound == null) {
                    throw new ServiceException(WarehouseIOError.E120101);
                }
                if (warehouseOutFound.getStatus() != ProcessStatus.DRAFT) {
                    throw new ServiceException(WarehouseIOError.E120102);
                }
                warehouseOutRepository.changeStatus(WarehouseOut.builder().status(statusChangeCmd.getStatus()).serialNo(serialNo).build());
                ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("purchase_warehouse_out").serialNo(serialNo).build();
                flowService.createAndRun(startCmd);
            }
        } else if (statusChangeCmd.getStatus() == ProcessStatus.CLOSED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                WarehouseOut warehouseOutFound = warehouseOutRepository.get(serialNo);
                if (warehouseOutFound == null) {
                    throw new ServiceException(WarehouseIOError.E120101);
                }
                if (warehouseOutFound.getStatus() != ProcessStatus.APPROVED) {
                    throw new ServiceException(WarehouseIOError.E120103);
                }
            }
            warehouseOutRepository.statusChange(statusChangeCmd);
        } else if (statusChangeCmd.getStatus() == ProcessStatus.APPROVED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                WarehouseOut warehouseOutFound = warehouseOutRepository.detail(serialNo);
                if (warehouseOutFound == null) {
                    throw new ServiceException(WarehouseIOError.E120101);
                }
                warehouseOutRepository.changeStatus(WarehouseOut.builder().status(statusChangeCmd.getStatus()).serialNo(serialNo).build());
                // 防止多次点击
                if (warehouseOutFound.getStatus() != ProcessStatus.APPROVED) {
                    // 库存记录
                    eventPublisher.publishEvent(new WarehouseStockEvent(assembler.toWarehouseOutStockEvent(warehouseOutFound)));
                }
            }
        } else {
            warehouseOutRepository.statusChange(statusChangeCmd);
        }
    }

    @Transactional
    @Override
    public void remove(WarehouseIORemoveCmd removeCmd) {
        log.info("WarehouseIORemoveCmd params:{}", removeCmd);
        warehouseOutRepository.remove(removeCmd.getSerialNos());
    }

    @Override
    public void warehouseUpdateEvent(WarehouseUpdateE eventData) {
        log.info("WarehouseUpdateE params:{}", eventData);
        warehouseOutRepository.moveTo(eventData.getFromWarehouseCode(), eventData.getToWarehouseCode());
    }

    @Override
    public void approved(ProcessResult processResult) {
        WarehouseOut changeCmd = WarehouseOut.builder().serialNo(processResult.getSerialNo())
                .status(ProcessStatus.APPROVED).build();
        warehouseOutRepository.changeStatus(changeCmd);
    }
}
