package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalFinishedCommitCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineWarehouseOutAddCmd;
import com.greenstone.mes.machine.application.dto.event.MachineWarehouseOutE;
import com.greenstone.mes.machine.application.dto.result.*;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import com.greenstone.mes.machine.domain.entity.MachineWarehouseOut;
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
        imports = {Date.class, List.class, StrUtil.class, LocalDateTimeUtil.class, DatePattern.class}
)
public interface MachineWarehouseOutAssemble {


    MachineWarehouseOut toMachineWarehouseOut(MachineWarehouseOutAddCmd addCmd);

    MachineWarehouseOutResult toMachineWarehouseOutR(MachineWarehouseOut warehouseOut);

    List<MachineWarehouseOutResult> toMachineWarehouseOutRs(List<MachineWarehouseOut> list);

    MachineWarehouseOutE toWarehouseOutE(MachineWarehouseOut warehouseOut);

    @Mapping(target = "materialId", source = "id")
    @Mapping(target = "partCode", source = "code")
    @Mapping(target = "partVersion", source = "version")
    @Mapping(target = "partName", source = "name")
    MachinePartStockR toPartStockR(BaseMaterial baseMaterial);

    @Mapping(target = "orderDetailId", source = "id")
    @Mapping(target = "orderSerialNo", source = "serialNo")
    MachineOrderPartR toMachineOrderPartR(MachineOrderDetail orderDetail);

    @Mapping(target = "takeBy", source = "applicant")
    @Mapping(target = "takeById", source = "applicantId")
    @Mapping(target = "takeTime", expression = "java(LocalDateTimeUtil.format(warehouseOut.getOutStockTime(), DatePattern.NORM_DATETIME_PATTERN))")
    WxApprovalFinishedCommitCmd toFinishedApprovalCmd(MachineWarehouseOut warehouseOut);

    List<MachineWarehouseOutRecordExportR> toMachineWarehouseOutRecordERS(List<MachineWarehouseOutRecord> listRecords);

    MachineWarehouseOutRecordExportR toMachineWarehouseOutRecordER(MachineWarehouseOutRecord listRecord);
}
