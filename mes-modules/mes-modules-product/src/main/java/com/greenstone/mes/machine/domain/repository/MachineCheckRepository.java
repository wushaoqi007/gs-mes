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
import com.greenstone.mes.machine.application.dto.cqe.query.*;
import com.greenstone.mes.machine.application.dto.result.MachineCheckPartR;
import com.greenstone.mes.machine.application.dto.result.MachineCheckPartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineCheckRecord;
import com.greenstone.mes.machine.domain.converter.MachineCheckConverter;
import com.greenstone.mes.machine.domain.entity.MachineCheck;
import com.greenstone.mes.machine.domain.entity.MachineCheckDetail;
import com.greenstone.mes.machine.infrastructure.enums.CheckResultType;
import com.greenstone.mes.machine.infrastructure.mapper.MachineCheckDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineCheckMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCheckDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCheckDetailDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class MachineCheckRepository {
    private final MachineCheckMapper checkMapper;
    private final MachineCheckDetailMapper checkDetailMapper;
    private final MachineCheckConverter converter;

    public List<MachineCheck> list(MachineFuzzyQuery fuzzyQuery) {
        QueryWrapper<MachineCheckDO> fuzzyQueryWrapper = new QueryWrapper<>();
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
        List<MachineCheckDO> machineCheckDOS = checkMapper.selectList(fuzzyQueryWrapper);
        return converter.dos2Entities(machineCheckDOS);
    }

    public MachineCheck detail(String serialNo) {
        MachineCheckDO select = MachineCheckDO.builder().serialNo(serialNo).build();
        MachineCheckDO requisitionDO = checkMapper.getOneOnly(select);
        if (requisitionDO == null) {
            throw new ServiceException(MachineError.E200101);
        }
        List<MachineCheckDetailDO> detailDOS = checkDetailMapper.list(MachineCheckDetailDO.builder().serialNo(serialNo).build());
        return converter.toMachineCheck(requisitionDO, detailDOS);
    }

    public void add(MachineCheck check) {
        MachineCheckDO checkDO = converter.entity2Do(check);
        checkMapper.insert(checkDO);
        List<MachineCheckDetailDO> detailList = converter.detailEntities2Dos(check.getParts());
        checkDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void edit(MachineCheck check) {
        MachineCheckDO checkDO = converter.entity2Do(check);
        MachineCheckDO revokeFound = checkMapper.selectById(check.getId());
        if (revokeFound == null) {
            throw new ServiceException(MachineError.E200101);
        }
        if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(MachineError.E200102);
        }
        checkMapper.updateById(checkDO);
        checkDetailMapper.delete(MachineCheckDetailDO.builder().serialNo(check.getSerialNo()).build());
        List<MachineCheckDetailDO> detailList = converter.detailEntities2Dos(check.getParts());
        checkDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void statusChange(MachineStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<MachineCheckDO> updateWrapper = Wrappers.lambdaUpdate(MachineCheckDO.class).set(MachineCheckDO::getStatus, statusChangeCmd.getStatus())
                .in(MachineCheckDO::getSerialNo, statusChangeCmd.getSerialNos());
        checkMapper.update(updateWrapper);
    }

    public void changeStatus(MachineCheck check) {
        LambdaUpdateWrapper<MachineCheckDO> updateWrapper = Wrappers.lambdaUpdate(MachineCheckDO.class)
                .eq(MachineCheckDO::getSerialNo, check.getSerialNo())
                .set(MachineCheckDO::getStatus, check.getStatus());
        checkMapper.update(updateWrapper);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            MachineCheckDO revokeFound = checkMapper.getOneOnly(MachineCheckDO.builder().serialNo(serialNo).build());
            if (revokeFound == null) {
                throw new ServiceException(MachineError.E200101);
            }
            if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(MachineError.E200102);
            }
        }
        LambdaQueryWrapper<MachineCheckDO> revokeWrapper = Wrappers.lambdaQuery(MachineCheckDO.class).in(MachineCheckDO::getSerialNo, serialNos);
        checkMapper.delete(revokeWrapper);
        LambdaQueryWrapper<MachineCheckDetailDO> detailWrapper = Wrappers.lambdaQuery(MachineCheckDetailDO.class).in(MachineCheckDetailDO::getSerialNo,
                serialNos);
        checkDetailMapper.delete(detailWrapper);
    }

    public List<MachineCheckPartR> selectPartList(MachineCheckPartListQuery query) {
        return checkMapper.selectPartList(query);
    }

    public MachineCheckPartR selectPart(MachineCheckPartScanQuery query) {
        MachineCheckDetailDO detailDO = checkDetailMapper.getOneOnly(MachineCheckDetailDO.builder().serialNo(query.getCheckSerialNo())
                .orderSerialNo(query.getOrderSerialNo()).requirementSerialNo(query.getRequirementSerialNo()).projectCode(query.getProjectCode())
                .partCode(query.getPartCode()).partVersion(query.getPartVersion()).build());
        return converter.toCheckPartR(detailDO);
    }

    public List<MachineCheckDetail> selectReworkDetails(MachinePartScanQuery2 scanQuery) {
        return checkMapper.selectReworkDetails(scanQuery);
    }

    public MachineCheckPartStockR scanPart(MachineCheckPartScanQuery part) {
        log.info("查询质检单零件，参数:{}", part);
        MachineCheckDetailDO selectWrapper = MachineCheckDetailDO.builder()
                .projectCode(part.getProjectCode()).orderSerialNo(part.getOrderSerialNo())
                .partCode(part.getPartCode()).partVersion(part.getPartVersion())
                .checkResultType(part.getCheckResultType()).build();
        MachineCheckDetailDO detailDO = checkDetailMapper.getOneOnly(selectWrapper);
        if (Objects.isNull(detailDO)) {
            throw new ServiceException(MachineError.E200310, StrUtil.format("质检结果为{}的零件号/版本：{}/{}，", part.getCheckResultType().getName(), part.getPartCode(), part.getPartVersion()));
        }
        return converter.toCheckPartStockR(detailDO);
    }

    public void resultEntry(List<MachineCheckDetail> updateList) {
        List<MachineCheckDetailDO> machineCheckDetailDOS = converter.detailEntities2Dos(updateList);
        for (MachineCheckDetailDO machineCheckDetailDO : machineCheckDetailDOS) {
            checkDetailMapper.updateById(machineCheckDetailDO);
        }
    }

    public void isFinished(String checkSerialNo) {
        LambdaQueryWrapper<MachineCheckDetailDO> detailWrapper = Wrappers.lambdaQuery(MachineCheckDetailDO.class)
                .select(MachineCheckDetailDO::getToBeCheckedNumber)
                .eq(MachineCheckDetailDO::getSerialNo, checkSerialNo);
        List<MachineCheckDetailDO> machineCheckDetailDOS = checkDetailMapper.selectList(detailWrapper);
        if (CollUtil.isNotEmpty(machineCheckDetailDOS)) {
            long sum = machineCheckDetailDOS.stream().mapToLong(MachineCheckDetailDO::getToBeCheckedNumber).sum();
            if (sum == 0) {
                LambdaUpdateWrapper<MachineCheckDO> updateWrapper = Wrappers.lambdaUpdate(MachineCheckDO.class)
                        .set(MachineCheckDO::getFinished, 1).eq(MachineCheckDO::getSerialNo, checkSerialNo);
                checkMapper.update(updateWrapper);
            }
        }
    }

    public List<MachineCheckRecord> listRecord(MachineCheckPartListQuery query) {
        return checkMapper.listRecord(query);
    }

    public List<MachineCheckRecord> reworkRecord(MachineRecordQuery query) {
        LambdaQueryWrapper<MachineCheckDetailDO> queryWrapper = Wrappers.lambdaQuery(MachineCheckDetailDO.class)
                .eq(MachineCheckDetailDO::getCheckResultType, CheckResultType.REWORK)
                .eq(StrUtil.isNotEmpty(query.getProjectCode()), MachineCheckDetailDO::getProjectCode, query.getProjectCode())
                .eq(StrUtil.isNotEmpty(query.getPartCode()), MachineCheckDetailDO::getPartCode, query.getPartCode())
                .eq(StrUtil.isNotEmpty(query.getProvider()), MachineCheckDetailDO::getProvider, query.getProvider())
                .ge(Objects.nonNull(query.getStartDate()), MachineCheckDetailDO::getCheckDate, query.getStartDate())
                .le(Objects.nonNull(query.getEndDate()), MachineCheckDetailDO::getCheckDate, query.getEndDate());
        return checkMapper.reworkRecord(query);
    }

    public void updateDetail(MachineCheckDetail part) {
        LambdaUpdateWrapper<MachineCheckDetailDO> updateWrapper = Wrappers.lambdaUpdate(MachineCheckDetailDO.class)
                .set(MachineCheckDetailDO::getProvider, part.getProvider())
                .eq(MachineCheckDetailDO::getSerialNo, part.getSerialNo())
                .eq(MachineCheckDetailDO::getProjectCode, part.getProjectCode())
                .eq(MachineCheckDetailDO::getPartCode, part.getPartCode()).eq(MachineCheckDetailDO::getPartVersion, part.getPartVersion());
        checkDetailMapper.update(updateWrapper);
    }

    public void updateDetailById(MachineCheckDetail part) {
        MachineCheckDetailDO updateDo = converter.detailEntity2Do(part);
        checkDetailMapper.updateById(updateDo);
    }

    public List<MachineCheckDetailDO> selectDetailsByOrderSerialNos(List<String> serialNos) {
        LambdaQueryWrapper<MachineCheckDetailDO> queryWrapper = Wrappers.lambdaQuery(MachineCheckDetailDO.class)
                .select(MachineCheckDetailDO::getSerialNo, MachineCheckDetailDO::getRequirementSerialNo, MachineCheckDetailDO::getCheckResultType, MachineCheckDetailDO::getOrderSerialNo, MachineCheckDetailDO::getProjectCode, MachineCheckDetailDO::getPartCode, MachineCheckDetailDO::getPartVersion, MachineCheckDetailDO::getCheckedNumber)
                .in(MachineCheckDetailDO::getOrderSerialNo, serialNos);
        return checkDetailMapper.selectList(queryWrapper);
    }

    public List<MachineCheckDO> selectSubmitSerialNo(List<String> serialNos) {
        LambdaQueryWrapper<MachineCheckDO> queryWrapper = Wrappers.lambdaQuery(MachineCheckDO.class)
                .select(MachineCheckDO::getSerialNo)
                .eq(MachineCheckDO::getStatus, ProcessStatus.COMMITTED)
                .in(MachineCheckDO::getSerialNo, serialNos);
        return checkMapper.selectList(queryWrapper);
    }
}
