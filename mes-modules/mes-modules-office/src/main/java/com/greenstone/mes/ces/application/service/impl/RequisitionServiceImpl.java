package com.greenstone.mes.ces.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.ces.application.assembler.RequisitionAssembler;
import com.greenstone.mes.ces.application.dto.cmd.RequisitionAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.RequisitionEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.RequisitionRemoveCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseOutAddCmd;
import com.greenstone.mes.ces.application.dto.event.CesReturnAddE;
import com.greenstone.mes.ces.application.dto.event.RequisitionAddE;
import com.greenstone.mes.ces.application.dto.query.RequisitionFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.RequisitionItemResult;
import com.greenstone.mes.ces.application.dto.result.RequisitionResult;
import com.greenstone.mes.ces.application.event.RequisitionAddEvent;
import com.greenstone.mes.ces.application.service.RequisitionService;
import com.greenstone.mes.ces.domain.entity.*;
import com.greenstone.mes.ces.domain.repository.CesReturnRepository;
import com.greenstone.mes.ces.domain.repository.ItemSpecRepository;
import com.greenstone.mes.ces.domain.repository.RequisitionRepository;
import com.greenstone.mes.ces.domain.repository.WarehouseStockRepository;
import com.greenstone.mes.ces.dto.cmd.RequisitionStatusChangeCmd;
import com.greenstone.mes.common.core.enums.FormError;
import com.greenstone.mes.common.core.enums.RequisitionError;
import com.greenstone.mes.common.core.enums.WarehouseIOError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.application.service.ProcessInstanceService;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.form.dto.cmd.ProcessResult;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class RequisitionServiceImpl implements RequisitionService {

    private final RequisitionRepository requisitionRepository;
    private final RequisitionAssembler assembler;
    private final RemoteSystemService systemService;
    private final ProcessInstanceService flowService;
    private final ApplicationEventPublisher eventPublisher;
    private final ItemSpecRepository itemSpecRepository;
    private final WarehouseStockRepository stockRepository;
    private final CesReturnRepository cesReturnRepository;

    @Override
    public List<RequisitionResult> list(RequisitionFuzzyQuery query) {
        log.info("RequisitionFuzzyQuery params:{}", query);
        List<Requisition> requisitions = requisitionRepository.list(query);
        return assembler.toRequisitionRs(requisitions);
    }

    @Override
    public List<RequisitionItemResult> itemList(RequisitionFuzzyQuery query) {
        log.info("RequisitionFuzzyQuery params:{}", query);
        return requisitionRepository.listItem(query);
    }

    @Override
    public RequisitionResult detail(String serialNo) {
        log.info("detail params:{}", serialNo);
        Requisition requisition = requisitionRepository.detail(serialNo);
        return assembler.toRequisitionR(requisition);
    }


    @Override
    public void approved(ProcessResult processResult) {
        Requisition changeCmd = Requisition.builder().serialNo(processResult.getSerialNo())
                .status(ProcessStatus.APPROVED).build();
        requisitionRepository.changeStatus(changeCmd);
    }


    @Override
    public void add(RequisitionAddCmd addCmd) {
        log.info("RequisitionAddCmd params:{}", addCmd);
        Requisition requisition = assembler.toRequisition(addCmd);
        check(requisition.getItems());

        SerialNoNextCmd nextCmd =
                SerialNoNextCmd.builder().type("ces_requisition").prefix("CRQ" + DateUtil.dateSerialStrNow()).build();
        SerialNoR serialNoR = systemService.getNextSn(nextCmd);

        requisition.setSerialNo(serialNoR.getSerialNo());
        requisition.setStatus(addCmd.isCommit() ? ProcessStatus.APPROVING : ProcessStatus.DRAFT);
        requisition.setRequisitionerId(SecurityUtils.getLoginUser().getUser().getUserId());
        requisition.setRequisitionerName(SecurityUtils.getLoginUser().getUser().getNickName());
        requisition.setRequisitionerNo(SecurityUtils.getLoginUser().getUser().getEmployeeNo());

        log.info("add Requisition params:{}", requisition);
        requisitionRepository.add(requisition);

//        if (addCmd.isCommit()) {
//            ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("purchase_requisition").serialNo(serialNoR.getSerialNo()).build();
//            flowService.createAndRun(startCmd);
//        }
    }

    public void check(List<RequisitionItem> items) {
        for (RequisitionItem item : items) {
            // 校验：去除自定义物品，只能选物品档案中物品
            ItemSpec itemSpec = itemSpecRepository.detail(item.getItemCode());
            if (Objects.isNull(itemSpec)) {
                throw new ServiceException(FormError.E70105);
            } else {
                item.setNeedReturn(itemSpec.getNeedReturn());
                item.setUnitPrice(itemSpec.getDefaultPrice());
            }
            // 校验：库存检查，不能领库存不足的物品
            WarehouseStock stock = stockRepository.getByWarehouseAndItem(item.getWarehouseCode(), item.getItemCode());
            if (Objects.isNull(stock) || stock.getNumber() < item.getRequisitionNum()) {
                throw new ServiceException(WarehouseIOError.E120105);
            }
        }
    }

    @Transactional
    @Override
    public void edit(RequisitionEditCmd editCmd) {
        log.info("RequisitionEditCmd params:{}", editCmd);
        Requisition requisitionFound = requisitionRepository.get(editCmd.getSerialNo());
        if (requisitionFound == null) {
            throw new ServiceException(RequisitionError.E140101);
        }
        if (requisitionFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(RequisitionError.E140102);
        }

//        if (editCmd.isCommit()) {
//            ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("ces_requisition").serialNo(editCmd.getSerialNo()).build();
//            flowService.createAndRun(startCmd);
//        }
        Requisition requisition = assembler.toRequisition(editCmd);
        check(requisition.getItems());

        requisition.setStatus(editCmd.isCommit() ? ProcessStatus.COMMITTED : ProcessStatus.DRAFT);
        log.info("edit Requisition params:{}", requisition);
        requisitionRepository.edit(requisition);
    }

    @Override
    public void returnAddEvent(CesReturnAddE eventData) {
        log.info("CesReturnAddE params:{}", eventData);
        CesReturn cesReturn = cesReturnRepository.detail(eventData.getSerialNo());
        if (Objects.nonNull(cesReturn)) {
            for (CesReturnItem item : cesReturn.getItems()) {
                RequisitionItem requisitionRepositoryItem = requisitionRepository.getItemById(item.getRequisitionItemId());
                if (Objects.nonNull(requisitionRepositoryItem)) {
                    requisitionRepositoryItem.setReturnDate(LocalDateTime.now());
                    requisitionRepositoryItem.setReturnNum(requisitionRepositoryItem.getReturnNum() == null ? item.getReturnNum() : requisitionRepositoryItem.getReturnNum() + item.getReturnNum());
                    requisitionRepositoryItem.setLossNum(requisitionRepositoryItem.getLossNum() == null ? item.getLossNum() : requisitionRepositoryItem.getLossNum() + item.getLossNum());
                    requisitionRepository.updateByCesReturn(requisitionRepositoryItem);
                }
            }
        }
    }

    @Override
    public List<WarehouseOutAddCmd> createWarehouseOut(String serialNo) {
        List<WarehouseOutAddCmd> cmdList = new ArrayList<>();
        Requisition requisition = requisitionRepository.detail(serialNo);
        if (Objects.nonNull(requisition)) {
            // 领用单物品按照收货仓库分类，生成多个出库单（一个出库单对应一个仓库编码）
            List<RequisitionItem> requisitionItems = requisition.getItems().stream().filter(a -> StrUtil.isNotEmpty(a.getWarehouseCode())).collect(Collectors.toList());
            Map<String, List<RequisitionItem>> groupByWarehouse = requisitionItems.stream().collect(Collectors.groupingBy(RequisitionItem::getWarehouseCode));
            groupByWarehouse.forEach((warehouseCode, list) -> {
                List<WarehouseOutAddCmd.Item> itemList = new ArrayList<>();
                WarehouseOutAddCmd warehouseOutAddCmd = WarehouseOutAddCmd.builder().autoCreate(true).
                        warehouseCode(warehouseCode).
                        recipientId(requisition.getRequisitionerId()).recipientName(requisition.getRequisitionerName()).
                        outDate(LocalDate.now()).
                        handleDate(LocalDateTime.now()).
                        items(itemList).build();
                cmdList.add(warehouseOutAddCmd);
                for (RequisitionItem requisitionItem : list) {
                    WarehouseOutAddCmd.Item warehouseOutAddCmdItem = assembler.toWarehouseOutAddCmdItem(requisitionItem);
                    itemList.add(warehouseOutAddCmdItem);
                }
            });
        }
        return cmdList;
    }


    @Override
    public void statusChange(RequisitionStatusChangeCmd statusChangeCmd) {
        log.info("RequisitionStatusChangeCmd params:{}", statusChangeCmd);
        if (statusChangeCmd.getStatus() == ProcessStatus.COMMITTED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                Requisition requisitionFound = requisitionRepository.get(serialNo);
                if (requisitionFound == null) {
                    throw new ServiceException(RequisitionError.E140101);
                }
                if (requisitionFound.getStatus() != ProcessStatus.DRAFT) {
                    throw new ServiceException(RequisitionError.E140102);
                }
                requisitionRepository.changeStatus(Requisition.builder().status(statusChangeCmd.getStatus()).serialNo(serialNo).build());
//                ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("ces_requisition").serialNo(serialNo).build();
//                flowService.createAndRun(startCmd);
            }
        } else if (statusChangeCmd.getStatus() == ProcessStatus.CLOSED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                Requisition requisitionFound = requisitionRepository.get(serialNo);
                if (requisitionFound == null) {
                    throw new ServiceException(RequisitionError.E140101);
                }
                if (requisitionFound.getStatus() != ProcessStatus.APPROVED) {
                    throw new ServiceException(RequisitionError.E140103);
                }
            }
            requisitionRepository.statusChange(statusChangeCmd);
        } else if (statusChangeCmd.getStatus() == ProcessStatus.APPROVED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                Requisition requisitionFound = requisitionRepository.detail(serialNo);
                if (requisitionFound == null) {
                    throw new ServiceException(RequisitionError.E140101);
                }
                requisitionRepository.changeStatus(Requisition.builder().status(statusChangeCmd.getStatus()).serialNo(serialNo).build());
                // 防止多次点击
                if (requisitionFound.getStatus() != ProcessStatus.APPROVED) {
                    eventPublisher.publishEvent(new RequisitionAddEvent(RequisitionAddE.builder().serialNo(requisitionFound.getSerialNo()).build()));
                }
            }
        } else {
            requisitionRepository.statusChange(statusChangeCmd);
        }
    }

    @Transactional
    @Override
    public void remove(RequisitionRemoveCmd removeCmd) {
        log.info("RequisitionRemoveCmd params:{}", removeCmd);
        requisitionRepository.remove(removeCmd.getSerialNos());
    }

}
