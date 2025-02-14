package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineSurfaceTreatmentAddCmd;
import com.greenstone.mes.machine.application.dto.event.MachineSurfaceTreatmentE;
import com.greenstone.mes.machine.application.dto.result.*;
import com.greenstone.mes.machine.domain.entity.MachineSurfaceTreatment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.Date;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class, List.class, StrUtil.class}
)
public interface MachineSurfaceTreatmentAssemble {


    MachineSurfaceTreatment toMachineSurfaceTreatment(MachineSurfaceTreatmentAddCmd addCmd);

    MachineSurfaceTreatmentResult toMachineSurfaceTreatmentR(MachineSurfaceTreatment surfaceTreatment);

    List<MachineSurfaceTreatmentResult> toMachineSurfaceTreatmentRs(List<MachineSurfaceTreatment> list);

    MachineSurfaceTreatmentE toSurfaceTreatmentE(MachineSurfaceTreatment surfaceTreatment);

    List<MachineCheckPartStockR> toMachineCheckPartStockRs(List<MachineCheckPartR> selectPartList);

    @Mapping(target = "stockNumber", source = "toBeCheckedNumber")
    @Mapping(target = "warehouseCode", source = "inWarehouseCode")
    MachineCheckPartStockR toMachineCheckPartStockR(MachineCheckPartR partR);

    List<MachineSurfaceTreatmentRecordExportR> toMachineSurfaceTreatmentRecordERS(List<MachineSurfaceTreatmentRecord> listRecordS);

    MachineSurfaceTreatmentRecordExportR toMachineSurfaceTreatmentRecordER(MachineSurfaceTreatmentRecord listRecord);
}
