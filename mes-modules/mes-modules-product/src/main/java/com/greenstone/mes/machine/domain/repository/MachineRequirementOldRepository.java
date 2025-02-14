package com.greenstone.mes.machine.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.machine.domain.converter.MachineRequirementOldConverter;
import com.greenstone.mes.machine.domain.entity.MachineInquiryPriceDetail;
import com.greenstone.mes.machine.domain.entity.MachineRequirement;
import com.greenstone.mes.machine.domain.entity.MachineRequirementDetail;
import com.greenstone.mes.machine.infrastructure.mapper.MachineRequirementDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineRequirementMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineRequirementDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineRequirementDetailDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2023-11-24-10:27
 */
@Slf4j
@AllArgsConstructor
@Service
public class MachineRequirementOldRepository {
    private final MachineRequirementMapper requirementMapper;
    private final MachineRequirementDetailMapper requirementDetailMapper;
    private final MachineRequirementOldConverter requirementConverter;

    public MachineRequirementDO selectBySerialNo(String serialNo) {
        return requirementMapper.getOneOnly(MachineRequirementDO.builder().serialNo(serialNo).build());
    }

    public MachineRequirement detail(String serialNo) {
        MachineRequirementDO requirementDO = requirementMapper.getOneOnly(MachineRequirementDO.builder().serialNo(serialNo).build());
        if (requirementDO == null) {
            throw new ServiceException(MachineError.E200106, StrUtil.format("需求单号：{}", serialNo));
        }
        List<MachineRequirementDetailDO> detailDOS = requirementDetailMapper.list(MachineRequirementDetailDO.builder().serialNo(serialNo).build());

        return requirementConverter.toMachineRequirement(requirementDO, detailDOS);
    }

    public List<MachineRequirementDetail> selectDetailList(String serialNo) {
        LambdaQueryWrapper<MachineRequirementDetailDO> detailWrapper = Wrappers.lambdaQuery(MachineRequirementDetailDO.class).eq(MachineRequirementDetailDO::getSerialNo,
                serialNo);
        return requirementConverter.detailDos2Entities(requirementDetailMapper.selectList(detailWrapper));
    }

    public MachineRequirementDetail selectPart(String projectCode, String serialNo, String partCode, String partVersion) {
        LambdaQueryWrapper<MachineRequirementDetailDO> lambdaQueryWrapper = Wrappers.lambdaQuery(MachineRequirementDetailDO.class)
                .eq(MachineRequirementDetailDO::getProjectCode, projectCode)
                .eq(MachineRequirementDetailDO::getSerialNo, serialNo)
                .eq(MachineRequirementDetailDO::getPartCode, partCode)
                .eq(MachineRequirementDetailDO::getPartVersion, partVersion);
        MachineRequirementDetailDO detailDO = requirementDetailMapper.selectOne(lambdaQueryWrapper);
        return requirementConverter.detailDo2Entity(detailDO);
    }

    public void updateScannedPaperNumber(List<MachineInquiryPriceDetail> parts) {
        Map<String, List<MachineInquiryPriceDetail>> groupBySerialNo = parts.stream().collect(Collectors.groupingBy(MachineInquiryPriceDetail::getRequirementSerialNo));
        List<MachineRequirementDetail> updateDetails = new ArrayList<>();
        groupBySerialNo.forEach((serialNo, list) -> {
            MachineRequirement requirement = detail(serialNo);
            for (MachineInquiryPriceDetail machineInquiryPriceDetail : list) {
                Optional<MachineRequirementDetail> find = requirement.getParts().stream().filter(a -> a.getId().equals(machineInquiryPriceDetail.getRequirementDetailId())).findFirst();
                if (find.isPresent()) {
                    MachineRequirementDetail machineRequirementDetail = find.get();
                    machineRequirementDetail.setScannedPaperNumber(machineInquiryPriceDetail.getScannedPaperNumber());
                    updateDetails.add(machineRequirementDetail);
                }
            }
        });
        if (CollUtil.isNotEmpty(updateDetails)) {
            // 更新已扫描图纸梳理
            List<MachineRequirementDetailDO> detailDOS = requirementConverter.detailEntities2Dos(updateDetails);
            for (MachineRequirementDetailDO detailDO : detailDOS) {
                requirementDetailMapper.updateById(detailDO);
            }
            // 当申请单中的零件在询价单中都扫过码之后，申请单变更为已审批状态
            List<MachineRequirement> updateRequirements = new ArrayList<>();
            for (String serialNo : groupBySerialNo.keySet()) {
                MachineRequirement requirement = detail(serialNo);
                Optional<MachineRequirementDetail> find = requirement.getParts().stream().filter(a -> a.getProcessNumber() != 0L && (a.getScannedPaperNumber() == null || a.getScannedPaperNumber() < a.getPaperNumber())).findFirst();
                if (find.isEmpty()) {
                    updateRequirements.add(requirement);
                }
            }
            if (CollUtil.isNotEmpty(updateRequirements)) {
                for (MachineRequirement updateRequirement : updateRequirements) {
                    updateRequirement.setConfirmBy(SecurityUtils.getLoginUser().getUser().getUserId());
                    updateRequirement.setConfirmTime(LocalDateTime.now());
                    MachineRequirementDO requirementDO = requirementConverter.entity2Do(updateRequirement);
                    requirementMapper.updateById(requirementDO);
                }
            }
        }
    }

}
