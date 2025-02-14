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
import com.greenstone.mes.machine.domain.converter.MachineMaterialReturnConverter;
import com.greenstone.mes.machine.domain.entity.MachineMaterialReturn;
import com.greenstone.mes.machine.infrastructure.mapper.MachineMaterialReturnDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineMaterialReturnMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineMaterialReturnDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineMaterialReturnDetailDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class MachineMaterialReturnRepository {
    private final MachineMaterialReturnMapper materialReturnMapper;
    private final MachineMaterialReturnDetailMapper materialReturnDetailMapper;
    private final MachineMaterialReturnConverter converter;

    public List<MachineMaterialReturn> list(MachineFuzzyQuery fuzzyQuery) {
        QueryWrapper<MachineMaterialReturnDO> fuzzyQueryWrapper = new QueryWrapper<>();
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
        List<MachineMaterialReturnDO> machineMaterialReturnDOS = materialReturnMapper.selectList(fuzzyQueryWrapper);
        List<MachineMaterialReturn> machineMaterialReturns = converter.dos2Entities(machineMaterialReturnDOS);
        if (CollUtil.isNotEmpty(machineMaterialReturns)) {
            for (MachineMaterialReturn machineMaterialReturn : machineMaterialReturns) {
                List<MachineMaterialReturnDetailDO> detailDOS = materialReturnDetailMapper.list(MachineMaterialReturnDetailDO.builder().serialNo(machineMaterialReturn.getSerialNo()).build());
                machineMaterialReturn.setParts(converter.detailDos2Entities(detailDOS));
            }
        }
        return machineMaterialReturns;
    }

    public MachineMaterialReturn detail(String serialNo) {
        MachineMaterialReturnDO select = MachineMaterialReturnDO.builder().serialNo(serialNo).build();
        MachineMaterialReturnDO requisitionDO = materialReturnMapper.getOneOnly(select);
        if (requisitionDO == null) {
            throw new ServiceException(MachineError.E200101);
        }
        List<MachineMaterialReturnDetailDO> detailDOS = materialReturnDetailMapper.list(MachineMaterialReturnDetailDO.builder().serialNo(serialNo).build());
        return converter.toMachineMaterialReturn(requisitionDO, detailDOS);
    }

    public void add(MachineMaterialReturn materialReturn) {
        MachineMaterialReturnDO materialReturnDO = converter.entity2Do(materialReturn);
        materialReturnMapper.insert(materialReturnDO);
        List<MachineMaterialReturnDetailDO> detailList = converter.detailEntities2Dos(materialReturn.getParts());
        materialReturnDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void edit(MachineMaterialReturn materialReturn) {
        MachineMaterialReturnDO materialReturnDO = converter.entity2Do(materialReturn);
        MachineMaterialReturnDO revokeFound = materialReturnMapper.selectById(materialReturn.getId());
        if (revokeFound == null) {
            throw new ServiceException(MachineError.E200101);
        }
        if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(MachineError.E200102);
        }
        materialReturnMapper.updateById(materialReturnDO);
        materialReturnDetailMapper.delete(MachineMaterialReturnDetailDO.builder().serialNo(materialReturn.getSerialNo()).build());
        List<MachineMaterialReturnDetailDO> detailList = converter.detailEntities2Dos(materialReturn.getParts());
        materialReturnDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void statusChange(MachineStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<MachineMaterialReturnDO> updateWrapper = Wrappers.lambdaUpdate(MachineMaterialReturnDO.class).set(MachineMaterialReturnDO::getStatus, statusChangeCmd.getStatus())
                .in(MachineMaterialReturnDO::getSerialNo, statusChangeCmd.getSerialNos());
        materialReturnMapper.update(updateWrapper);
    }

    public void changeStatus(MachineMaterialReturn materialReturn) {
        LambdaUpdateWrapper<MachineMaterialReturnDO> updateWrapper = Wrappers.lambdaUpdate(MachineMaterialReturnDO.class)
                .eq(MachineMaterialReturnDO::getSerialNo, materialReturn.getSerialNo())
                .set(MachineMaterialReturnDO::getStatus, materialReturn.getStatus());
        materialReturnMapper.update(updateWrapper);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            MachineMaterialReturnDO revokeFound = materialReturnMapper.getOneOnly(MachineMaterialReturnDO.builder().serialNo(serialNo).build());
            if (revokeFound == null) {
                throw new ServiceException(MachineError.E200101);
            }
            if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(MachineError.E200102);
            }
        }
        LambdaQueryWrapper<MachineMaterialReturnDO> revokeWrapper = Wrappers.lambdaQuery(MachineMaterialReturnDO.class).in(MachineMaterialReturnDO::getSerialNo, serialNos);
        materialReturnMapper.delete(revokeWrapper);
        LambdaQueryWrapper<MachineMaterialReturnDetailDO> detailWrapper = Wrappers.lambdaQuery(MachineMaterialReturnDetailDO.class).in(MachineMaterialReturnDetailDO::getSerialNo,
                serialNos);
        materialReturnDetailMapper.delete(detailWrapper);
    }

}
