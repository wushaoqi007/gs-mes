package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.application.assemble.MachineCalculateAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.*;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineCalculateHistoryQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.result.MachineCalculateResult;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.machine.application.service.MachineCalculateService;
import com.greenstone.mes.machine.domain.entity.MachineCalculate;
import com.greenstone.mes.machine.domain.entity.MachineCalculateDetail;
import com.greenstone.mes.machine.domain.entity.MachineCalculateHistory;
import com.greenstone.mes.machine.domain.repository.MachineCalculateRepository;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author wushaoqi
 * @date 2024-03-01-14:57
 */
@AllArgsConstructor
@Slf4j
@Service
public class MachineCalculateServiceImpl implements MachineCalculateService {

    private final MachineCalculateAssemble calculateAssemble;
    private final MachineCalculateRepository calculateRepository;
    private final MachineHelper machineHelper;
    private final RemoteSystemService systemService;

    @Override
    public void importCalculate(MachineCalculateImportCmd importCommand) {
        // 将import命令转为save命令
        MachineCalculateAddCmd addCmd = calculateAssemble.toAddCommand(importCommand);
        // 保存核价单
        saveCalculate(addCmd);
    }

    @Override
    public List<MachineCalculateResult> selectList(MachineFuzzyQuery query) {
        List<MachineCalculate> machineCalculates = calculateRepository.selectListByFuzzy(query);
        return calculateAssemble.toMachineCalculateRs(machineCalculates);
    }

    @Override
    public MachineCalculateResult detail(String serialNo) {
        MachineCalculate detail = calculateRepository.detail(serialNo);
        return calculateAssemble.toMachineCalculateR(detail);
    }

    @Transactional
    @Override
    public void calculate(MachineCalculateDetailEditCmd editCmd) {
        MachineCalculate calculate = calculateRepository.selectBySerialNo(editCmd.getCalculateSerialNo());
        if (calculate.getStatus() == ProcessStatus.APPROVING) {
            throw new ServiceException(StrUtil.format("核价单审批中，不可修改，请撤回后修改，单号：{}", editCmd.getCalculateSerialNo()));
        }
        MachineCalculateDetail calculateDetail = calculateRepository.selectDetailById(editCmd.getCalculateDetailId());
        BigDecimal calPrice = BigDecimal.valueOf(editCmd.getCalculatePrice()).setScale(2, RoundingMode.HALF_UP);
        calculateDetail.setCalculatePrice(calPrice.doubleValue());
        calculateDetail.setCalculateJson(editCmd.getCalculateJson());
        calculateDetail.setCalculateTime(LocalDateTime.now());
        calculateDetail.setCalculateBy(SecurityUtils.getLoginUser().getUser().getNickName());
        calculateDetail.setCalculateById(SecurityUtils.getLoginUser().getUser().getUserId());
        BigDecimal calTotalPrice = BigDecimal.valueOf(editCmd.getCalculatePrice() * (calculateDetail.getPartNumber() == null ? 0 : calculateDetail.getPartNumber())).setScale(2, RoundingMode.HALF_UP);
        calculateDetail.setTotalPrice(calTotalPrice.doubleValue());
        calculateRepository.updatePrice(calculateDetail);
        // 审批通过后的核价，可直接修改物料价格，并记录历史
        if (calculate.getStatus() == ProcessStatus.APPROVED) {
            calculateRepository.updateMaterialPrice(calculateDetail);
            calculateRepository.insertHistory(calculateAssemble.toHistory(calculateDetail));
        }
    }

