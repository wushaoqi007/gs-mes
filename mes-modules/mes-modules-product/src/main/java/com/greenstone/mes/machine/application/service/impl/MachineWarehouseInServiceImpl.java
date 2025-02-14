package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.application.assemble.MachineWarehouseInAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineWarehouseInAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseInRecord;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseInRecordExportR;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseInResult;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.machine.application.service.MachineStockService;
import com.greenstone.mes.machine.application.service.MachineWarehouseInService;
import com.greenstone.mes.machine.domain.entity.MachineOrder;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import com.greenstone.mes.machine.domain.entity.MachineWarehouseIn;
import com.greenstone.mes.machine.domain.entity.MachineWarehouseInDetail;
import com.greenstone.mes.machine.domain.helper.StockVoHelper;
import com.greenstone.mes.machine.domain.repository.MachineOrderOldRepository;
import com.greenstone.mes.machine.domain.repository.MachineRequirementOldRepository;
import com.greenstone.mes.machine.domain.repository.MachineWarehouseInRepository;
import com.greenstone.mes.machine.domain.service.MachineStockManager;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import com.greenstone.mes.table.infrastructure.constant.TableConst;
import com.greenstone.mes.warehouse.domain.StockPrepareCmd;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2023-12-08-11:30
 */
@AllArgsConstructor
@Slf4j
@Service
public class MachineWarehouseInServiceImpl implements MachineWarehouseInService {

    private final MachineWarehouseInRepository warehouseInRepository;
    private final MachineWarehouseInAssemble warehouseInAssemble;
    private final RemoteSystemService systemService;
    private final MachineOrderOldRepository orderRepository;
    private final MachineRequirementOldRepository requirementRepository;
    private final MachineStockManager stockManager;
    private final MachineHelper machineHelper;
    private final StockVoHelper stockVoHelper;
    private final MachineStockService stockService;

