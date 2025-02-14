package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockChangeAddCmd;
import com.greenstone.mes.machine.application.dto.event.MachineStockChangeE;
import com.greenstone.mes.machine.application.dto.result.MachinePartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineStockChangeRecord;
import com.greenstone.mes.machine.application.dto.result.MachineStockChangeRecordExportR;
import com.greenstone.mes.machine.application.dto.result.MachineStockChangeResult;
import com.greenstone.mes.machine.domain.entity.MachineStockChange;
import com.greenstone.mes.material.domain.BaseMaterial;
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
public interface MachineStockChangeAssemble {


    MachineStockChange toMachineStockChange(MachineStockChangeAddCmd addCmd);

    MachineStockChangeResult toMachineStockChangeR(MachineStockChange stockChange);

    List<MachineStockChangeResult> toMachineStockChangeRs(List<MachineStockChange> list);

    MachineStockChangeE toStockChangeE(MachineStockChange stockChange);

    @Mapping(target = "materialId", source = "id")
    @Mapping(target = "partCode", source = "code")
    @Mapping(target = "partVersion", source = "version")
    @Mapping(target = "partName", source = "name")
    MachinePartStockR toPartStockR(BaseMaterial baseMaterial);

    List<MachineStockChangeRecordExportR> toMachineStockChangeRecordERS(List<MachineStockChangeRecord> listRecordS);

    MachineStockChangeRecordExportR toMachineStockChangeRecordER(MachineStockChangeRecord listRecord);
}
