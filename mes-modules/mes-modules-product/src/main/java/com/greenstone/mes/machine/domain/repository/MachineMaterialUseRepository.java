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
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineMaterialUseFinishCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStatusChangeCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.domain.converter.MachineMaterialUseConverter;
import com.greenstone.mes.machine.domain.entity.MachineMaterialUse;
import com.greenstone.mes.machine.infrastructure.enums.UseStatus;
import com.greenstone.mes.machine.infrastructure.mapper.MachineMaterialUseDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineMaterialUseMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineMaterialUseDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineMaterialUseDetailDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class MachineMaterialUseRepository {
    private final MachineMaterialUseMapper materialUseMapper;
    private final MachineMaterialUseDetailMapper materialUseDetailMapper;
    private final MachineMaterialUseConverter converter;

    public List<MachineMaterialUse> list(MachineFuzzyQuery fuzzyQuery) {
        QueryWrapper<MachineMaterialUseDO> fuzzyQueryWrapper = new QueryWrapper<>();
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
        List<MachineMaterialUseDO> machineMaterialUseDOS = materialUseMapper.selectList(fuzzyQueryWrapper);
        List<MachineMaterialUse> machineMaterialUses = converter.dos2Entities(machineMaterialUseDOS);
        if (CollUtil.isNotEmpty(machineMaterialUses)) {
            for (MachineMaterialUse machineMaterialUs : machineMaterialUses) {
                List<MachineMaterialUseDetailDO> detailDOS = materialUseDetailMapper.list(MachineMaterialUseDetailDO.builder().serialNo(machineMaterialUs.getSerialNo()).build());
                machineMaterialUs.setParts(converter.detailDos2Entities(detailDOS));
            }
        }
        return machineMaterialUses;
    }

    public MachineMaterialUse detail(String serialNo) {
        MachineMaterialUseDO select = MachineMaterialUseDO.builder().serialNo(serialNo).build();
        MachineMaterialUseDO requisitionDO = materialUseMapper.getOneOnly(select);
        if (requisitionDO == null) {
            throw new ServiceException(MachineError.E200101);
        }
        List<MachineMaterialUseDetailDO> detailDOS = materialUseDetailMapper.list(MachineMaterialUseDetailDO.builder().serialNo(serialNo).build());
        return converter.toMachineMaterialUse(requisitionDO, detailDOS);
    }

    public void add(MachineMaterialUse materialUse) {
        MachineMaterialUseDO materialUseDO = converter.entity2Do(materialUse);
        materialUseMapper.insert(materialUseDO);
        List<MachineMaterialUseDetailDO> detailList = converter.detailEntities2Dos(materialUse.getParts());
        materialUseDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void edit(MachineMaterialUse materialUse) {
        MachineMaterialUseDO materialUseDO = converter.entity2Do(materialUse);
        MachineMaterialUseDO revokeFound = materialUseMapper.selectById(materialUse.getId());
        if (revokeFound == null) {
            throw new ServiceException(MachineError.E200101);
        }
        if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(MachineError.E200102);
        }
        materialUseMapper.updateById(materialUseDO);
        materialUseDetailMapper.delete(MachineMaterialUseDetailDO.builder().serialNo(materialUse.getSerialNo()).build());
        List<MachineMaterialUseDetailDO> detailList = converter.detailEntities2Dos(materialUse.getParts());
        materialUseDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void statusChange(MachineStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<MachineMaterialUseDO> updateWrapper = Wrappers.lambdaUpdate(MachineMaterialUseDO.class).set(MachineMaterialUseDO::getStatus, statusChangeCmd.getStatus())
                .in(MachineMaterialUseDO::getSerialNo, statusChangeCmd.getSerialNos());
        materialUseMapper.update(updateWrapper);
    }

    public void changeStatus(MachineMaterialUse materialUse) {
        LambdaUpdateWrapper<MachineMaterialUseDO> updateWrapper = Wrappers.lambdaUpdate(MachineMaterialUseDO.class)
                .eq(MachineMaterialUseDO::getSerialNo, materialUse.getSerialNo())
                .set(MachineMaterialUseDO::getStatus, materialUse.getStatus());
        materialUseMapper.update(updateWrapper);
    }

    public void finish(MachineMaterialUseFinishCmd finishCmd) {
        LambdaUpdateWrapper<MachineMaterialUseDO> updateWrapper = Wrappers.lambdaUpdate(MachineMaterialUseDO.class)
                .set(MachineMaterialUseDO::getUseStatus, UseStatus.ALL.getCode())
                .in(MachineMaterialUseDO::getSerialNo, finishCmd.getSerialNos());
        materialUseMapper.update(updateWrapper);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            MachineMaterialUseDO revokeFound = materialUseMapper.getOneOnly(MachineMaterialUseDO.builder().serialNo(serialNo).build());
            if (revokeFound == null) {
                throw new ServiceException(MachineError.E200101);
            }
            if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(MachineError.E200102);
            }
        }
        LambdaQueryWrapper<MachineMaterialUseDO> revokeWrapper = Wrappers.lambdaQuery(MachineMaterialUseDO.class).in(MachineMaterialUseDO::getSerialNo, serialNos);
        materialUseMapper.delete(revokeWrapper);
        LambdaQueryWrapper<MachineMaterialUseDetailDO> detailWrapper = Wrappers.lambdaQuery(MachineMaterialUseDetailDO.class).in(MachineMaterialUseDetailDO::getSerialNo,
                serialNos);
        materialUseDetailMapper.delete(detailWrapper);
    }

}
