package com.greenstone.mes.machine.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStatusChangeCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineCalculateHistoryQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.domain.converter.MachineCalculateConverter;
import com.greenstone.mes.machine.domain.entity.MachineCalculate;
import com.greenstone.mes.machine.domain.entity.MachineCalculateDetail;
import com.greenstone.mes.machine.domain.entity.MachineCalculateHistory;
import com.greenstone.mes.machine.infrastructure.mapper.MachineCalculateDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineCalculateHistoryMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineCalculateMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCalculateDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCalculateDetailDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCalculateHistoryDO;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.service.IBaseMaterialService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class MachineCalculateRepository {
    private final MachineCalculateMapper calculateMapper;
    private final MachineCalculateDetailMapper calculateDetailMapper;
    private final MachineCalculateHistoryMapper historyMapper;
    private final MachineCalculateConverter calculateConverter;
    private final IBaseMaterialService materialService;

    @Async
    public void updateMaterialPriceBatch(List<MachineCalculateDetail> calculateDetailList) {
        for (MachineCalculateDetail part : calculateDetailList) {
            if (part.getCalculatePrice() != null) {
                // 修改物料价格
                updateMaterialPrice(part);
            }
        }
    }

    public void updateMaterialPrice(MachineCalculateDetail calculateDetail) {
        // 更新物料价格
        BaseMaterial baseMaterial = materialService.selectBaseMaterialById(calculateDetail.getMaterialId());
        if (Objects.isNull(baseMaterial)) {
            log.info("更新物料价格失败，物料未找到，物料id:{}", calculateDetail.getMaterialId());
            return;
        }
        baseMaterial.setPrice(calculateDetail.getCalculatePrice());
        baseMaterial.setCalculateJson(calculateDetail.getCalculateJson());
        baseMaterial.setCalculateTime(LocalDateTime.now());
        baseMaterial.setCalculateBy(SecurityUtils.getLoginUser().getUser().getNickName());
        baseMaterial.setCalculateById(SecurityUtils.getLoginUser().getUser().getUserId());
        materialService.updatePrice(baseMaterial);
    }

    public MachineCalculate selectBySerialNo(String serialNo) {
        MachineCalculateDO oneOnly = calculateMapper.getOneOnly(MachineCalculateDO.builder().serialNo(serialNo).build());
        if (Objects.isNull(oneOnly)) {
            log.info("核价单未找到，单号：{}", serialNo);
            throw new ServiceException(MachineError.E200101);
        }
        return calculateConverter.do2Entity(oneOnly);
    }

    public List<MachineCalculate> selectListByFuzzy(MachineFuzzyQuery fuzzyQuery) {
        QueryWrapper<MachineCalculateDO> fuzzyQueryWrapper = new QueryWrapper<>();
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
        List<MachineCalculateDO> calculateDOS = calculateMapper.selectList(fuzzyQueryWrapper);
        return calculateConverter.dos2Entities(calculateDOS);
    }

    public MachineCalculate detail(String serialNo) {
        MachineCalculateDO calculateDO = calculateMapper.getOneOnly(MachineCalculateDO.builder().serialNo(serialNo).build());
        if (calculateDO == null) {
            throw new ServiceException(MachineError.E200101, StrUtil.format("单号：{}", serialNo));
        }
        List<MachineCalculateDetail> details = calculateDetailMapper.selectDetailBySerialNo(serialNo);
        for (MachineCalculateDetail detail : details) {
            if (detail.getMaterialCalculatePrice() != null && detail.getCalculatePrice() == null) {
                detail.setCalculatePrice(detail.getMaterialCalculatePrice());
                detail.setCalculateJson(detail.getMaterialCalculateJson());
                detail.setCalculateBy(detail.getMaterialCalculateBy());
                detail.setCalculateById(detail.getMaterialCalculateById());
                detail.setCalculateTime(detail.getMaterialCalculateTime());
            }
        }
        MachineCalculate calculate = calculateConverter.do2Entity(calculateDO);
        calculate.setParts(details);
        return calculate;
    }

    @Transactional
    public void addCalculate(MachineCalculate calculate) {
        MachineCalculateDO machineCalculateDO = calculateConverter.entity2Do(calculate);
        calculateMapper.insert(machineCalculateDO);
        List<MachineCalculateDetailDO> detailDOS = calculateConverter.detailEntities2Dos(calculate.getParts());
        calculateDetailMapper.insertBatchSomeColumn(detailDOS);
    }


    public void statusChange(MachineStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<MachineCalculateDO> updateWrapper = Wrappers.lambdaUpdate(MachineCalculateDO.class).set(MachineCalculateDO::getStatus, statusChangeCmd.getStatus())
                .in(MachineCalculateDO::getSerialNo, statusChangeCmd.getSerialNos());
        calculateMapper.update(updateWrapper);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            MachineCalculateDO calculateFound = calculateMapper.getOneOnly(MachineCalculateDO.builder().serialNo(serialNo).build());
            if (calculateFound == null) {
                throw new ServiceException(MachineError.E200101);
            }
            if (calculateFound.getStatus() != ProcessStatus.DRAFT && calculateFound.getStatus() != ProcessStatus.REJECTED) {
                throw new ServiceException(MachineError.E200118);
            }
        }
        LambdaQueryWrapper<MachineCalculateDO> calculateWrapper = Wrappers.lambdaQuery(MachineCalculateDO.class).in(MachineCalculateDO::getSerialNo, serialNos);
        calculateMapper.delete(calculateWrapper);
        LambdaQueryWrapper<MachineCalculateDetailDO> detailWrapper = Wrappers.lambdaQuery(MachineCalculateDetailDO.class).in(MachineCalculateDetailDO::getSerialNo,
                serialNos);
        calculateDetailMapper.delete(detailWrapper);
    }

    public List<MachineCalculateDetail> selectDetailList(String serialNo) {
        LambdaQueryWrapper<MachineCalculateDetailDO> detailWrapper = Wrappers.lambdaQuery(MachineCalculateDetailDO.class).eq(MachineCalculateDetailDO::getSerialNo,
                serialNo);
        return calculateConverter.detailDos2Entities(calculateDetailMapper.selectList(detailWrapper));
    }

    public MachineCalculateDetail selectDetailById(String calculateDetailId) {
        MachineCalculateDetailDO machineCalculateDetailDO = calculateDetailMapper.selectById(calculateDetailId);
        if (Objects.isNull(machineCalculateDetailDO)) {
            throw new ServiceException(StrUtil.format("核价单详情未找到,详情id:{}", calculateDetailId));
        }
        return calculateConverter.detailDo2Entity(machineCalculateDetailDO);
    }

    public void updatePrice(MachineCalculateDetail calculateDetail) {
        log.info("核价单：更新核价结果{}", calculateDetail);
        MachineCalculateDetailDO detailDO = calculateConverter.detailEntity2Do(calculateDetail);
        calculateDetailMapper.updateById(detailDO);
    }

    public void insertHistory(MachineCalculateHistory history) {
        log.info("核价单：记录核价历史{}", history);
        MachineCalculateHistoryDO historyDO = calculateConverter.historyEntity2Do(history);
        historyMapper.insert(historyDO);
    }

    public void insertHistoryBatch(List<MachineCalculateHistory> historyList) {
        if (CollUtil.isNotEmpty(historyList)) {
            log.info("核价单：批量记录核价历史{}", historyList);
            List<MachineCalculateHistoryDO> historyDOS = calculateConverter.historyEntities2Dos(historyList);
            historyMapper.insertBatchSomeColumn(historyDOS);
        }
    }

    public List<MachineCalculateHistory> selectHistory(MachineCalculateHistoryQuery query) {
        LambdaQueryWrapper<MachineCalculateHistoryDO> wrapper = Wrappers.lambdaQuery(MachineCalculateHistoryDO.class)
                .eq(MachineCalculateHistoryDO::getPartCode, query.getPartCode())
                .eq(MachineCalculateHistoryDO::getPartVersion, query.getPartVersion())
                .orderByDesc(MachineCalculateHistoryDO::getCalculateTime);
        List<MachineCalculateHistoryDO> list = historyMapper.selectList(wrapper);
        return calculateConverter.historyDos2Entities(list);
    }

    public void approve(MachineCalculate calculate) {
        MachineCalculateDO machineCalculateDO = calculateConverter.entity2Do(calculate);
        machineCalculateDO.setStatus(ProcessStatus.APPROVED);
        machineCalculateDO.setConfirmBy(SecurityUtils.getLoginUser().getUser().getNickName());
        machineCalculateDO.setConfirmTime(LocalDateTime.now());
        calculateMapper.updateById(machineCalculateDO);

    }
}
