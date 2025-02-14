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
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseOutRecord;
import com.greenstone.mes.machine.domain.converter.MachineWarehouseOutConverter;
import com.greenstone.mes.machine.domain.entity.MachineWarehouseOut;
import com.greenstone.mes.machine.dto.cmd.MachineSignFinishCmd;
import com.greenstone.mes.machine.infrastructure.mapper.MachineWarehouseOutDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineWarehouseOutMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineWarehouseOutDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineWarehouseOutDetailDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class MachineWarehouseOutRepository {
    private final MachineWarehouseOutMapper warehouseOutMapper;
    private final MachineWarehouseOutDetailMapper warehouseOutDetailMapper;
    private final MachineWarehouseOutConverter converter;

    public List<MachineWarehouseOut> list(MachineFuzzyQuery fuzzyQuery) {
        QueryWrapper<MachineWarehouseOutDO> fuzzyQueryWrapper = new QueryWrapper<>();
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
        List<MachineWarehouseOutDO> machineWarehouseOutDOS = warehouseOutMapper.selectList(fuzzyQueryWrapper);
        return converter.dos2Entities(machineWarehouseOutDOS);
    }

    public MachineWarehouseOut detail(String serialNo) {
        MachineWarehouseOutDO select = MachineWarehouseOutDO.builder().serialNo(serialNo).build();
        MachineWarehouseOutDO getOne = warehouseOutMapper.getOneOnly(select);
        if (getOne == null) {
            throw new ServiceException(MachineError.E200101);
        }
        List<MachineWarehouseOutDetailDO> detailDOS = warehouseOutDetailMapper.list(MachineWarehouseOutDetailDO.builder().serialNo(serialNo).build());
        return converter.toMachineWarehouseOut(getOne, detailDOS);
    }

    public MachineWarehouseOut getMachineWarehouseOut(String serialNo) {
        MachineWarehouseOutDO select = MachineWarehouseOutDO.builder().serialNo(serialNo).build();
        MachineWarehouseOutDO getOne = warehouseOutMapper.getOneOnly(select);
        if (getOne == null) {
            throw new ServiceException(MachineError.E200101);
        }
        return converter.do2Entity(getOne);
    }

    public void add(MachineWarehouseOut warehouseOut) {
        MachineWarehouseOutDO warehouseOutDO = converter.entity2Do(warehouseOut);
        warehouseOutMapper.insert(warehouseOutDO);
        List<MachineWarehouseOutDetailDO> detailList = converter.detailEntities2Dos(warehouseOut.getParts());
        warehouseOutDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void edit(MachineWarehouseOut warehouseOut) {
        MachineWarehouseOutDO warehouseOutDO = converter.entity2Do(warehouseOut);
        MachineWarehouseOutDO revokeFound = warehouseOutMapper.selectById(warehouseOut.getId());
        if (revokeFound == null) {
            throw new ServiceException(MachineError.E200101);
        }
        if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(MachineError.E200102);
        }
        warehouseOutMapper.updateById(warehouseOutDO);
        warehouseOutDetailMapper.delete(MachineWarehouseOutDetailDO.builder().serialNo(warehouseOut.getSerialNo()).build());
        List<MachineWarehouseOutDetailDO> detailList = converter.detailEntities2Dos(warehouseOut.getParts());
        warehouseOutDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void statusChange(MachineStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<MachineWarehouseOutDO> updateWrapper = Wrappers.lambdaUpdate(MachineWarehouseOutDO.class).set(MachineWarehouseOutDO::getStatus, statusChangeCmd.getStatus())
                .in(MachineWarehouseOutDO::getSerialNo, statusChangeCmd.getSerialNos());
        warehouseOutMapper.update(updateWrapper);
    }

    public void changeStatus(MachineWarehouseOut warehouseOut) {
        LambdaUpdateWrapper<MachineWarehouseOutDO> updateWrapper = Wrappers.lambdaUpdate(MachineWarehouseOutDO.class)
                .eq(MachineWarehouseOutDO::getSerialNo, warehouseOut.getSerialNo())
                .set(MachineWarehouseOutDO::getStatus, warehouseOut.getStatus());
        warehouseOutMapper.update(updateWrapper);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            MachineWarehouseOutDO revokeFound = warehouseOutMapper.getOneOnly(MachineWarehouseOutDO.builder().serialNo(serialNo).build());
            if (revokeFound == null) {
                throw new ServiceException(MachineError.E200101);
            }
            if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(MachineError.E200102);
            }
        }
        LambdaQueryWrapper<MachineWarehouseOutDO> revokeWrapper = Wrappers.lambdaQuery(MachineWarehouseOutDO.class).in(MachineWarehouseOutDO::getSerialNo, serialNos);
        warehouseOutMapper.delete(revokeWrapper);
        LambdaQueryWrapper<MachineWarehouseOutDetailDO> detailWrapper = Wrappers.lambdaQuery(MachineWarehouseOutDetailDO.class).in(MachineWarehouseOutDetailDO::getSerialNo,
                serialNos);
        warehouseOutDetailMapper.delete(detailWrapper);
    }

    public void signFinish(MachineSignFinishCmd finishCmd) {
        LambdaUpdateWrapper<MachineWarehouseOutDO> updateWrapper = Wrappers.lambdaUpdate(MachineWarehouseOutDO.class)
                .eq(MachineWarehouseOutDO::getSerialNo, finishCmd.getSerialNo())
                .set(MachineWarehouseOutDO::getSigned, finishCmd.getStatus() == ProcessStatus.FINISH)
                .set(MachineWarehouseOutDO::getSpNo, finishCmd.getSpNo())
                .set(MachineWarehouseOutDO::getStatus, finishCmd.getStatus());
        warehouseOutMapper.update(updateWrapper);
    }

    public void sign(String serialNo, String spNo) {
        LambdaUpdateWrapper<MachineWarehouseOutDO> updateWrapper = Wrappers.lambdaUpdate(MachineWarehouseOutDO.class)
                .eq(MachineWarehouseOutDO::getSerialNo, serialNo)
                .set(MachineWarehouseOutDO::getStatus, ProcessStatus.WAIT_APPROVE)
                .set(MachineWarehouseOutDO::getSpNo, spNo);
        warehouseOutMapper.update(updateWrapper);
    }

    public List<MachineWarehouseOutRecord> listRecord(MachineRecordQuery query) {
        return warehouseOutMapper.listRecord(query);
    }

    public List<MachineWarehouseOutDetailDO> selectDetailsByOrderSerialNos(List<String> serialNos) {
        LambdaQueryWrapper<MachineWarehouseOutDetailDO> queryWrapper = Wrappers.lambdaQuery(MachineWarehouseOutDetailDO.class)
                .select(MachineWarehouseOutDetailDO::getSerialNo, MachineWarehouseOutDetailDO::getRequirementSerialNo, MachineWarehouseOutDetailDO::getOrderSerialNo, MachineWarehouseOutDetailDO::getProjectCode, MachineWarehouseOutDetailDO::getPartCode, MachineWarehouseOutDetailDO::getPartVersion, MachineWarehouseOutDetailDO::getOutStockNumber)
                .in(MachineWarehouseOutDetailDO::getOrderSerialNo, serialNos);
        return warehouseOutDetailMapper.selectList(queryWrapper);
    }

    public List<MachineWarehouseOutDO> selectSubmitSerialNo(List<String> serialNos) {
        LambdaQueryWrapper<MachineWarehouseOutDO> queryWrapper = Wrappers.lambdaQuery(MachineWarehouseOutDO.class)
                .select(MachineWarehouseOutDO::getSerialNo)
                .eq(MachineWarehouseOutDO::getStatus, ProcessStatus.COMMITTED)
                .in(MachineWarehouseOutDO::getSerialNo, serialNos);
        return warehouseOutMapper.selectList(queryWrapper);
    }
}
