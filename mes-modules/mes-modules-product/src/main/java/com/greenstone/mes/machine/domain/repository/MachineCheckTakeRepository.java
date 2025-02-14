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
import com.greenstone.mes.machine.application.dto.result.MachineCheckTakeRecord;
import com.greenstone.mes.machine.domain.converter.MachineCheckTakeConverter;
import com.greenstone.mes.machine.domain.entity.MachineCheckTake;
import com.greenstone.mes.machine.dto.cmd.MachineSignFinishCmd;
import com.greenstone.mes.machine.infrastructure.mapper.MachineCheckTakeDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineCheckTakeMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCheckTakeDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCheckTakeDetailDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class MachineCheckTakeRepository {
    private final MachineCheckTakeMapper checkTakeMapper;
    private final MachineCheckTakeDetailMapper checkTakeDetailMapper;
    private final MachineCheckTakeConverter converter;

    public List<MachineCheckTake> list(MachineFuzzyQuery fuzzyQuery) {
        QueryWrapper<MachineCheckTakeDO> fuzzyQueryWrapper = new QueryWrapper<>();
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
        List<MachineCheckTakeDO> machineCheckTakeDOS = checkTakeMapper.selectList(fuzzyQueryWrapper);
        List<MachineCheckTake> machineCheckTakes = converter.dos2Entities(machineCheckTakeDOS);
        for (MachineCheckTake machineCheckTake : machineCheckTakes) {
            List<MachineCheckTakeDetailDO> detailDOS = checkTakeDetailMapper.list(MachineCheckTakeDetailDO.builder().serialNo(machineCheckTake.getSerialNo()).build());
            machineCheckTake.setParts(converter.detailDos2Entities(detailDOS));
        }
        return machineCheckTakes;
    }

    public List<MachineCheckTakeRecord> listRecord(MachineRecordFuzzyQuery query) {
        return checkTakeMapper.listRecord(query);
    }

    public MachineCheckTake detail(String serialNo) {
        MachineCheckTakeDO select = MachineCheckTakeDO.builder().serialNo(serialNo).build();
        MachineCheckTakeDO requisitionDO = checkTakeMapper.getOneOnly(select);
        if (requisitionDO == null) {
            throw new ServiceException(MachineError.E200101);
        }
        List<MachineCheckTakeDetailDO> detailDOS = checkTakeDetailMapper.list(MachineCheckTakeDetailDO.builder().serialNo(serialNo).build());
        return converter.toMachineCheckTake(requisitionDO, detailDOS);
    }

    public MachineCheckTake getMachineCheckTake(String serialNo) {
        MachineCheckTakeDO select = MachineCheckTakeDO.builder().serialNo(serialNo).build();
        MachineCheckTakeDO getOne = checkTakeMapper.getOneOnly(select);
        if (getOne == null) {
            throw new ServiceException(MachineError.E200101);
        }
        return converter.do2Entity(getOne);
    }

    public void add(MachineCheckTake check) {
        MachineCheckTakeDO checkDO = converter.entity2Do(check);
        checkTakeMapper.insert(checkDO);
        List<MachineCheckTakeDetailDO> detailList = converter.detailEntities2Dos(check.getParts());
        checkTakeDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void edit(MachineCheckTake check) {
        MachineCheckTakeDO checkDO = converter.entity2Do(check);
        MachineCheckTakeDO revokeFound = checkTakeMapper.selectById(check.getId());
        if (revokeFound == null) {
            throw new ServiceException(MachineError.E200101);
        }
        if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(MachineError.E200102);
        }
        checkTakeMapper.updateById(checkDO);
        checkTakeDetailMapper.delete(MachineCheckTakeDetailDO.builder().serialNo(check.getSerialNo()).build());
        List<MachineCheckTakeDetailDO> detailList = converter.detailEntities2Dos(check.getParts());
        checkTakeDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void statusChange(MachineStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<MachineCheckTakeDO> updateWrapper = Wrappers.lambdaUpdate(MachineCheckTakeDO.class).set(MachineCheckTakeDO::getStatus, statusChangeCmd.getStatus())
                .in(MachineCheckTakeDO::getSerialNo, statusChangeCmd.getSerialNos());
        checkTakeMapper.update(updateWrapper);
    }

    public void changeStatus(MachineCheckTake check) {
        LambdaUpdateWrapper<MachineCheckTakeDO> updateWrapper = Wrappers.lambdaUpdate(MachineCheckTakeDO.class)
                .eq(MachineCheckTakeDO::getSerialNo, check.getSerialNo())
                .set(MachineCheckTakeDO::getStatus, check.getStatus());
        checkTakeMapper.update(updateWrapper);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            MachineCheckTakeDO revokeFound = checkTakeMapper.getOneOnly(MachineCheckTakeDO.builder().serialNo(serialNo).build());
            if (revokeFound == null) {
                throw new ServiceException(MachineError.E200101);
            }
            if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(MachineError.E200102);
            }
        }
        LambdaQueryWrapper<MachineCheckTakeDO> revokeWrapper = Wrappers.lambdaQuery(MachineCheckTakeDO.class).in(MachineCheckTakeDO::getSerialNo, serialNos);
        checkTakeMapper.delete(revokeWrapper);
        LambdaQueryWrapper<MachineCheckTakeDetailDO> detailWrapper = Wrappers.lambdaQuery(MachineCheckTakeDetailDO.class).in(MachineCheckTakeDetailDO::getSerialNo,
                serialNos);
        checkTakeDetailMapper.delete(detailWrapper);
    }

    public void signFinish(MachineSignFinishCmd finishCmd) {
        LambdaUpdateWrapper<MachineCheckTakeDO> updateWrapper = Wrappers.lambdaUpdate(MachineCheckTakeDO.class)
                .eq(MachineCheckTakeDO::getSerialNo, finishCmd.getSerialNo())
                .set(MachineCheckTakeDO::getSigned, finishCmd.getStatus() == ProcessStatus.FINISH)
                .set(MachineCheckTakeDO::getSpNo, finishCmd.getSpNo())
                .set(MachineCheckTakeDO::getStatus, finishCmd.getStatus());
        checkTakeMapper.update(updateWrapper);
    }

    public void sign(String serialNo, String spNo) {
        LambdaUpdateWrapper<MachineCheckTakeDO> updateWrapper = Wrappers.lambdaUpdate(MachineCheckTakeDO.class)
                .eq(MachineCheckTakeDO::getSerialNo, serialNo)
                .set(MachineCheckTakeDO::getStatus, ProcessStatus.WAIT_APPROVE)
                .set(MachineCheckTakeDO::getSpNo, spNo);
        checkTakeMapper.update(updateWrapper);
    }

    public void importedByCheck(List<String> serialNos) {
        LambdaUpdateWrapper<MachineCheckTakeDO> updateWrapper = Wrappers.lambdaUpdate(MachineCheckTakeDO.class).in(MachineCheckTakeDO::getSerialNo, serialNos)
                .set(MachineCheckTakeDO::getImported, true);
        checkTakeMapper.update(updateWrapper);
    }
}
