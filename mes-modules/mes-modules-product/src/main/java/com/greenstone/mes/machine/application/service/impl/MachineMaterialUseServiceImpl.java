package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.application.assemble.MachineMaterialUseAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineMaterialUseAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineMaterialUseFinishCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineWarehouseOutAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartListQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartScanQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartStockQuery;
import com.greenstone.mes.machine.application.dto.event.MachineMaterialUseE;
import com.greenstone.mes.machine.application.dto.result.MachineMaterialUseResult;
import com.greenstone.mes.machine.application.dto.result.MachineOrderPartR;
import com.greenstone.mes.machine.application.dto.result.MachinePartStockR;
import com.greenstone.mes.machine.application.event.MachineMaterialUseEvent;
import com.greenstone.mes.machine.application.service.MachineMaterialUseService;
import com.greenstone.mes.machine.application.service.MachineWarehouseOutService;
import com.greenstone.mes.machine.domain.entity.MachineMaterialUse;
import com.greenstone.mes.machine.domain.entity.MachineMaterialUseDetail;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import com.greenstone.mes.machine.domain.repository.MachineMaterialUseRepository;
import com.greenstone.mes.machine.domain.repository.MachineOrderOldRepository;
import com.greenstone.mes.machine.domain.repository.MachineStockRepository;
import com.greenstone.mes.machine.infrastructure.enums.UseStatus;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.domain.service.IBaseWarehouseService;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-12-29-11:30
 */
@AllArgsConstructor
@Slf4j
@Service
public class MachineMaterialUseServiceImpl implements MachineMaterialUseService {

    private final MachineMaterialUseRepository materialUseRepository;
    private final MachineMaterialUseAssemble materialUseAssemble;
    private final RemoteSystemService systemService;
    private final ApplicationEventPublisher eventPublisher;
    private final IBaseWarehouseService warehouseService;
    private final MachineStockRepository stockRepository;
    private final MachineOrderOldRepository orderRepository;
    private final MachineWarehouseOutService warehouseOutService;

