package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.machine.application.dto.result.MachineRequirementDetailResult;
import com.greenstone.mes.machine.application.dto.result.MachineRequirementExportR;
import com.greenstone.mes.machine.domain.entity.MachineRequirementDetail;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-11-24-9:17
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class, List.class, StrUtil.class, ZoneId.class}
)
@Validated
public interface MachineRequirementAssemble {


    MachineRequirementDetailResult toMachineRequirementDetailR(MachineRequirementDetail detail);

    List<MachineRequirementExportR> toExportDataList(List<MachineRequirementDetail> machineRequirementDetails);

    default MachineRequirementExportR toExportData(MachineRequirementDetail machineRequirementDetail) {
        MachineRequirementExportR machineRequirementExportR = MachineRequirementExportR.builder()
                .serialNo(machineRequirementDetail.getSerialNo())
                .projectCode(machineRequirementDetail.getProjectCode())
                .componentCode(machineRequirementDetail.getHierarchy())
                .hierarchy(machineRequirementDetail.getHierarchy())
                .partCodeAndVersion(machineRequirementDetail.getPartCode() + "/" + machineRequirementDetail.getPartVersion())
                .partName(machineRequirementDetail.getPartName())
                .processNumber(machineRequirementDetail.getProcessNumber())
                .designer(machineRequirementDetail.getDesigner())
                .rawMaterial(machineRequirementDetail.getRawMaterial())
                .surfaceTreatment(machineRequirementDetail.getSurfaceTreatment())
                .weight(machineRequirementDetail.getWeight())
                .remark(machineRequirementDetail.getRemark())
                .provider("")
                .processDeadline(null)
                .planDeadline(null)
                .build();
        if (StrUtil.isNotEmpty(machineRequirementExportR.getHierarchy())) {
            String[] split = machineRequirementExportR.getHierarchy().split("/");
            if (split.length > 1) {
                machineRequirementExportR.setHierarchy(split[0]);
            }
        }
        return machineRequirementExportR;
    }

}
