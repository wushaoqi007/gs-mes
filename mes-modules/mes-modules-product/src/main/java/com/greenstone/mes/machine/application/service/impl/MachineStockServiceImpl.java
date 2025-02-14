package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.machine.application.assemble.MachineStockAssembler;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineInStockCommand;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineOutStockCommand;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockOperationCommand;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRealStockQuery;
import com.greenstone.mes.machine.application.dto.result.*;
import com.greenstone.mes.machine.application.event.MachineStockEvent;
import com.greenstone.mes.machine.application.service.MachineStockService;
import com.greenstone.mes.machine.domain.repository.MachineStockRepository;
import com.greenstone.mes.machine.infrastructure.mapper.MachineStockMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineStock;
import com.greenstone.mes.machine.interfaces.resp.MachineStageStockResp;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.domain.service.IBaseMaterialService;
import com.greenstone.mes.material.domain.service.IBaseWarehouseService;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import com.greenstone.mes.material.infrastructure.enums.StockBehavior;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
import com.greenstone.mes.warehouse.domain.StockCmd;
import com.greenstone.mes.warehouse.domain.StockMaterial;
import com.greenstone.mes.warehouse.domain.StockPrepareCmd;
import com.greenstone.mes.warehouse.domain.StockPrepareMaterial;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/1/13 14:54
 */
@AllArgsConstructor
@Slf4j
@Service
public class MachineStockServiceImpl implements MachineStockService {

    private final ApplicationEventPublisher eventPublisher;

    private final MachineStockAssembler stockAssembler;

    private final MachineStockRepository stockRepository;

    private final MachineStockMapper stockMapper;

    private final IBaseWarehouseService warehouseService;

    private final IBaseMaterialService materialService;

    /**
     * 出入库操作的统一入口
     *
     * @param stockPrepareCmd 预备的出入库数据
     */
    @Override
    public void doStock(StockPrepareCmd stockPrepareCmd) {
        if (CollUtil.isEmpty(stockPrepareCmd.getMaterialList())) {
            throw new ServiceException("零件列表不能为空");
        }

        BillOperation operation = stockPrepareCmd.getOperation();

        List<StockCmd> stockCmds = new ArrayList<>();
        // 对于已经定义好操作过程的单据，使用预定义的流程进行操作
        if (CollUtil.isNotEmpty(operation.getStageActions())) {
            for (BillOperation.StageAction stageAction : operation.getStageActions()) {
                // 将预定义的仓库操作数据StockPrepareCmd补全为StockCmd
                StockCmd stockCmd = prepare(stockPrepareCmd, operation, stageAction);
                // 检查仓库的阶段是否都一致
                if (!stockCmd.getMaterialList().stream().allMatch(m -> m.getWarehouse().getStage() == stageAction.getStage().getId())) {
                    throw new ServiceException("操作失败：仓库必须属于阶段：" + stageAction.getStage().getName());
                }
                stockCmds.add(stockCmd);
            }
        }
        // 对于没有定义操作过程的单据，单独处理
        else {
            // 库存变更单 需要根据实际情况计算出入库和数量
            if (operation == BillOperation.STOCK_CHANGE_CREATE) {
                StockCmd stockCmd = prepareStockChange(stockPrepareCmd, operation);
                stockCmds.add(stockCmd);
            } else {
                throw new ServiceException("操作失败：不支持的操作：" + operation.getName());
            }
        }
        if (CollUtil.isNotEmpty(stockCmds)) {
            doStock(stockCmds);
        }
    }

    private StockCmd prepareStockChange(StockPrepareCmd stockPrepareCmd, BillOperation operation) {
        // 设置单次操作的通用数据
        StockCmd stockCmd = new StockCmd();
        stockCmd.setOperation(operation);
        stockCmd.setSerialNo(stockPrepareCmd.getSerialNo());
        stockCmd.setSponsor(stockPrepareCmd.getSponsor());
        stockCmd.setApplicant(stockPrepareCmd.getApplicant());
        stockCmd.setApplicantNo(stockPrepareCmd.getApplicantNo());
        stockCmd.setRemark(stockPrepareCmd.getRemark());
        stockCmd.setProvider(stockPrepareCmd.getProvider());

        List<StockMaterial> stockMaterials = new ArrayList<>();
        stockCmd.setMaterialList(stockMaterials);

        for (StockPrepareMaterial prepareMaterial : stockPrepareCmd.getMaterialList()) {
            BaseWarehouse warehouse = getWarehouseOrThrow(prepareMaterial.getWarehouseCode());
            MachineStock machineStock = stockRepository.getStock(warehouse.getId(), prepareMaterial.getMaterialId(), prepareMaterial.getProjectCode());
            BaseMaterial material = getMaterialOrThrow(prepareMaterial.getMaterialId());
            StockAction action = machineStock == null ? StockAction.IN : (machineStock.getNumber() > prepareMaterial.getNumber() ? StockAction.OUT : StockAction.IN);
            StockMaterial stockMaterial = buildStockChangeMaterial(operation, action, warehouse, material, prepareMaterial, machineStock == null ? prepareMaterial.getNumber() : (machineStock.getNumber() > prepareMaterial.getNumber() ? machineStock.getNumber() - prepareMaterial.getNumber() : prepareMaterial.getNumber() - machineStock.getNumber()));
            stockMaterials.add(stockMaterial);

        }
        return stockCmd;
    }

