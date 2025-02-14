package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineInquiryPriceAddCmd;
import com.greenstone.mes.machine.application.dto.result.MachineInquiryPriceExportR;
import com.greenstone.mes.machine.application.dto.result.MachineInquiryPriceResult;
import com.greenstone.mes.machine.domain.entity.MachineInquiryPrice;
import com.greenstone.mes.machine.domain.entity.MachineInquiryPriceDetail;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class, List.class, StrUtil.class, ZoneId.class, LocalDate.class}
)
public interface MachineInquiryPriceAssemble {

    MachineInquiryPriceResult toMachineInquiryPriceR(MachineInquiryPrice inquiryPrice);

    List<MachineInquiryPriceResult> toMachineInquiryPriceRs(List<MachineInquiryPrice> inquiryPriceList);

    MachineInquiryPrice toMachineInquiryPrice(MachineInquiryPriceAddCmd addCmd);

    List<MachineInquiryPriceExportR> toMachineInquiryPriceExportRS(List<MachineInquiryPriceDetail> machineInquiryPriceDetails);

    default MachineInquiryPriceExportR toMachineInquiryPriceExportR(MachineInquiryPriceDetail machineInquiryPriceDetail) {
        MachineInquiryPriceExportR machineInquiryPriceExportR = MachineInquiryPriceExportR.builder()
                .serialNo(machineInquiryPriceDetail.getSerialNo())
                .orderTime(LocalDate.now())
                .processDeadline(machineInquiryPriceDetail.getProcessDeadline())
                .requirementSerialNo(machineInquiryPriceDetail.getRequirementSerialNo())
                .projectCode(machineInquiryPriceDetail.getProjectCode())
                .hierarchy(machineInquiryPriceDetail.getHierarchy())
                .partCodeAndVersion(machineInquiryPriceDetail.getPartCode() + "/" + machineInquiryPriceDetail.getPartVersion())
                .partName(machineInquiryPriceDetail.getPartName())
                .rawMaterial(machineInquiryPriceDetail.getRawMaterial())
                .weight(machineInquiryPriceDetail.getWeight())
                .designer(machineInquiryPriceDetail.getDesigner())
                .surfaceTreatment(machineInquiryPriceDetail.getSurfaceTreatment())
                .remark(machineInquiryPriceDetail.getRemark())
                .partNumber(machineInquiryPriceDetail.getPartNumber()).build();
        return machineInquiryPriceExportR;
    }
}
