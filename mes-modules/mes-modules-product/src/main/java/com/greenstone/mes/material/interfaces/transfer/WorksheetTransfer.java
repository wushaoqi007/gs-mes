package com.greenstone.mes.material.interfaces.transfer;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.cqe.command.WorksheetImportCommand;
import com.greenstone.mes.material.cqe.command.WorksheetImportEditCommand;
import com.greenstone.mes.material.cqe.command.WorksheetQrCodeSaveVO;
import com.greenstone.mes.material.cqe.command.WorksheetSaveCommand;
import com.greenstone.mes.material.enums.PartBuyReason;
import com.greenstone.mes.material.request.ProcessOrderF1ImportVO;
import com.greenstone.mes.material.request.ProcessOrderF2ImportVO;
import com.greenstone.mes.material.request.WorksheetImportEditVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/18 13:35
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class, List.class, StrUtil.class}
)
public interface WorksheetTransfer {

    List<WorksheetImportCommand.PartImportCommand> toF1PartImportCommands(List<ProcessOrderF1ImportVO> validImportVOs);

    @Mapping(target = "componentCode", expression = "java(f1ImportVO.validAndGetComponentCodeName().getCode())")
    @Mapping(target = "componentName", expression = "java(f1ImportVO.validAndGetComponentCodeName().getName())")
    @Mapping(target = "partCode", expression = "java(f1ImportVO.validAndGetPartCodeNameVersion().getCode())")
    @Mapping(target = "partVersion", expression = "java(f1ImportVO.validAndGetPartCodeNameVersion().getVersion())")
    @Mapping(target = "partName", expression = "java(f1ImportVO.validAndGetPartCodeNameVersion().getName())")
    WorksheetImportCommand.PartImportCommand toF1PartImportCommand(ProcessOrderF1ImportVO f1ImportVO);

    List<WorksheetImportCommand.PartImportCommand> toF2PartImportCommands(List<ProcessOrderF2ImportVO> validImportVOs);

    @Mapping(target = "componentCode", expression = "java(f2ImportVO.validAndGetComponentPartCodeVersion().getComponentCode())")
    @Mapping(target = "partCode", expression = "java(f2ImportVO.validAndGetComponentPartCodeVersion().getPartCode())")
    @Mapping(target = "partVersion", expression = "java(f2ImportVO.validAndGetComponentPartCodeVersion().getPartVersion())")
    @Mapping(target = "surfaceTreatment", expression = "java(StrUtil.isEmpty(f2ImportVO.getHotTreatment()) ? f2ImportVO.getSurfaceTreatment() : f2ImportVO.getSurfaceTreatment() + f2ImportVO.getHotTreatment())")
    WorksheetImportCommand.PartImportCommand toF2PartImportCommand(ProcessOrderF2ImportVO f2ImportVO);

    @Mapping(target = "code", expression = "java(saveVO.getPartCode())")
    @Mapping(target = "version", source = "partVersion")
    @Mapping(target = "name", expression = "java(saveVO.getPartName())")
    @Mapping(target = "number", source = "partNumber")
    @Mapping(target = "reason", source = "purchaseReason")
    @Mapping(target = "printDate", expression = "java(new Date())")
    WorksheetSaveCommand.ProcessPart toPart(WorksheetQrCodeSaveVO saveVO);

    @Mapping(target = "code", expression = "java(saveVO.getComponentCode())")
    @Mapping(target = "name", expression = "java(saveVO.getComponentName())")
    @Mapping(target = "parts", expression = "java(List.of(toPart(saveVO)))")
    WorksheetSaveCommand.ProcessComponent toComponent(WorksheetQrCodeSaveVO saveVO);

    @Mapping(target = "code", source = "partOrderCode")
    @Mapping(target = "projectCode", source = "projectCode")
    @Mapping(target = "company", source = "companyType")
    @Mapping(target = "components", expression = "java(List.of(toComponent(saveVO)))")
    WorksheetSaveCommand toSaveCommand(WorksheetQrCodeSaveVO saveVO);


    default PartBuyReason toReason(String reason) {
        return PartBuyReason.getByName(reason);
    }

    default PartBuyReason toReason(Integer reason) {
        return PartBuyReason.getByCode(reason);
    }

    List<WorksheetImportEditCommand.PartImportEditCommand> toPartEditImportCommands(List<WorksheetImportEditVO> importEditVOs);

    @Mapping(target = "partCode", expression = "java(importEditVO.validAndGetPartCodeVersion().getCode())")
    @Mapping(target = "partVersion", expression = "java(importEditVO.validAndGetPartCodeVersion().getVersion())")
    WorksheetImportEditCommand.PartImportEditCommand toPartEditImportCommand(WorksheetImportEditVO importEditVO);
}