    private StockCmd prepare(StockPrepareCmd stockPrepareCmd, BillOperation operation, BillOperation.StageAction stageAction) {
        // 设置单次操作的通用数据
        StockCmd stockCmd = new StockCmd();
        stockCmd.setOperation(operation);
        stockCmd.setSerialNo(stockPrepareCmd.getSerialNo());
        stockCmd.setSponsor(stockPrepareCmd.getSponsor());
        stockCmd.setApplicant(stockPrepareCmd.getApplicant());
        stockCmd.setApplicantNo(stockPrepareCmd.getApplicantNo());
        stockCmd.setRemark(stockPrepareCmd.getRemark());
        stockCmd.setProvider(stockPrepareCmd.getProvider());

        List<StockMaterial> stockMaterials = new ArrayList<>();
        stockCmd.setMaterialList(stockMaterials);

        // 如果是自动的操作流程则默认执行
        if (stageAction.isAuto()) {
            BaseWarehouse warehouse = warehouseService.findOnlyOneByStage(stageAction.getStage().getId());
            for (StockPrepareMaterial prepareMaterial : stockPrepareCmd.getMaterialList()) {
                BaseMaterial material = getMaterialOrThrow(prepareMaterial.getMaterialId());
                StockMaterial stockMaterial = buildStockMaterial(operation, stageAction.getAction(), warehouse, material, prepareMaterial);
                stockMaterials.add(stockMaterial);
            }
        } else {
            for (StockPrepareMaterial prepareMaterial : stockPrepareCmd.getMaterialList()) {
                BaseWarehouse warehouse = getWarehouseOrThrow(prepareMaterial.getWarehouseCode());
                BaseMaterial material = getMaterialOrThrow(prepareMaterial.getMaterialId());
                StockMaterial stockMaterial = buildStockMaterial(operation, stageAction.getAction(), warehouse, material, prepareMaterial);
                stockMaterials.add(stockMaterial);
            }
        }
        return stockCmd;
    }

    private BaseWarehouse getWarehouseOrThrow(String warehouseCode) {
        BaseWarehouse warehouse = warehouseService.getOneOnly(BaseWarehouse.builder().code(warehouseCode).build());
        if (warehouse == null) {
            throw new ServiceException("操作仓库失败：不存在此编号的仓库：" + warehouseCode);
        }
        return warehouse;
    }

    private BaseMaterial getMaterialOrThrow(Long materialId) {
        BaseMaterial material = materialService.getById(materialId);
        if (material == null) {
            throw new ServiceException("操作仓库失败：不存在此ID的物料：" + materialId);
        }
        return material;
    }

    private StockMaterial buildStockMaterial(BillOperation operation, StockAction action, BaseWarehouse warehouse, BaseMaterial material, StockPrepareMaterial prepareMaterial) {
        StockBehavior stockBehavior = StockBehavior.getOrThrow(operation, action);
        return StockMaterial.builder()
                .action(action)
                .behavior(stockBehavior)
                .material(material)
                .number(prepareMaterial.getNumber())
                .orderSerialNo(prepareMaterial.getOrderSerialNo())
                .projectCode(prepareMaterial.getProjectCode())
                .warehouse(warehouse).build();
    }

    private StockMaterial buildStockChangeMaterial(BillOperation operation, StockAction action, BaseWarehouse warehouse, BaseMaterial material, StockPrepareMaterial prepareMaterial, long changeNum) {
        StockBehavior stockBehavior = StockBehavior.getOrThrow(operation, action);
        return StockMaterial.builder()
                .action(action)
                .behavior(stockBehavior)
                .material(material)
                .number(changeNum)
                .orderSerialNo(prepareMaterial.getOrderSerialNo())
                .projectCode(prepareMaterial.getProjectCode())
                .warehouse(warehouse).build();
    }

    /**
     * @param stockCmds
     */

    private void doStock(List<StockCmd> stockCmds) {
        for (StockCmd stockCmd : stockCmds) {
            stockRepository.doStock(stockCmd);
            eventPublisher.publishEvent(stockCmd);
        }

    }

