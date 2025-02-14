package com.greenstone.mes.machine.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStatusChangeCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseInRecord;
import com.greenstone.mes.machine.domain.converter.MachineWarehouseInConverter;
import com.greenstone.mes.machine.domain.entity.MachineWarehouseIn;
import com.greenstone.mes.machine.infrastructure.mapper.MachineWarehouseInDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineWarehouseInMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineWarehouseInDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineWarehouseInDetailDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class MachineWarehouseInRepository {
    private final MachineWarehouseInMapper warehouseInMapper;
    private final MachineWarehouseInDetailMapper warehouseInDetailMapper;
    private final MachineWarehouseInConverter converter;

    public List<MachineWarehouseIn> list(MachineFuzzyQuery fuzzyQuery) {
        QueryWrapper<MachineWarehouseInDO> fuzzyQueryWrapper = new QueryWrapper<>();
        fuzzyQueryWrapper.orderByDesc("create_time");
        if (StrUtil.isNotEmpty(fuzzyQuery.getKey()) && CollUtil.isNotEmpty(fuzzyQuery.getFields())) {
            fuzzyQueryWrapper.and(wrapper -> {
                for (String field : fuzzyQuery.getFields()) {
                    String fieldCode = StrUtil.toSymbolCase(field, '_');
                    wrapper.or().like(fieldCode, fuzzyQuery.getKey());
                }
            });
        }
        if (Objects.nonNull(fuzzyQuery.getStatus())) {
            fuzzyQueryWrapper.eq("status", fuzzyQuery.getStatus());
        }
        List<MachineWarehouseInDO> machineWarehouseInDOS = warehouseInMapper.selectList(fuzzyQueryWrapper);
        return converter.dos2Entities(machineWarehouseInDOS);
    }

    public MachineWarehouseIn detail(String serialNo) {
        MachineWarehouseInDO select = MachineWarehouseInDO.builder().serialNo(serialNo).build();
        MachineWarehouseInDO requisitionDO = warehouseInMapper.getOneOnly(select);
        if (requisitionDO == null) {
            throw new ServiceException(MachineError.E200101);
        }
        List<MachineWarehouseInDetailDO> detailDOS = warehouseInDetailMapper.list(MachineWarehouseInDetailDO.builder().serialNo(serialNo).build());
        return converter.toMachineWarehouseIn(requisitionDO, detailDOS);
    }

    public void add(MachineWarehouseIn warehouseIn) {
        MachineWarehouseInDO warehouseInDO = converter.entity2Do(warehouseIn);
        warehouseInMapper.insert(warehouseInDO);
        List<MachineWarehouseInDetailDO> detailList = converter.detailEntities2Dos(warehouseIn.getParts());
        warehouseInDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void edit(MachineWarehouseIn warehouseIn) {
        MachineWarehouseInDO warehouseInDO = converter.entity2Do(warehouseIn);
        MachineWarehouseInDO revokeFound = warehouseInMapper.selectById(warehouseIn.getId());
        if (revokeFound == null) {
            throw new ServiceException(MachineError.E200101);
        }
        if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(MachineError.E200102);
        }
        warehouseInMapper.updateById(warehouseInDO);
        warehouseInDetailMapper.delete(MachineWarehouseInDetailDO.builder().serialNo(warehouseIn.getSerialNo()).build());
        List<MachineWarehouseInDetailDO> detailList = converter.detailEntities2Dos(warehouseIn.getParts());
        warehouseInDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void statusChange(MachineStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<MachineWarehouseInDO> updateWrapper = Wrappers.lambdaUpdate(MachineWarehouseInDO.class).set(MachineWarehouseInDO::getStatus, statusChangeCmd.getStatus())
                .in(MachineWarehouseInDO::getSerialNo, statusChangeCmd.getSerialNos());
        warehouseInMapper.update(updateWrapper);
    }

    public void changeStatus(MachineWarehouseIn warehouseIn) {
        LambdaUpdateWrapper<MachineWarehouseInDO> updateWrapper = Wrappers.lambdaUpdate(MachineWarehouseInDO.class)
                .eq(MachineWarehouseInDO::getSerialNo, warehouseIn.getSerialNo())
                .set(MachineWarehouseInDO::getStatus, warehouseIn.getStatus());
        warehouseInMapper.update(updateWrapper);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            MachineWarehouseInDO revokeFound = warehouseInMapper.getOneOnly(MachineWarehouseInDO.builder().serialNo(serialNo).build());
            if (revokeFound == null) {
                throw new ServiceException(MachineError.E200101);
            }
            if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(MachineError.E200102);
            }
        }
        LambdaQueryWrapper<MachineWarehouseInDO> revokeWrapper = Wrappers.lambdaQuery(MachineWarehouseInDO.class).in(MachineWarehouseInDO::getSerialNo, serialNos);
        warehouseInMapper.delete(revokeWrapper);
        LambdaQueryWrapper<MachineWarehouseInDetailDO> detailWrapper = Wrappers.lambdaQuery(MachineWarehouseInDetailDO.class).in(MachineWarehouseInDetailDO::getSerialNo,
                serialNos);
        warehouseInDetailMapper.delete(detailWrapper);
    }

    public List<MachineWarehouseInRecord> listRecord(MachineRecordQuery query) {
        return warehouseInMapper.listRecord(query);
    }

    public List<MachineWarehouseInDetailDO> selectDetailsByOrderSerialNos(List<String> serialNos) {
        LambdaQueryWrapper<MachineWarehouseInDetailDO> queryWrapper = Wrappers.lambdaQuery(MachineWarehouseInDetailDO.class)
                .select(MachineWarehouseInDetailDO::getSerialNo, MachineWarehouseInDetailDO::getRequirementSerialNo, MachineWarehouseInDetailDO::getOrderSerialNo, MachineWarehouseInDetailDO::getProjectCode, MachineWarehouseInDetailDO::getPartCode, MachineWarehouseInDetailDO::getPartVersion, MachineWarehouseInDetailDO::getInStockNumber)
                .in(MachineWarehouseInDetailDO::getOrderSerialNo, serialNos);
        return warehouseInDetailMapper.selectList(queryWrapper);
    }

    public List<MachineWarehouseInDO> selectSubmitSerialNo(List<String> serialNos) {
        LambdaQueryWrapper<MachineWarehouseInDO> queryWrapper = Wrappers.lambdaQuery(MachineWarehouseInDO.class)
                .select(MachineWarehouseInDO::getSerialNo)
                .eq(MachineWarehouseInDO::getStatus, ProcessStatus.COMMITTED)
                .in(MachineWarehouseInDO::getSerialNo, serialNos);
        return warehouseInMapper.selectList(queryWrapper);
    }
}
