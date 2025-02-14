package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineReworkAddCmd;
import com.greenstone.mes.machine.application.dto.event.MachineReworkE;
import com.greenstone.mes.machine.application.dto.result.MachineCheckPartR;
import com.greenstone.mes.machine.application.dto.result.MachineCheckPartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineReworkResult;
import com.greenstone.mes.machine.domain.entity.MachineRework;
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
public interface MachineReworkAssemble {


    MachineRework toMachineRework(MachineReworkAddCmd addCmd);

    MachineReworkResult toMachineReworkR(MachineRework rework);

    List<MachineReworkResult> toMachineReworkRs(List<MachineRework> list);

    MachineReworkE toReworkE(MachineRework rework);

    List<MachineCheckPartStockR> toMachineCheckPartStockRs(List<MachineCheckPartR> selectPartList);

    @Mapping(target = "stockNumber", source = "toBeCheckedNumber")
    @Mapping(target = "warehouseCode", source = "inWarehouseCode")
    MachineCheckPartStockR toMachineCheckPartStockR(MachineCheckPartR partR);
}
