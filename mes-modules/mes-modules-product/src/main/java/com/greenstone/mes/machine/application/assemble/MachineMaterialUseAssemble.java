package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineMaterialUseAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineWarehouseOutAddCmd;
import com.greenstone.mes.machine.application.dto.event.MachineMaterialUseE;
import com.greenstone.mes.machine.application.dto.result.MachineMaterialUseResult;
import com.greenstone.mes.machine.application.dto.result.MachineOrderPartR;
import com.greenstone.mes.machine.application.dto.result.MachinePartStockR;
import com.greenstone.mes.machine.domain.entity.MachineMaterialUse;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import com.greenstone.mes.material.domain.BaseMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class, List.class, StrUtil.class}
)
public interface MachineMaterialUseAssemble {


    MachineMaterialUse toMachineMaterialUse(MachineMaterialUseAddCmd addCmd);

    MachineMaterialUseResult toMachineMaterialUseR(MachineMaterialUse materialUse);

    List<MachineMaterialUseResult> toMachineMaterialUseRs(List<MachineMaterialUse> list);

    MachineMaterialUseE toMaterialUseE(MachineMaterialUse materialUse);

    @Mapping(target = "outStockTime", source = "useTime")
    MachineWarehouseOutAddCmd toWarehouseOutAddCmd(MachineMaterialUseE useE);

    List<MachineWarehouseOutAddCmd.Part> toWarehouseOutParts(List<MachineMaterialUseE.Part> parts);

    @Mapping(target = "outStockNumber", source = "useNumber")
    MachineWarehouseOutAddCmd.Part toWarehouseOutPart(MachineMaterialUseE.Part part);

    @Mapping(target = "materialId", source = "id")
    @Mapping(target = "partCode", source = "code")
    @Mapping(target = "partVersion", source = "version")
    @Mapping(target = "partName", source = "name")
    MachinePartStockR toPartStockR(BaseMaterial baseMaterial);

    @Mapping(target = "orderDetailId", source = "id")
    @Mapping(target = "orderSerialNo", source = "serialNo")
    MachineOrderPartR toMachineOrderPartR(MachineOrderDetail orderDetail);
}