    @Override
    public void statusChange(MachineStatusChangeCmd statusChangeCmd) {
        log.info("statusChange params:{}", statusChangeCmd);
        if (statusChangeCmd.getStatus() == ProcessStatus.DRAFT) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                MachineCalculate calculate = calculateRepository.selectBySerialNo(serialNo);
                if (calculate.getStatus() != ProcessStatus.APPROVING) {
                    throw new ServiceException(MachineError.E200117);
                }
            }
            calculateRepository.statusChange(statusChangeCmd);
        } else if (statusChangeCmd.getStatus() == ProcessStatus.REJECTED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                MachineCalculate calculate = calculateRepository.selectBySerialNo(serialNo);
                if (calculate.getStatus() != ProcessStatus.APPROVING) {
                    throw new ServiceException(MachineError.E200115);
                }
            }
            calculateRepository.statusChange(statusChangeCmd);
        } else if (statusChangeCmd.getStatus() == ProcessStatus.APPROVING) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                MachineCalculate calculate = calculateRepository.detail(serialNo);
                if (calculate.getStatus() != ProcessStatus.DRAFT && calculate.getStatus() != ProcessStatus.REJECTED) {
                    throw new ServiceException(MachineError.E200116);
                }
                Optional<MachineCalculateDetail> emptyPrice = calculate.getParts().stream().filter(a -> a.getCalculatePrice() == null).findFirst();
                if (emptyPrice.isPresent()) {
                    throw new ServiceException(StrUtil.format("未完成核价，提交失败，零件号/版本：{}/{}，核价结果为空。", emptyPrice.get().getPartCode(), emptyPrice.get().getPartVersion()));
                }
            }
            calculateRepository.statusChange(statusChangeCmd);
        } else if (statusChangeCmd.getStatus() == ProcessStatus.APPROVED) {
            List<MachineCalculateDetail> allParts = new ArrayList<>();
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                MachineCalculate calculate = calculateRepository.detail(serialNo);
                if (calculate.getStatus() != ProcessStatus.APPROVING) {
                    throw new ServiceException(MachineError.E200115);
                }
                calculateRepository.approve(calculate);
                allParts.addAll(calculate.getParts());
            }
            calculateRepository.statusChange(statusChangeCmd);
            // 记录历史
            calculateRepository.insertHistoryBatch(calculateAssemble.toHistories(allParts));
            // 异步批量更新物料价格
            calculateRepository.updateMaterialPriceBatch(allParts);
        } else {
            throw new ServiceException(MachineError.E200113);
        }
    }

    @Override
    public void remove(MachineRemoveCmd removeCmd) {
        calculateRepository.remove(removeCmd.getSerialNos());
    }

    @Override
    public List<MachineCalculateHistory> selectHistory(MachineCalculateHistoryQuery query) {
        return calculateRepository.selectHistory(query);
    }

    private void saveCalculate(MachineCalculateAddCmd addCmd) {
        log.info("save calculate params:{}", addCmd);
        addCmd.trim();
        MachineCalculate calculate = calculateAssemble.toMachineCalculate(addCmd);
        // 设置单号
        SerialNoNextCmd nextCmd =
                SerialNoNextCmd.builder().type("machine_calculate").prefix("MCC" + DateUtil.dateSerialStrNow()).build();
        SerialNoR serialNoR = systemService.getNextSn(nextCmd);
        calculate.setStatus(ProcessStatus.DRAFT);
        calculate.setSerialNo(serialNoR.getSerialNo());
        for (MachineCalculateDetail part : calculate.getParts()) {
            validMaterial(part);
            part.setSerialNo(serialNoR.getSerialNo());
        }
        // 保存核价单
        calculateRepository.addCalculate(calculate);
    }

    private void validMaterial(MachineCalculateDetail part) {
        BaseMaterial materialFind = machineHelper.checkMaterial(part.getPartCode(), part.getPartVersion());
        if (!materialFind.getName().equals(part.getPartName())) {
            throw new ServiceException(BizError.E20002, StrUtil.format("零件号'{}/{}'重复，当前的零件名称：'{}'，已存在的零件名称：'{}'",
                    part.getPartCode(), part.getPartVersion(), part.getPartName(), materialFind.getName()));
        }
        part.setMaterialId(materialFind.getId());
    }

}
