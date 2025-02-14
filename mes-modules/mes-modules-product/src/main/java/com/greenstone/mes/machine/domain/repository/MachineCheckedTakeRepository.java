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
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordFuzzyQuery;
import com.greenstone.mes.machine.application.dto.result.MachineCheckedTakeRecord;
import com.greenstone.mes.machine.domain.converter.MachineCheckedTakeConverter;
import com.greenstone.mes.machine.domain.entity.MachineCheckedTake;
import com.greenstone.mes.machine.dto.cmd.MachineSignFinishCmd;
import com.greenstone.mes.machine.infrastructure.mapper.MachineCheckedTakeDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineCheckedTakeMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCheckedTakeDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCheckedTakeDetailDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class MachineCheckedTakeRepository {
    private final MachineCheckedTakeMapper checkTakeMapper;
    private final MachineCheckedTakeDetailMapper checkTakeDetailMapper;
    private final MachineCheckedTakeConverter converter;

    public List<MachineCheckedTake> list(MachineFuzzyQuery fuzzyQuery) {
        QueryWrapper<MachineCheckedTakeDO> fuzzyQueryWrapper = new QueryWrapper<>();
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
        List<MachineCheckedTakeDO> machineCheckedTakeDOS = checkTakeMapper.selectList(fuzzyQueryWrapper);
        List<MachineCheckedTake> machineCheckedTakes = converter.dos2Entities(machineCheckedTakeDOS);
        for (MachineCheckedTake machineCheckedTake : machineCheckedTakes) {
            List<MachineCheckedTakeDetailDO> detailDOS = checkTakeDetailMapper.list(MachineCheckedTakeDetailDO.builder().serialNo(machineCheckedTake.getSerialNo()).build());
            machineCheckedTake.setParts(converter.detailDos2Entities(detailDOS));
        }
        return machineCheckedTakes;
    }

    public List<MachineCheckedTakeRecord> listRecord(MachineRecordFuzzyQuery query) {
        return checkTakeMapper.listRecord(query);
    }

    public MachineCheckedTake detail(String serialNo) {
        MachineCheckedTakeDO select = MachineCheckedTakeDO.builder().serialNo(serialNo).build();
        MachineCheckedTakeDO requisitionDO = checkTakeMapper.getOneOnly(select);
        if (requisitionDO == null) {
            throw new ServiceException(MachineError.E200101);
        }
        List<MachineCheckedTakeDetailDO> detailDOS = checkTakeDetailMapper.list(MachineCheckedTakeDetailDO.builder().serialNo(serialNo).build());
        return converter.toMachineCheckedTake(requisitionDO, detailDOS);
    }

    public MachineCheckedTake getMachineCheckedTake(String serialNo) {
        MachineCheckedTakeDO select = MachineCheckedTakeDO.builder().serialNo(serialNo).build();
        MachineCheckedTakeDO getOne = checkTakeMapper.getOneOnly(select);
        if (getOne == null) {
            throw new ServiceException(MachineError.E200101);
        }
        return converter.do2Entity(getOne);
    }

    public void add(MachineCheckedTake check) {
        MachineCheckedTakeDO checkDO = converter.entity2Do(check);
        checkTakeMapper.insert(checkDO);
        List<MachineCheckedTakeDetailDO> detailList = converter.detailEntities2Dos(check.getParts());
        checkTakeDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void edit(MachineCheckedTake check) {
        MachineCheckedTakeDO checkDO = converter.entity2Do(check);
        MachineCheckedTakeDO revokeFound = checkTakeMapper.selectById(check.getId());
        if (revokeFound == null) {
            throw new ServiceException(MachineError.E200101);
        }
        if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(MachineError.E200102);
        }
        checkTakeMapper.updateById(checkDO);
        checkTakeDetailMapper.delete(MachineCheckedTakeDetailDO.builder().serialNo(check.getSerialNo()).build());
        List<MachineCheckedTakeDetailDO> detailList = converter.detailEntities2Dos(check.getParts());
        checkTakeDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void statusChange(MachineStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<MachineCheckedTakeDO> updateWrapper = Wrappers.lambdaUpdate(MachineCheckedTakeDO.class).set(MachineCheckedTakeDO::getStatus, statusChangeCmd.getStatus())
                .in(MachineCheckedTakeDO::getSerialNo, statusChangeCmd.getSerialNos());
        checkTakeMapper.update(updateWrapper);
    }

    public void changeStatus(MachineCheckedTake check) {
        LambdaUpdateWrapper<MachineCheckedTakeDO> updateWrapper = Wrappers.lambdaUpdate(MachineCheckedTakeDO.class)
                .eq(MachineCheckedTakeDO::getSerialNo, check.getSerialNo())
                .set(MachineCheckedTakeDO::getStatus, check.getStatus());
        checkTakeMapper.update(updateWrapper);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            MachineCheckedTakeDO revokeFound = checkTakeMapper.getOneOnly(MachineCheckedTakeDO.builder().serialNo(serialNo).build());
            if (revokeFound == null) {
                throw new ServiceException(MachineError.E200101);
            }
            if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(MachineError.E200102);
            }
        }
        LambdaQueryWrapper<MachineCheckedTakeDO> revokeWrapper = Wrappers.lambdaQuery(MachineCheckedTakeDO.class).in(MachineCheckedTakeDO::getSerialNo, serialNos);
        checkTakeMapper.delete(revokeWrapper);
        LambdaQueryWrapper<MachineCheckedTakeDetailDO> detailWrapper = Wrappers.lambdaQuery(MachineCheckedTakeDetailDO.class).in(MachineCheckedTakeDetailDO::getSerialNo,
                serialNos);
        checkTakeDetailMapper.delete(detailWrapper);
    }

    public void signFinish(MachineSignFinishCmd finishCmd) {
        LambdaUpdateWrapper<MachineCheckedTakeDO> updateWrapper = Wrappers.lambdaUpdate(MachineCheckedTakeDO.class)
                .eq(MachineCheckedTakeDO::getSerialNo, finishCmd.getSerialNo())
                .set(MachineCheckedTakeDO::getSigned, finishCmd.getStatus() == ProcessStatus.FINISH)
                .set(MachineCheckedTakeDO::getSpNo, finishCmd.getSpNo())
                .set(MachineCheckedTakeDO::getStatus, finishCmd.getStatus());
        checkTakeMapper.update(updateWrapper);
    }

    public void sign(String serialNo, String spNo) {
        LambdaUpdateWrapper<MachineCheckedTakeDO> updateWrapper = Wrappers.lambdaUpdate(MachineCheckedTakeDO.class)
                .eq(MachineCheckedTakeDO::getSerialNo, serialNo)
                .set(MachineCheckedTakeDO::getStatus, ProcessStatus.WAIT_APPROVE)
                .set(MachineCheckedTakeDO::getSpNo, spNo);
        checkTakeMapper.update(updateWrapper);
    }

    public void importedByCheck(List<String> serialNos) {
        LambdaUpdateWrapper<MachineCheckedTakeDO> updateWrapper = Wrappers.lambdaUpdate(MachineCheckedTakeDO.class).in(MachineCheckedTakeDO::getSerialNo, serialNos)
                .set(MachineCheckedTakeDO::getImported, true);
        checkTakeMapper.update(updateWrapper);
    }
}
