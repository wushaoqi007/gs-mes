package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineWarehouseInAddCmd;
import com.greenstone.mes.machine.application.dto.event.MachineWarehouseInE;
import com.greenstone.mes.machine.application.dto.result.MachineOrderPartR;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseInRecord;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseInRecordExportR;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseInResult;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import com.greenstone.mes.machine.domain.entity.MachineWarehouseIn;
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
public interface MachineWarehouseInAssemble {

    MachineWarehouseIn toMachineWarehouseIn(MachineWarehouseInAddCmd addCmd);

    MachineWarehouseInResult toMachineWarehouseInR(MachineWarehouseIn warehouseIn);

    List<MachineWarehouseInResult> toMachineWarehouseInRs(List<MachineWarehouseIn> list);

    @Mapping(target = "serialNo", source = "warehouseIn.serialNo")
    @Mapping(target = "operation", source = "operation")
    @Mapping(target = "parts", source = "warehouseIn.parts")
    MachineWarehouseInE toWarehouseInE(MachineWarehouseIn warehouseIn, Integer operation);

    @Mapping(target = "orderDetailId", source = "id")
    @Mapping(target = "orderSerialNo", source = "serialNo")
    MachineOrderPartR toMachineOrderPartR(MachineOrderDetail orderDetail);

    List<MachineWarehouseInRecordExportR> toMachineWarehouseInRecordERS(List<MachineWarehouseInRecord> listRecordS);

    MachineWarehouseInRecordExportR toMachineWarehouseInRecordER(MachineWarehouseInRecord listRecord);
}
