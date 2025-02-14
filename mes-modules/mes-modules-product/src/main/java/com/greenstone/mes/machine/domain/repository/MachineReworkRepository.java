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
import com.greenstone.mes.machine.application.dto.result.MachineReworkRecord;
import com.greenstone.mes.machine.domain.converter.MachineReworkConverter;
import com.greenstone.mes.machine.domain.entity.MachineRework;
import com.greenstone.mes.machine.infrastructure.mapper.MachineReworkDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineReworkMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineReworkDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineReworkDetailDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class MachineReworkRepository {
    private final MachineReworkMapper reworkMapper;
    private final MachineReworkDetailMapper reworkDetailMapper;
    private final MachineReworkConverter converter;

    public List<MachineRework> list(MachineFuzzyQuery fuzzyQuery) {
        QueryWrapper<MachineReworkDO> fuzzyQueryWrapper = new QueryWrapper<>();
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
        List<MachineReworkDO> machineReworkDOS = reworkMapper.selectList(fuzzyQueryWrapper);
        return converter.dos2Entities(machineReworkDOS);
    }

    public MachineRework detail(String serialNo) {
        MachineReworkDO select = MachineReworkDO.builder().serialNo(serialNo).build();
        MachineReworkDO requisitionDO = reworkMapper.getOneOnly(select);
        if (requisitionDO == null) {
            throw new ServiceException(MachineError.E200101);
        }
        List<MachineReworkDetailDO> detailDOS = reworkDetailMapper.list(MachineReworkDetailDO.builder().serialNo(serialNo).build());
        return converter.toMachineRework(requisitionDO, detailDOS);
    }

    public List<MachineReworkRecord> listRecord(MachineRecordFuzzyQuery query) {
        return reworkMapper.listRecord(query);
    }

    public void add(MachineRework rework) {
        MachineReworkDO reworkDO = converter.entity2Do(rework);
        reworkMapper.insert(reworkDO);
        List<MachineReworkDetailDO> detailList = converter.detailEntities2Dos(rework.getParts());
        reworkDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void edit(MachineRework rework) {
        MachineReworkDO reworkDO = converter.entity2Do(rework);
        MachineReworkDO revokeFound = reworkMapper.selectById(rework.getId());
        if (revokeFound == null) {
            throw new ServiceException(MachineError.E200101);
        }
        if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(MachineError.E200102);
        }
        reworkMapper.updateById(reworkDO);
        reworkDetailMapper.delete(MachineReworkDetailDO.builder().serialNo(rework.getSerialNo()).build());
        List<MachineReworkDetailDO> detailList = converter.detailEntities2Dos(rework.getParts());
        reworkDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void statusChange(MachineStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<MachineReworkDO> updateWrapper = Wrappers.lambdaUpdate(MachineReworkDO.class).set(MachineReworkDO::getStatus, statusChangeCmd.getStatus())
                .in(MachineReworkDO::getSerialNo, statusChangeCmd.getSerialNos());
        reworkMapper.update(updateWrapper);
    }

    public void changeStatus(MachineRework rework) {
        LambdaUpdateWrapper<MachineReworkDO> updateWrapper = Wrappers.lambdaUpdate(MachineReworkDO.class)
                .eq(MachineReworkDO::getSerialNo, rework.getSerialNo())
                .set(MachineReworkDO::getStatus, rework.getStatus());
        reworkMapper.update(updateWrapper);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            MachineReworkDO revokeFound = reworkMapper.getOneOnly(MachineReworkDO.builder().serialNo(serialNo).build());
            if (revokeFound == null) {
                throw new ServiceException(MachineError.E200101);
            }
            if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(MachineError.E200102);
            }
        }
        LambdaQueryWrapper<MachineReworkDO> revokeWrapper = Wrappers.lambdaQuery(MachineReworkDO.class).in(MachineReworkDO::getSerialNo, serialNos);
        reworkMapper.delete(revokeWrapper);
        LambdaQueryWrapper<MachineReworkDetailDO> detailWrapper = Wrappers.lambdaQuery(MachineReworkDetailDO.class).in(MachineReworkDetailDO::getSerialNo,
                serialNos);
        reworkDetailMapper.delete(detailWrapper);
    }

}