    @Transactional
    @Override
    public void saveDraft(MachineMaterialUseAddCmd addCmd) {
        log.info("machine material use save draft params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineMaterialUse materialUse = validAndAssembleMaterialUse(addCmd, isNew);
        materialUse.setStatus(ProcessStatus.DRAFT);
        materialUse.setUseStatus(UseStatus.NONE);
        if (isNew) {
            materialUseRepository.add(materialUse);
        } else {
            materialUseRepository.edit(materialUse);
        }
    }

    @Transactional
    @Override
    public void saveCommit(MachineMaterialUseAddCmd addCmd) {
        log.info("machine material use save commit params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineMaterialUse materialUse = validAndAssembleMaterialUse(addCmd, isNew);
        materialUse.setStatus(ProcessStatus.COMMITTED);
        materialUse.setUseStatus(UseStatus.NONE);
        if (isNew) {
            materialUseRepository.add(materialUse);
        } else {
            materialUseRepository.edit(materialUse);
        }
        // 提交后操作
        eventPublisher.publishEvent(new MachineMaterialUseEvent(materialUseAssemble.toMaterialUseE(materialUse)));
    }

    public MachineMaterialUse validAndAssembleMaterialUse(MachineMaterialUseAddCmd addCmd, boolean isNew) {
        MachineMaterialUse materialUse = materialUseAssemble.toMachineMaterialUse(addCmd);
        if (isNew) {
            // 设置单号
            SerialNoNextCmd nextCmd =
                    SerialNoNextCmd.builder().type("machine_materialUse").prefix("MMU" + DateUtil.dateSerialStrNow()).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);
            materialUse.setSerialNo(serialNoR.getSerialNo());
        }
        for (MachineMaterialUseDetail part : materialUse.getParts()) {
            scan(MachineOrderPartScanQuery.builder().serialNo(part.getSerialNo()).projectCode(part.getProjectCode()).partCode(part.getPartCode()).partVersion(part.getPartVersion()).warehouseCode(part.getWarehouseCode()).build());
            part.setSerialNo(materialUse.getSerialNo());
        }
        return materialUse;
    }

    @Override
    public void remove(MachineRemoveCmd removeCmd) {
        log.info("MachineRemoveCmd params:{}", removeCmd);
        materialUseRepository.remove(removeCmd.getSerialNos());
    }

    @Override
    public List<MachineMaterialUseResult> selectList(MachineFuzzyQuery query) {
        log.info("query machine materialUse list params:{}", query);
        List<MachineMaterialUse> list = materialUseRepository.list(query);
        return materialUseAssemble.toMachineMaterialUseRs(list);
    }


    @Override
    public MachineMaterialUseResult detail(String serialNo) {
        log.info("query machine materialUse detail params:{}", serialNo);
        MachineMaterialUse detail = materialUseRepository.detail(serialNo);
        return materialUseAssemble.toMachineMaterialUseR(detail);
    }

    @Override
    public MachineOrderPartR scan(MachineOrderPartScanQuery query) {
        MachineOrderDetail orderDetail = orderRepository.selectPart(query);
        MachineOrderPartR machineOrderPartR = materialUseAssemble.toMachineOrderPartR(orderDetail);
        if (StrUtil.isEmpty(query.getWarehouseCode())) {
            throw new ServiceException(MachineError.E200009);
        }
        BaseWarehouse warehouse = warehouseService.queryWarehouseByCode(BaseWarehouse.builder().code(query.getWarehouseCode()).build());
        if (Objects.isNull(warehouse)) {
            throw new ServiceException(MachineError.E200007, StrUtil.format("仓库编码：{}", query.getWarehouseCode()));
        }
        if (warehouse.getStage() != WarehouseStage.GOOD.getId()) {
            throw new ServiceException(MachineError.E200009, StrUtil.format("领用单仓库只能选良品库"));
        }
        // 库存校验
        List<MachinePartStockR> stockRList = stockRepository.listStock(MachinePartStockQuery.builder()
                .materialId(machineOrderPartR.getMaterialId()).warehouseId(warehouse.getId()).build());
        if (CollUtil.isEmpty(stockRList)) {
            throw new ServiceException(MachineError.E200010, StrUtil.format("零件号/版本：{}/{}，库存数量为空", query.getPartCode(), query.getPartVersion()));
        }
        machineOrderPartR.setStockNumber(stockRList.get(0).getStockNumber());
        machineOrderPartR.setWarehouseId(warehouse.getId());
        machineOrderPartR.setWarehouseCode(warehouse.getCode());
        machineOrderPartR.setWarehouseName(warehouse.getName());
        return machineOrderPartR;
    }

    @Override
    public List<MachineOrderPartR> partChoose(MachineOrderPartListQuery query) {
        log.info("part choose query params:{}", query);
        return orderRepository.selectPartList(query);
    }

    @Override
    public void finish(MachineMaterialUseFinishCmd finishCmd) {
        for (String serialNo : finishCmd.getSerialNos()) {
            MachineMaterialUse materialUse = materialUseRepository.detail(serialNo);
            if (materialUse == null) {
                throw new ServiceException(MachineError.E200101, StrUtil.format("单号：{}", serialNo));
            }
            if (materialUse.getStatus() != ProcessStatus.COMMITTED) {
                throw new ServiceException(MachineError.E200501);
            }
            if (materialUse.getUseStatus() != UseStatus.NONE) {
                throw new ServiceException(MachineError.E200502);
            }
        }
        materialUseRepository.finish(finishCmd);
    }

    @Override
    public void operationAfterMaterialUse(MachineMaterialUseE source) {
        log.info("operationAfterMaterialUse params:{}", source);
        MachineWarehouseOutAddCmd machineWarehouseOutAddCmd = materialUseAssemble.toWarehouseOutAddCmd(source);
        warehouseOutService.saveCommit(machineWarehouseOutAddCmd);
    }

}
