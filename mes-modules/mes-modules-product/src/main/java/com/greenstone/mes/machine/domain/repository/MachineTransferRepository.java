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
import com.greenstone.mes.machine.domain.converter.MachineTransferConverter;
import com.greenstone.mes.machine.domain.entity.MachineTransfer;
import com.greenstone.mes.machine.infrastructure.mapper.MachineTransferDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineTransferMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineTransferDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineTransferDetailDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class MachineTransferRepository {
    private final MachineTransferMapper transferMapper;
    private final MachineTransferDetailMapper transferDetailMapper;
    private final MachineTransferConverter converter;

    public List<MachineTransfer> list(MachineFuzzyQuery fuzzyQuery) {
        QueryWrapper<MachineTransferDO> fuzzyQueryWrapper = new QueryWrapper<>();
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
        List<MachineTransferDO> machineTransferDOS = transferMapper.selectList(fuzzyQueryWrapper);
        return converter.dos2Entities(machineTransferDOS);
    }

    public MachineTransfer detail(String serialNo) {
        MachineTransferDO select = MachineTransferDO.builder().serialNo(serialNo).build();
        MachineTransferDO requisitionDO = transferMapper.getOneOnly(select);
        if (requisitionDO == null) {
            throw new ServiceException(MachineError.E200101);
        }
        List<MachineTransferDetailDO> detailDOS = transferDetailMapper.list(MachineTransferDetailDO.builder().serialNo(serialNo).build());
        return converter.toMachineTransfer(requisitionDO, detailDOS);
    }

    public void add(MachineTransfer transfer) {
        MachineTransferDO transferDO = converter.entity2Do(transfer);
        transferMapper.insert(transferDO);
        List<MachineTransferDetailDO> detailList = converter.detailEntities2Dos(transfer.getParts());
        transferDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void edit(MachineTransfer transfer) {
        MachineTransferDO transferDO = converter.entity2Do(transfer);
        MachineTransferDO revokeFound = transferMapper.selectById(transfer.getId());
        if (revokeFound == null) {
            throw new ServiceException(MachineError.E200101);
        }
        if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(MachineError.E200102);
        }
        transferMapper.updateById(transferDO);
        transferDetailMapper.delete(MachineTransferDetailDO.builder().serialNo(transfer.getSerialNo()).build());
        List<MachineTransferDetailDO> detailList = converter.detailEntities2Dos(transfer.getParts());
        transferDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void statusChange(MachineStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<MachineTransferDO> updateWrapper = Wrappers.lambdaUpdate(MachineTransferDO.class).set(MachineTransferDO::getStatus, statusChangeCmd.getStatus())
                .in(MachineTransferDO::getSerialNo, statusChangeCmd.getSerialNos());
        transferMapper.update(updateWrapper);
    }

    public void changeStatus(MachineTransfer transfer) {
        LambdaUpdateWrapper<MachineTransferDO> updateWrapper = Wrappers.lambdaUpdate(MachineTransferDO.class)
                .eq(MachineTransferDO::getSerialNo, transfer.getSerialNo())
                .set(MachineTransferDO::getStatus, transfer.getStatus());
        transferMapper.update(updateWrapper);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            MachineTransferDO revokeFound = transferMapper.getOneOnly(MachineTransferDO.builder().serialNo(serialNo).build());
            if (revokeFound == null) {
                throw new ServiceException(MachineError.E200101);
            }
            if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(MachineError.E200102);
            }
        }
        LambdaQueryWrapper<MachineTransferDO> revokeWrapper = Wrappers.lambdaQuery(MachineTransferDO.class).in(MachineTransferDO::getSerialNo, serialNos);
        transferMapper.delete(revokeWrapper);
        LambdaQueryWrapper<MachineTransferDetailDO> detailWrapper = Wrappers.lambdaQuery(MachineTransferDetailDO.class).in(MachineTransferDetailDO::getSerialNo,
                serialNos);
        transferDetailMapper.delete(detailWrapper);
    }

}
