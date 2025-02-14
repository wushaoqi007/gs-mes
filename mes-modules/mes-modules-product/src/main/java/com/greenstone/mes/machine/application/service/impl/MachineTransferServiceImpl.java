package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.application.assemble.MachineTransferAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.*;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineStockAllQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineStockScanQuery;
import com.greenstone.mes.machine.application.dto.event.MachineTransferE;
import com.greenstone.mes.machine.application.dto.result.MachinePartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineTransferResult;
import com.greenstone.mes.machine.application.event.MachineTransferEvent;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.machine.application.service.MachineStockChangeService;
import com.greenstone.mes.machine.application.service.MachineStockService;
import com.greenstone.mes.machine.application.service.MachineTransferService;
import com.greenstone.mes.machine.domain.entity.MachineTransfer;
import com.greenstone.mes.machine.domain.entity.MachineTransferDetail;
import com.greenstone.mes.machine.domain.repository.MachineTransferRepository;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class MachineTransferServiceImpl implements MachineTransferService {

    private final MachineTransferRepository transferRepository;
    private final MachineTransferAssemble transferAssemble;
    private final RemoteSystemService systemService;
    private final ApplicationEventPublisher eventPublisher;
    private final MachineStockService stockService;
    private final MachineHelper machineHelper;
    private final MachineStockChangeService stockChangeService;

    @Transactional
    @Override
    public void saveDraft(MachineTransferAddCmd addCmd) {
        log.info("machine transfer save draft params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineTransfer transfer = validAndAssembleTransfer(addCmd, isNew, false);
        transfer.setStatus(ProcessStatus.DRAFT);
        if (isNew) {
            transferRepository.add(transfer);
        } else {
            transferRepository.edit(transfer);
        }
    }

    @Transactional
    @Override
    public void saveCommit(MachineTransferAddCmd addCmd) {
        log.info("machine transfer save commit params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineTransfer transfer = validAndAssembleTransfer(addCmd, isNew, true);
        transfer.setStatus(ProcessStatus.COMMITTED);
        if (isNew) {
            transferRepository.add(transfer);
        } else {
            transferRepository.edit(transfer);
        }
        // 提交后操作
        eventPublisher.publishEvent(new MachineTransferEvent(transferAssemble.toMachineTransferE(transfer)));
    }

    public MachineTransfer validAndAssembleTransfer(MachineTransferAddCmd addCmd, boolean isNew, boolean isCommit) {
        MachineTransfer transfer = transferAssemble.toMachineTransfer(addCmd);
        if (isNew) {
            // 设置单号
            SerialNoNextCmd nextCmd =
                    SerialNoNextCmd.builder().type("machine_transfer").prefix("MTS" + DateUtil.dateSerialStrNow()).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);
            transfer.setSerialNo(serialNoR.getSerialNo());
        }
        // 超出库存出库，需要调整的库存
        List<MachineStockChangeAddCmd.Part> stockChangeParts = new ArrayList<>();
        for (MachineTransferDetail part : transfer.getParts()) {
            part.setSerialNo(transfer.getSerialNo());
            // 出库仓库
            BaseWarehouse outWarehouse = machineHelper.existWarehouseByCode(part.getOutWarehouseCode());
            if (transfer.getStage() == null) {
                transfer.setStage(WarehouseStage.getById(outWarehouse.getStage()));
            }
            // 入库仓库
            BaseWarehouse inWarehouse = machineHelper.existWarehouseByCode(part.getInWarehouseCode());
            // 是否允许调拨
            machineHelper.allowTransfer(outWarehouse, inWarehouse);
            // 调拨区域限制：空砧板可改变区域，所以需限制单一区域
            if (transfer.getStage().getId() != outWarehouse.getStage()) {
                throw new ServiceException(MachineError.E200015,
                        StrUtil.format("该调拨单存在多个区域调拨：{}、{}", transfer.getStage().getName(), WarehouseStage.getById(outWarehouse.getStage()).getName()));
            }
            // 查询出库库存
            Long stockNumber = machineHelper.getStockNumber(part.getMaterialId(), outWarehouse.getCode());
            // 超出库存出库，需要库存调整单
            if (stockNumber < part.getNumber()) {
                if (addCmd.isForceOperation()) {
                    MachineStockChangeAddCmd.Part stockChangePart = MachineStockChangeAddCmd.Part.builder().partCode(part.getPartCode()).partVersion(part.getPartVersion()).partName(part.getPartName())
                            .materialId(part.getMaterialId().toString()).stockNumber(stockNumber).changeNumber(part.getNumber()).warehouseCode(outWarehouse.getCode()).build();
                    stockChangeParts.add(stockChangePart);
                } else {
                    throw new ServiceException(MachineError.E200014, StrUtil.format("零件号/版本：{}/{}，库存数量：{}，出库数量：{}",
                            part.getPartCode(), part.getPartVersion(), stockNumber, part.getNumber()));
                }
            }
        }
        // 库存调整单
        if (CollUtil.isNotEmpty(stockChangeParts) && isCommit) {
            MachineStockChangeAddCmd stockChangeAddCmd = machineHelper.buildChangeStockCmd(stockChangeParts);
            stockChangeService.saveCommit(stockChangeAddCmd);
        }
        return transfer;
    }

    @Override
    public void remove(MachineRemoveCmd removeCmd) {
        log.info("MachineRemoveCmd params:{}", removeCmd);
        transferRepository.remove(removeCmd.getSerialNos());
    }

    @Override
    public List<MachineTransferResult> selectList(MachineFuzzyQuery query) {
        log.info("query machine transfer list params:{}", query);
        List<MachineTransfer> list = transferRepository.list(query);
        return transferAssemble.toMachineTransferRs(list);
    }


    @Override
    public MachineTransferResult detail(String serialNo) {
        log.info("query machine transfer detail params:{}", serialNo);
        MachineTransfer detail = transferRepository.detail(serialNo);
        return transferAssemble.toMachineTransferR(detail);
    }

    @Override
    public MachinePartStockR scan(MachineStockScanQuery query) {
        BaseWarehouse warehouse = machineHelper.existWarehouseByCode(query.getWarehouseCode());
        return machineHelper.getPartStock(warehouse, query.getPartCode(), query.getPartVersion());
    }

    @Override
    public List<MachinePartStockR> stockAll(MachineStockAllQuery query) {
        machineHelper.existWarehouseByCode(query.getWarehouseCode());
        return machineHelper.getStockAllByWarehouse(query.getWarehouseCode());
    }

    @Override
    public void operationAfterTransfer(MachineTransferE source) {
        log.info("operationAfterTransfer params:{}", source);
        // 调拨出库
        Map<String, List<MachineTransferE.Part>> groupByOut = source.getParts().stream().collect(Collectors.groupingBy(MachineTransferE.Part::getOutWarehouseCode));
        groupByOut.forEach((outWarehouseCode, list) -> {
            BaseWarehouse warehouse = machineHelper.existWarehouseByCode(outWarehouseCode);
            List<MachineOutStockCommand.OutStockMaterial> materialInfoList = new ArrayList<>();
            for (MachineTransferE.Part part : list) {
                BaseMaterial baseMaterial = machineHelper.checkMaterialById(part.getMaterialId());
                part.setMaterial(baseMaterial);
                MachineOutStockCommand.OutStockMaterial material = MachineOutStockCommand.OutStockMaterial.builder()
                        .material(baseMaterial).number(part.getNumber()).build();
                materialInfoList.add(material);
            }
            MachineOutStockCommand outStockCommand = MachineOutStockCommand.builder().operation(BillOperation.TRANSFER_OUT).action(StockAction.OUT).warehouse(warehouse).remark("AUTO").sponsor("admin").materialList(materialInfoList).build();
            log.info("调拨出库参数:{}", outStockCommand);
            stockService.outStock(outStockCommand);
        });
        // 调拨入库
        Map<String, List<MachineTransferE.Part>> groupByIn = source.getParts().stream().collect(Collectors.groupingBy(MachineTransferE.Part::getInWarehouseCode));
        groupByIn.forEach((inWarehouseCode, list) -> {
            BaseWarehouse warehouse = machineHelper.existWarehouseByCode(inWarehouseCode);
            List<MachineInStockCommand.InStockMaterial> materialInfoList = new ArrayList<>();
            for (MachineTransferE.Part part : list) {
                MachineInStockCommand.InStockMaterial material = MachineInStockCommand.InStockMaterial.builder()
                        .material(part.getMaterial()).number(part.getNumber()).build();
                materialInfoList.add(material);
            }
            MachineInStockCommand inStockCommand = MachineInStockCommand.builder().operation(BillOperation.STOCK_OUT_CREATE).action(StockAction.IN).warehouse(warehouse).remark("AUTO").sponsor("admin").materialList(materialInfoList).build();
            log.info("调拨入库参数:{}", inStockCommand);
            stockService.inStock(inStockCommand);
        });
    }


}
