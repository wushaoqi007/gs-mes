package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.application.assemble.MachineMaterialReturnAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineMaterialReturnAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartListQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartScanQuery;
import com.greenstone.mes.machine.application.dto.result.MachineMaterialReturnResult;
import com.greenstone.mes.machine.application.dto.result.MachineOrderPartR;
import com.greenstone.mes.machine.application.service.MachineMaterialReturnService;
import com.greenstone.mes.machine.domain.entity.MachineMaterialReturn;
import com.greenstone.mes.machine.domain.entity.MachineMaterialReturnDetail;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import com.greenstone.mes.machine.domain.repository.MachineMaterialReturnRepository;
import com.greenstone.mes.machine.domain.repository.MachineOrderOldRepository;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.domain.service.IBaseWarehouseService;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-12-08-11:30
 */
@AllArgsConstructor
@Slf4j
@Service
public class MachineMaterialReturnServiceImpl implements MachineMaterialReturnService {

    private final MachineMaterialReturnRepository materialReturnRepository;
    private final MachineMaterialReturnAssemble materialReturnAssemble;
    private final RemoteSystemService systemService;
    private final IBaseWarehouseService warehouseService;
    private final MachineOrderOldRepository orderRepository;

    @Transactional
    @Override
    public void saveDraft(MachineMaterialReturnAddCmd addCmd) {
        log.info("machine material return save draft params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineMaterialReturn materialReturn = validAndAssembleMaterialReturn(addCmd, isNew);
        materialReturn.setStatus(ProcessStatus.DRAFT);
        if (isNew) {
            materialReturnRepository.add(materialReturn);
        } else {
            materialReturnRepository.edit(materialReturn);
        }
    }

    @Transactional
    @Override
    public void saveCommit(MachineMaterialReturnAddCmd addCmd) {
        log.info("machine material return save commit params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineMaterialReturn materialReturn = validAndAssembleMaterialReturn(addCmd, isNew);
        materialReturn.setStatus(ProcessStatus.COMMITTED);
        if (isNew) {
            materialReturnRepository.add(materialReturn);
        } else {
            materialReturnRepository.edit(materialReturn);
        }
    }

    public MachineMaterialReturn validAndAssembleMaterialReturn(MachineMaterialReturnAddCmd addCmd, boolean isNew) {
        MachineMaterialReturn materialReturn = materialReturnAssemble.toMachineMaterialReturn(addCmd);
        if (isNew) {
            // 设置单号
            SerialNoNextCmd nextCmd =
                    SerialNoNextCmd.builder().type("machine_materialReturn").prefix("MMR" + DateUtil.dateSerialStrNow()).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);
            materialReturn.setSerialNo(serialNoR.getSerialNo());
        }
        for (MachineMaterialReturnDetail part : materialReturn.getParts()) {
            scan(MachineOrderPartScanQuery.builder().serialNo(part.getSerialNo()).projectCode(part.getProjectCode()).partCode(part.getPartCode()).partVersion(part.getPartVersion()).warehouseCode(part.getWarehouseCode()).build());
            if (Objects.nonNull(part.getWarehouseCode())) {
                BaseWarehouse warehouse = warehouseService.queryWarehouseByCode(BaseWarehouse.builder().code(part.getWarehouseCode()).build());
                if (Objects.isNull(warehouse)) {
                    throw new ServiceException(MachineError.E200007, StrUtil.format("仓库编码：{}", part.getWarehouseCode()));
                }
                part.setWarehouseId(warehouse.getId());
                part.setWarehouseName(warehouse.getName());
            }
            part.setSerialNo(materialReturn.getSerialNo());
        }
        return materialReturn;
    }

    @Override
    public void remove(MachineRemoveCmd removeCmd) {
        log.info("MachineRemoveCmd params:{}", removeCmd);
        materialReturnRepository.remove(removeCmd.getSerialNos());
    }

    @Override
    public List<MachineMaterialReturnResult> selectList(MachineFuzzyQuery query) {
        log.info("query machine materialReturn list params:{}", query);
        List<MachineMaterialReturn> list = materialReturnRepository.list(query);
        return materialReturnAssemble.toMachineMaterialReturnRs(list);
    }


    @Override
    public MachineMaterialReturnResult detail(String serialNo) {
        log.info("query machine materialReturn detail params:{}", serialNo);
        MachineMaterialReturn detail = materialReturnRepository.detail(serialNo);
        return materialReturnAssemble.toMachineMaterialReturnR(detail);
    }

    @Override
    public MachineOrderPartR scan(MachineOrderPartScanQuery query) {
        MachineOrderDetail orderDetail = orderRepository.selectPart(query);
        return materialReturnAssemble.toMachineOrderPartR(orderDetail);
    }

    @Override
    public List<MachineOrderPartR> partChoose(MachineOrderPartListQuery query) {
        log.info("part choose query params:{}", query);
        return orderRepository.selectPartList(query);
    }

}