    /**
     * 所有出入库操作的入口
     */
    @Override
    public void operation(MachineStockOperationCommand operationCommand) {
        switch (operationCommand.getAction()) {
            case IN -> {
                inStock(stockAssembler.toInStockCommand(operationCommand));
                // 发布入库操作事件
//                MachineStockOperationE stockOperationEventData = stockAssembler.toStockOperationEventData(operationCommand);
//                eventPublisher.publishEvent(new MachineStockOperationEvent(stockOperationEventData));
            }
            case OUT -> {
                outStock(stockAssembler.toOutStockCommand(operationCommand));
                // 发布出库操作事件
//                MachineStockOperationE stockOperationEventData = stockAssembler.toStockOperationEventData(operationCommand);
//                eventPublisher.publishEvent(new MachineStockOperationEvent(stockOperationEventData));
            }
//            case TRANSFER -> {
//                try {
//                    transfer(stockAssembler.toTransferStockCommand(operationCommand));
//                } finally {
//                    // 全部出库的零件暂存在 ThreadLocal 中，用于在入库时使用，在业务结束时，需要将 ThreadLocal 中的数据清除，否则会造成内存溢出
//                    StockTransferUtil.removeOutStockCommand();
//                }
//            }
        }

    }

    @Override
    public void inStock(MachineInStockCommand inStockCmd) {
        log.info("In stock start");
        if (CollUtil.isEmpty(inStockCmd.getMaterialList())) {
            throw new ServiceException("入库清单不能为空");
        }
        // 保存入库数据
        stockRepository.saveInStock(inStockCmd);
        log.info("In stock succeed");
        // 发布入库事件
        eventPublisher.publishEvent(new MachineStockEvent(stockAssembler.toEventData(inStockCmd)));
    }

    @Override
    public void outStock(MachineOutStockCommand outStockCmd) {
        // 走到这里，默认强制出库，是否库存不足，要不要强制出库在各自业务里校验
        outStockCmd.setForceOut(true);
        log.info("Out stock start");
        if (CollUtil.isEmpty(outStockCmd.getMaterialList())) {
            log.warn("No material in out stock list");
            throw new ServiceException(BizError.E23006);
        }
        stockRepository.saveOutStock(outStockCmd);
        log.info("Out stock succeed");
        // 发布出库事件
        eventPublisher.publishEvent(new MachineStockEvent(stockAssembler.toEventData(outStockCmd)));
    }

    @Override
    public List<MachinePartStockR> listRealStock(MachineRealStockQuery query) {
        if (query.getEndDate() != null) {
            query.setEndDate(DateUtil.endOfDay(query.getEndDate()));
        }
        List<MachinePartStockR> stockRList = stockRepository.listRealStock(query);
        if (CollUtil.isNotEmpty(stockRList)) {
            // 滞留时间：不满一天显示小时数，满1天显示天数，不足一天的部分忽略
            for (MachinePartStockR partStockR : stockRList) {
                if (partStockR.getStayDays() == 0) {
                    partStockR.setDuration(partStockR.getStayHours() + "小时");
                } else {
                    partStockR.setDuration(partStockR.getStayDays() + "天");
                }
            }
        }
        return stockRList;
    }

    @Override
    public List<MachineStageStockResp> listStagesStock(Long partId, WarehouseStage[] stages, String projectCode) {
        return stockMapper.listStageStock(partId, stages, projectCode);
    }

    @Override
    public List<MachineStockExportR> exportRealStock(MachineRealStockQuery query) {
        return stockAssembler.toMachineStockERS(listRealStock(query));
    }

    @Override
    public List<MachineStockWaitReceiveExportR> exportWaitReceiveStock(MachineRealStockQuery query) {
        return stockAssembler.toMachineStockWaitReceiveERS(listRealStock(query));
    }

    @Override
    public List<MachineStockCheckedExportR> exportCheckedStock(MachineRealStockQuery query) {
        return stockAssembler.toMachineStockCheckedERS(listRealStock(query));
    }

    @Override
    public List<MachineStockReceivingExportR> exportReceivingStock(MachineRealStockQuery query) {
        return stockAssembler.toMachineStockReceivingERS(listRealStock(query));
    }

    @Override
    public List<MachineStockTreatingExportR> exportTreatingStock(MachineRealStockQuery query) {
        return stockAssembler.toMachineStockTreatingERS(listRealStock(query));
    }

    @Override
    public List<MachineStockStageExportR> exportStockStage(MachineRealStockQuery query) {
        return stockAssembler.toMachineStockStageERS(listRealStock(query));
    }

}
