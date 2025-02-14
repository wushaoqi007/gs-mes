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
import com.greenstone.mes.machine.application.dto.result.MachineStockChangeRecord;
import com.greenstone.mes.machine.domain.converter.MachineStockChangeConverter;
import com.greenstone.mes.machine.domain.entity.MachineStockChange;
import com.greenstone.mes.machine.infrastructure.mapper.MachineStockChangeDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineStockChangeMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineStockChangeDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineStockChangeDetailDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class MachineStockChangeRepository {
    private final MachineStockChangeMapper stockChangeMapper;
    private final MachineStockChangeDetailMapper stockChangeDetailMapper;
    private final MachineStockChangeConverter converter;

    public List<MachineStockChange> list(MachineFuzzyQuery fuzzyQuery) {
        QueryWrapper<MachineStockChangeDO> fuzzyQueryWrapper = new QueryWrapper<>();
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
        List<MachineStockChangeDO> machineStockChangeDOS = stockChangeMapper.selectList(fuzzyQueryWrapper);
        return converter.dos2Entities(machineStockChangeDOS);
    }

    public MachineStockChange detail(String serialNo) {
        MachineStockChangeDO select = MachineStockChangeDO.builder().serialNo(serialNo).build();
        MachineStockChangeDO requisitionDO = stockChangeMapper.getOneOnly(select);
        if (requisitionDO == null) {
            throw new ServiceException(MachineError.E200101);
        }
        List<MachineStockChangeDetailDO> detailDOS = stockChangeDetailMapper.list(MachineStockChangeDetailDO.builder().serialNo(serialNo).build());
        return converter.toMachineStockChange(requisitionDO, detailDOS);
    }

    public void add(MachineStockChange stockChange) {
        MachineStockChangeDO stockChangeDO = converter.entity2Do(stockChange);
        stockChangeMapper.insert(stockChangeDO);
        List<MachineStockChangeDetailDO> detailList = converter.detailEntities2Dos(stockChange.getParts());
        stockChangeDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void edit(MachineStockChange stockChange) {
        MachineStockChangeDO stockChangeDO = converter.entity2Do(stockChange);
        MachineStockChangeDO revokeFound = stockChangeMapper.selectById(stockChange.getId());
        if (revokeFound == null) {
            throw new ServiceException(MachineError.E200101);
        }
        if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(MachineError.E200102);
        }
        stockChangeMapper.updateById(stockChangeDO);
        stockChangeDetailMapper.delete(MachineStockChangeDetailDO.builder().serialNo(stockChange.getSerialNo()).build());
        List<MachineStockChangeDetailDO> detailList = converter.detailEntities2Dos(stockChange.getParts());
        stockChangeDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void statusChange(MachineStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<MachineStockChangeDO> updateWrapper = Wrappers.lambdaUpdate(MachineStockChangeDO.class).set(MachineStockChangeDO::getStatus, statusChangeCmd.getStatus())
                .in(MachineStockChangeDO::getSerialNo, statusChangeCmd.getSerialNos());
        stockChangeMapper.update(updateWrapper);
    }

    public void changeStatus(MachineStockChange stockChange) {
        LambdaUpdateWrapper<MachineStockChangeDO> updateWrapper = Wrappers.lambdaUpdate(MachineStockChangeDO.class)
                .eq(MachineStockChangeDO::getSerialNo, stockChange.getSerialNo())
                .set(MachineStockChangeDO::getStatus, stockChange.getStatus());
        stockChangeMapper.update(updateWrapper);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            MachineStockChangeDO revokeFound = stockChangeMapper.getOneOnly(MachineStockChangeDO.builder().serialNo(serialNo).build());
            if (revokeFound == null) {
                throw new ServiceException(MachineError.E200101);
            }
            if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(MachineError.E200102);
            }
        }
        LambdaQueryWrapper<MachineStockChangeDO> revokeWrapper = Wrappers.lambdaQuery(MachineStockChangeDO.class).in(MachineStockChangeDO::getSerialNo, serialNos);
        stockChangeMapper.delete(revokeWrapper);
        LambdaQueryWrapper<MachineStockChangeDetailDO> detailWrapper = Wrappers.lambdaQuery(MachineStockChangeDetailDO.class).in(MachineStockChangeDetailDO::getSerialNo,
                serialNos);
        stockChangeDetailMapper.delete(detailWrapper);
    }

    public List<MachineStockChangeRecord> listRecord(MachineRecordQuery query) {
        return stockChangeMapper.listRecord(query);
    }
}
