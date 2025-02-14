package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.machine.application.dto.cqe.cmd.*;
import com.greenstone.mes.machine.application.dto.result.*;
import com.greenstone.mes.machine.domain.entity.MachineOrder;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import com.greenstone.mes.machine.domain.entity.MachineRequirementDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-11-24-9:17
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class, List.class, StrUtil.class, ZoneId.class, BigDecimal.class}
)
public interface MachineOrderAssemble {

    MachineOrderResult toMachineOrderR(MachineOrder order);

    List<MachineOrderResult> toMachineOrderRs(List<MachineOrder> orderList);

    MachineOrderDetailResult toMachineOrderDetailR(MachineOrderDetail detail);

    List<MachineOrderDetailResult> toMachineOrderDetailRs(List<MachineOrderDetail> detailList);

    @Mapping(target = "orderDetailId", source = "id")
    @Mapping(target = "orderSerialNo", source = "serialNo")
    MachineOrderPartR toMachineOrderPartR(MachineOrderDetail orderDetail);

    List<MachineOrderImportCmd.Part> toPartImportCommands(@Valid List<MachineOrderImportVO> importVOS);

    @Mapping(target = "partCode", expression = "java(importVO.validAndGetPartCode())")
    @Mapping(target = "partVersion", expression = "java(importVO.validAndGetPartVersion())")
    @Mapping(target = "processDeadline", expression = "java(importVO.getProcessDeadline().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())")
    @Mapping(target = "planDeadline", expression = "java(importVO.getPlanDeadline().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())")
    @Mapping(target = "orderTime", expression = "java(importVO.getOrderTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())")
    MachineOrderImportCmd.Part toPartImportCommand(MachineOrderImportVO importVO);

    default MachineOrderImportCmd.Part toPartImportCommand(MachineOrderHistoryImportVO importVO) {
        MachineOrderImportCmd.Part part = MachineOrderImportCmd.Part.builder().partCode(importVO.validAndGetPartCodeNameVersion().getCode())
                .partName(importVO.validAndGetPartCodeNameVersion().getName())
                .partVersion(importVO.validAndGetPartCodeNameVersion().getVersion())
                .processNumber(importVO.getOrderNumber().longValue())
                .projectCode(importVO.getProjectCode())
                .processNumber(importVO.getOrderNumber().longValue())
                .receivedNumber(importVO.getReceivedNumber() == null ? null : importVO.getReceivedNumber().longValue())
                .provider(importVO.getProvider())
                .processDeadline(importVO.getProcessDeadline() == null ? null : importVO.getProcessDeadline().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .planDeadline(importVO.getPlanDeadline() == null ? null : importVO.getPlanDeadline().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .receiveTime(importVO.getReceiveTime() == null ? null : importVO.getReceiveTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .rawMaterial(importVO.getRawMaterial())
                .hierarchy(importVO.getHierarchy())
                .designer(importVO.getDesigner())
                .remark(importVO.getRemark())
                .orderTime(importVO.getOrderTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).build();
        return part;
    }

    List<MachineOrderDetail> toMachineOrderDetailsFromImport(List<MachineOrderImportCmd.Part> parts);

    @Mapping(target = "unitPrice", expression = "java(part.getUnitPrice() != null ? BigDecimal.valueOf(part.getUnitPrice().doubleValue()) : new BigDecimal(0))")
    MachineOrderDetail toMachineOrderDetailFromImport(MachineOrderImportCmd.Part part);

    default MachineOrderPriceImportCmd.Part toPartPriceImportCommand(MachineOrderPriceImportVO importVO) {
        return MachineOrderPriceImportCmd.Part.builder().partCode(importVO.validAndGetPartCodeNameVersion().getCode())
                .partName(importVO.validAndGetPartCodeNameVersion().getName())
                .partVersion(importVO.validAndGetPartCodeNameVersion().getVersion())
                .unitPrice(importVO.getUnitPrice()).build();
    }

    MachineOrderContractResult toMachineOrderContractResult(MachineOrder detail);

    @Mapping(target = "serialNo", ignore = true)
    @Mapping(target = "id", ignore = true)
    MachineOrderDetail toContractOrderPart(MachineRequirementDetail machineRequirementDetail);

    List<MachineOrderProgressExportResult> toOrderProgressExportList(List<MachineOrderProgressResult> selectOrderProgressList);

    MachineOrderProgressExportResult toOrderProgressExport(MachineOrderProgressResult selectOrderProgress);

    MachineOrderExportToImportR toExportBatchR(MachineOrderDetail part);
}
