package com.greenstone.mes.machine.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
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
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderExportQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.result.MachineReceiveExportR;
import com.greenstone.mes.machine.application.dto.result.MachineReceiveRecord;
import com.greenstone.mes.machine.domain.converter.MachineReceiveConverter;
import com.greenstone.mes.machine.domain.entity.MachineReceive;
import com.greenstone.mes.machine.infrastructure.mapper.MachineReceiveDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineReceiveMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineReceiveDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineReceiveDetailDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-12-08-9:57
 */
@Slf4j
@AllArgsConstructor
@Service
public class MachineReceiveRepository {
    private final MachineReceiveMapper receiveMapper;
    private final MachineReceiveDetailMapper receiveDetailMapper;
    private final MachineReceiveConverter converter;

    public List<MachineReceive> list(MachineFuzzyQuery fuzzyQuery) {
        QueryWrapper<MachineReceiveDO> fuzzyQueryWrapper = new QueryWrapper<>();
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
        List<MachineReceiveDO> machineReceiveDOS = receiveMapper.selectList(fuzzyQueryWrapper);
        return converter.dos2Entities(machineReceiveDOS);
    }

    public MachineReceive detail(String serialNo) {
        MachineReceiveDO select = MachineReceiveDO.builder().serialNo(serialNo).build();
        MachineReceiveDO requisitionDO = receiveMapper.getOneOnly(select);
        if (requisitionDO == null) {
            throw new ServiceException(MachineError.E200101);
        }
        List<MachineReceiveDetailDO> detailDOS = receiveDetailMapper.list(MachineReceiveDetailDO.builder().serialNo(serialNo).build());
        return converter.toMachineReceive(requisitionDO, detailDOS);
    }

    public void add(MachineReceive receive) {
        MachineReceiveDO receiveDO = converter.entity2Do(receive);
        receiveMapper.insert(receiveDO);
        List<MachineReceiveDetailDO> detailList = converter.detailEntities2Dos(receive.getParts());
        receiveDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void edit(MachineReceive receive) {
        MachineReceiveDO receiveDO = converter.entity2Do(receive);
        MachineReceiveDO revokeFound = receiveMapper.selectById(receive.getId());
        if (revokeFound == null) {
            throw new ServiceException(MachineError.E200101);
        }
        if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(MachineError.E200102);
        }
        receiveMapper.updateById(receiveDO);
        receiveDetailMapper.delete(MachineReceiveDetailDO.builder().serialNo(receive.getSerialNo()).build());
        List<MachineReceiveDetailDO> detailList = converter.detailEntities2Dos(receive.getParts());
        receiveDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void statusChange(MachineStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<MachineReceiveDO> updateWrapper = Wrappers.lambdaUpdate(MachineReceiveDO.class)
                .set(MachineReceiveDO::getStatus, statusChangeCmd.getStatus())
                .in(MachineReceiveDO::getSerialNo, statusChangeCmd.getSerialNos());
        receiveMapper.update(updateWrapper);
    }

    public void changeStatus(MachineReceive receive) {
        LambdaUpdateWrapper<MachineReceiveDO> updateWrapper = Wrappers.lambdaUpdate(MachineReceiveDO.class)
                .eq(MachineReceiveDO::getSerialNo, receive.getSerialNo())
                .set(MachineReceiveDO::getStatus, receive.getStatus());
        receiveMapper.update(updateWrapper);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            MachineReceiveDO revokeFound = receiveMapper.getOneOnly(MachineReceiveDO.builder().serialNo(serialNo).build());
            if (revokeFound == null) {
                throw new ServiceException(MachineError.E200101);
            }
            if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(MachineError.E200102);
            }
        }
        LambdaQueryWrapper<MachineReceiveDO> revokeWrapper = Wrappers.lambdaQuery(MachineReceiveDO.class).in(MachineReceiveDO::getSerialNo, serialNos);
        receiveMapper.delete(revokeWrapper);
        LambdaQueryWrapper<MachineReceiveDetailDO> detailWrapper = Wrappers.lambdaQuery(MachineReceiveDetailDO.class).in(MachineReceiveDetailDO::getSerialNo,
                serialNos);
        receiveDetailMapper.delete(detailWrapper);
    }

    @Transactional
    public void addReceiveBatch(List<MachineReceive> receiveList) {
        List<MachineReceiveDO> insertReceiveDOS = new ArrayList<>();
        List<MachineReceiveDetailDO> insertReceiveDetailDOS = new ArrayList<>();
        for (MachineReceive receive : receiveList) {
            MachineReceiveDO receiveDO = converter.entity2Do(receive);
            insertReceiveDOS.add(receiveDO);
            insertReceiveDetailDOS.addAll(converter.detailEntities2Dos(receive.getParts()));
        }
        log.info("插入收货单{}条", insertReceiveDOS.size());
        receiveMapper.insertBatchSomeColumn(insertReceiveDOS);
        log.info("插入收货单详情{}条", insertReceiveDetailDOS.size());
        receiveDetailMapper.insertBatchSomeColumn(insertReceiveDetailDOS);
    }

    public List<MachineReceiveExportR> selectExportDataList(MachineOrderExportQuery query) {
        DateTime parse;
        try {
            parse = DateUtil.parse(query.getMonth(), "yyyy-MM");
        } catch (Exception e) {
            throw new ServiceException("导出月份格式为：yyyy-MM");
        }
        LocalDate timeStart = DateUtil.beginOfMonth(parse).toLocalDateTime().toLocalDate();
        LocalDate timeEnd = DateUtil.endOfMonth(parse).toLocalDateTime().toLocalDate();
        query.setStart(timeStart);
        query.setEnd(timeEnd);

        return receiveMapper.selectExportDataList(query);
    }

    public List<MachineReceiveRecord> listRecord(MachineRecordQuery query) {
        return receiveMapper.listRecord(query);
    }

    public List<MachineReceiveDetailDO> selectDetailsByOrderSerialNos(List<String> serialNos) {
        LambdaQueryWrapper<MachineReceiveDetailDO> queryWrapper = Wrappers.lambdaQuery(MachineReceiveDetailDO.class)
                .select(MachineReceiveDetailDO::getSerialNo, MachineReceiveDetailDO::getRequirementSerialNo, MachineReceiveDetailDO::getOperation, MachineReceiveDetailDO::getOrderSerialNo, MachineReceiveDetailDO::getProjectCode, MachineReceiveDetailDO::getPartCode, MachineReceiveDetailDO::getPartVersion, MachineReceiveDetailDO::getActualNumber)
                .in(MachineReceiveDetailDO::getOrderSerialNo, serialNos);
        return receiveDetailMapper.selectList(queryWrapper);
    }

    public List<MachineReceiveDO> selectSubmitSerialNo(List<String> serialNos) {
        LambdaQueryWrapper<MachineReceiveDO> queryWrapper = Wrappers.lambdaQuery(MachineReceiveDO.class)
                .select(MachineReceiveDO::getSerialNo)
                .eq(MachineReceiveDO::getStatus, ProcessStatus.COMMITTED)
                .in(MachineReceiveDO::getSerialNo, serialNos);
        return receiveMapper.selectList(queryWrapper);
    }
}