    @Transactional
    @Override
    public void saveDraft(MachineWarehouseInAddCmd addCmd) {
        log.info("machine warehouse in save draft params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineWarehouseIn warehouseIn = validAndAssembleWarehouseIn(addCmd, isNew, false);
        warehouseIn.setStatus(ProcessStatus.DRAFT);
        if (isNew) {
            warehouseInRepository.add(warehouseIn);
        } else {
            warehouseInRepository.edit(warehouseIn);
        }
    }

    @Transactional
    @Override
    public void saveCommit(MachineWarehouseInAddCmd addCmd) {
        log.info("machine warehouse in save commit params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineWarehouseIn warehouseIn = validAndAssembleWarehouseIn(addCmd, isNew, true);
        warehouseIn.setStatus(ProcessStatus.COMMITTED);
        if (isNew) {
            warehouseInRepository.add(warehouseIn);
        } else {
            warehouseInRepository.edit(warehouseIn);
        }
        // 提交后操作:零件入库
        doStockWhenStockInGoodsCommit(warehouseIn);
        // 更新订单入库数量
        updateOrderInStockNum(addCmd);
    }

    public void updateOrderInStockNum(MachineWarehouseInAddCmd addCmd) {
        Map<String, List<MachineWarehouseInAddCmd.Part>> groupByReSerialNo = addCmd.getParts().stream().collect(Collectors.groupingBy(MachineWarehouseInAddCmd.Part::getOrderSerialNo));
        groupByReSerialNo.forEach((orderSerialNo, list) -> {
            MachineOrder detail = orderRepository.detail(orderSerialNo);
            if (detail.getDataStatus() != TableConst.DataStatus.EFFECTIVE) {
                throw new ServiceException(StrUtil.format("订单未生效，订单号：{}", orderSerialNo));
            }
            for (MachineWarehouseInAddCmd.Part part : list) {
                // 校验订单
                MachineOrderDetail updateOrderDetail = machineHelper.existInMachineOrder(detail, part.getRequirementSerialNo(), part.getProjectCode(), part.getPartCode(), part.getPartVersion());
                Long inStockNum = updateOrderDetail.getInStockNumber() == null ? part.getInStockNumber() : updateOrderDetail.getInStockNumber() + part.getInStockNumber();
                if (inStockNum > updateOrderDetail.getProcessNumber()) {
                    throw new ServiceException(StrUtil.format("入库零件数量超出订单购买数量，订单号：{}，申请单号：{}，项目号：{}，零件号/版本：{}/{}", orderSerialNo, part.getRequirementSerialNo(), part.getProjectCode(), part.getPartCode(), part.getPartVersion()));
                }
                updateOrderDetail.setInStockNumber(inStockNum);
                updateOrderDetail.setInStockTime(LocalDateTime.now());
                orderRepository.update(updateOrderDetail);
            }
        });
    }

    public MachineWarehouseIn validAndAssembleWarehouseIn(MachineWarehouseInAddCmd addCmd, boolean isNew, boolean isCommit) {
        MachineWarehouseIn warehouseIn = warehouseInAssemble.toMachineWarehouseIn(addCmd);
        // 默认操作类型：良品入库
        if (warehouseIn.getOperation() == null) {
            warehouseIn.setOperation(BillOperation.OK_IN_STOCK_CREATE.getId());
        }
        if (isCommit) {
            checkPart(warehouseIn, addCmd.isForceOperation());
        }
        if (isNew) {
            // 设置单号
            SerialNoNextCmd nextCmd =
                    SerialNoNextCmd.builder().type("machine_warehouseIn").prefix("MWI" + DateUtil.dateSerialStrNow()).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);
            warehouseIn.setSerialNo(serialNoR.getSerialNo());
        }
        warehouseIn.getParts().forEach(p -> p.setSerialNo(warehouseIn.getSerialNo()));
        return warehouseIn;
    }

    public void checkPart(MachineWarehouseIn warehouseIn, boolean isForceOperation) {
        for (MachineWarehouseInDetail part : warehouseIn.getParts()) {
            // 校验零件
            machineHelper.checkMaterial(part.getPartCode(), part.getPartVersion());
            // 校验仓库
            machineHelper.existWarehouseByCode(part.getWarehouseCode());
            if (!isForceOperation) {
                // 合格品库存
                BaseWarehouse outWarehouse = machineHelper.findWarehouseByStage(WarehouseStage.CHECKED_OK);
                // 查询库存
                Long stockNumber = machineHelper.getStockNumberWithProjectCode(part.getProjectCode(), part.getMaterialId(), outWarehouse.getCode());
                if (stockNumber < part.getInStockNumber()) {
                    throw new ServiceException(StrUtil.format("合格品数量不足，项目号：{}，零件号/版本：{}/{}，合格品库存数量：{}，入库数量：{}",
                            part.getProjectCode(), part.getPartCode(), part.getPartVersion(), stockNumber, part.getInStockNumber()));
                }
            }
        }
    }

    @Override
    public void remove(MachineRemoveCmd removeCmd) {
        log.info("MachineRemoveCmd params:{}", removeCmd);
        warehouseInRepository.remove(removeCmd.getSerialNos());
    }

    @Override
    public List<MachineWarehouseInResult> selectList(MachineFuzzyQuery query) {
        log.info("query machine warehouseIn list params:{}", query);
        List<MachineWarehouseIn> list = warehouseInRepository.list(query);
        return warehouseInAssemble.toMachineWarehouseInRs(list);
    }


    @Override
    public MachineWarehouseInResult detail(String serialNo) {
        log.info("query machine warehouseIn detail params:{}", serialNo);
        MachineWarehouseIn detail = warehouseInRepository.detail(serialNo);
        return warehouseInAssemble.toMachineWarehouseInR(detail);
    }

    public BaseWarehouse checkInStockWarehouse(String warehouseCode) {
        BaseWarehouse warehouse = machineHelper.existWarehouseByCode(warehouseCode);
        // TODO 暂时允许所有仓库使用入库单
//        if (warehouse.getType() == WarehouseType.BOARD.getType()) {
//            machineHelper.usableBoard(warehouse, PartStage.FINISHED.getId());
//        } else if (warehouse.getStage() != PartStage.FINISHED.getId()) {
//            throw new ServiceException(MachineError.E200009, StrUtil.format("入库单只能选择良品库入库"));
//        }
        return warehouse;
    }

    @Override
    public List<MachineWarehouseInRecord> listRecord(MachineRecordQuery query) {
        if (query.getEndDate() != null) {
            query.setEndDate(cn.hutool.core.date.DateUtil.endOfDay(query.getEndDate()));
        }
        return warehouseInRepository.listRecord(query);
    }

    @Override
    public List<MachineWarehouseInRecordExportR> exportRecord(MachineRecordQuery query) {
        return warehouseInAssemble.toMachineWarehouseInRecordERS(listRecord(query));
    }

    @Override
    public void doStockWhenStockInGoodsCommit(MachineWarehouseIn warehouseIn) {
        log.info("入库，开始处理库存数据: {}", warehouseIn);
        StockPrepareCmd stockPrepareCmd = stockVoHelper.converterStockCmd(warehouseIn);
        stockService.doStock(stockPrepareCmd);
        log.info("入库，处理库存数据完成");
    }

}
