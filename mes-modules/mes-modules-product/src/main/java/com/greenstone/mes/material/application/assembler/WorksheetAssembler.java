package com.greenstone.mes.material.application.assembler;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.bom.dto.BomImportDTO;
import com.greenstone.mes.material.application.dto.result.WorksheetCheckCountR;
import com.greenstone.mes.material.application.dto.result.WorksheetPlaceOrderR;
import com.greenstone.mes.material.cqe.command.WorksheetImportCommand;
import com.greenstone.mes.material.cqe.command.WorksheetSaveCommand;
import com.greenstone.mes.material.domain.ProcessOrderDO;
import com.greenstone.mes.material.domain.ProcessOrderDetailDO;
import com.greenstone.mes.material.domain.entity.ProcessOrder;
import com.greenstone.mes.material.domain.entity.WorksheetCheck;
import com.greenstone.mes.material.domain.entity.WorksheetPlaceOrder;
import com.greenstone.mes.material.enums.PartBuyReason;
import com.greenstone.mes.material.request.ProcessOrderF1ImportVO;
import com.greenstone.mes.material.request.ProcessOrderF2ImportVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author gu_renkai
 * @date 2022/10/28 13:15
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {StrUtil.class}
)
public interface WorksheetAssembler {

    Logger log = LoggerFactory.getLogger(WorksheetAssembler.class);

    @Mapping(target = "code", source = "partCode")
    @Mapping(target = "version", source = "partVersion")
    @Mapping(target = "name", source = "partName")
    @Mapping(target = "number", source = "partNumber")
    @Mapping(target = "paperNumber", source = "paperNumber")
    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "designer", source = "designer")
    @Mapping(target = "printDate", source = "printDate")
    @Mapping(target = "rawMaterial", source = "rawMaterial")
    @Mapping(target = "surfaceTreatment", source = "surfaceTreatment")
    @Mapping(target = "weight", source = "weight")
    WorksheetSaveCommand.ProcessPart toProcessPart(WorksheetImportCommand.PartImportCommand partImport);

    ProcessOrder toProcessOrder(WorksheetSaveCommand saveCommand);

    List<ProcessOrder.ProcessComponent> toProcessComponents(List<WorksheetSaveCommand.ProcessComponent> components);

    ProcessOrder.ProcessComponent toProcessComponent(WorksheetSaveCommand.ProcessComponent component);

    List<ProcessOrder.ProcessPart> toProcessParts(List<WorksheetSaveCommand.ProcessPart> parts);

    @Mapping(target = "material.name", source = "name")
    @Mapping(target = "material.code", source = "code")
    @Mapping(target = "material.version", source = "version")
    @Mapping(target = "material.weight", source = "weight")
    @Mapping(target = "material.surfaceTreatment", source = "surfaceTreatment")
    @Mapping(target = "material.rawMaterial", source = "rawMaterial")
    @Mapping(target = "material.designer", source = "designer")
    @Mapping(target = "material.paperNumber", source = "paperNumber")
    ProcessOrder.ProcessPart toProcessPart(WorksheetSaveCommand.ProcessPart part);

    @Mapping(target = "id", source = "id.id")
    @Mapping(target = "companyType", source = "company")
    @Mapping(target = "purchaseNumber", source = "number")
    ProcessOrderDO toProcessOrderDO(ProcessOrder processOrder);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "processOrderId", source = "order.id.id")
    @Mapping(target = "projectCode", source = "order.projectCode")
    @Mapping(target = "componentCode", source = "component.code")
    @Mapping(target = "componentName", source = "component.name")
    @Mapping(target = "code", source = "part.material.code")
    @Mapping(target = "version", source = "part.material.version")
    @Mapping(target = "name", source = "part.material.name")
    @Mapping(target = "originalNumber", source = "part.number")
    @Mapping(target = "currentNumber", source = "part.number")
    @Mapping(target = "paperNumber", source = "part.material.paperNumber")
    @Mapping(target = "surfaceTreatment", source = "part.material.surfaceTreatment")
    @Mapping(target = "rawMaterial", source = "part.material.rawMaterial")
    @Mapping(target = "weight", source = "part.material.weight")
    @Mapping(target = "designer", source = "part.material.designer")
    @Mapping(target = "printDate", source = "part.printDate")
    @Mapping(target = "reason", source = "part.reason")
    @Mapping(target = "materialId", source = "part.material.id.id")
    ProcessOrderDetailDO toProcessOrderDetailDO(ProcessOrder order, ProcessOrder.ProcessComponent component, ProcessOrder.ProcessPart part);

    default Integer convertPartBuyReason(PartBuyReason reason) {
        return reason.getCode();
    }

    default PartBuyReason convertPartBuyReason(String reason) {
        return PartBuyReason.getByName(reason);
    }

    @Mapping(target = "processOrderCode", source = "order.code")
    @Mapping(target = "projectCode", source = "order.projectCode")
    @Mapping(target = "componentCode", source = "component.code")
    @Mapping(target = "componentName", source = "component.name")
    @Mapping(target = "partCode", source = "part.material.code")
    @Mapping(target = "partName", source = "part.material.name")
    @Mapping(target = "partVersion", source = "part.material.version")
    @Mapping(target = "partNumber", source = "part.number")
    BomImportDTO toBomImportDto(ProcessOrder order, ProcessOrder.ProcessComponent component, ProcessOrder.ProcessPart part);


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

    default WorksheetSaveCommand toSaveCommand(WorksheetImportCommand importCommand) {
        // 组装加工单信息
        String processOrderCode = importCommand.getPartImportCommands().get(0).getProcessOrderCode();
        String projectCode = importCommand.getPartImportCommands().get(0).getProjectCode();
        List<WorksheetSaveCommand.ProcessComponent> components = new ArrayList<>();
        WorksheetSaveCommand worksheetSaveCommand = WorksheetSaveCommand.builder().code(processOrderCode).
                projectCode(projectCode).company(importCommand.getCompany()).components(components).build();

        List<WorksheetImportCommand.PartImportCommand> partImportCommands = importCommand.getPartImportCommands();
        Map<String, WorksheetSaveCommand.ProcessComponent> componentMap = new HashMap<>();
        for (WorksheetImportCommand.PartImportCommand partImportCommand : partImportCommands) {
            // 组装组件信息
            WorksheetSaveCommand.ProcessComponent processComponent = componentMap.computeIfAbsent(partImportCommand.getComponentCode(), s -> {
                List<WorksheetSaveCommand.ProcessPart> parts = new ArrayList<>();
                WorksheetSaveCommand.ProcessComponent component = WorksheetSaveCommand.ProcessComponent.builder().
                        code(partImportCommand.getComponentCode()).name(partImportCommand.getComponentName()).parts(parts).build();
                components.add(component);
                return component;
            });

            WorksheetSaveCommand.ProcessPart processPart = toProcessPart(partImportCommand);
            processComponent.getParts().add(processPart);
        }
        return worksheetSaveCommand;
    }

    @Mapping(target = "processOrderCode", source = "processOrderDO.code")
    @Mapping(target = "projectCode", source = "processOrderDetailDO.projectCode")
    @Mapping(target = "componentCode", source = "processOrderDetailDO.componentCode")
    @Mapping(target = "componentName", source = "processOrderDetailDO.componentName")
    @Mapping(target = "partCode", source = "processOrderDetailDO.code")
    @Mapping(target = "partName", source = "processOrderDetailDO.name")
    @Mapping(target = "partVersion", source = "processOrderDetailDO.version")
    @Mapping(target = "partNumber", source = "processOrderDetailDO.currentNumber")
    @Mapping(target = "rawMaterial", source = "processOrderDetailDO.rawMaterial")
    @Mapping(target = "remark", source = "processOrderDetailDO.remark")
    @Mapping(target = "weight", source = "processOrderDetailDO.weight")
    @Mapping(target = "printData", source = "processOrderDetailDO.printDate")
    @Mapping(target = "designer", source = "processOrderDetailDO.designer")
    @Mapping(target = "surfaceTreatment", source = "processOrderDetailDO.surfaceTreatment")
    @Mapping(target = "paperNumber", source = "processOrderDetailDO.paperNumber")
    BomImportDTO toBomImportDto(ProcessOrderDO processOrderDO, ProcessOrderDetailDO processOrderDetailDO);

    WorksheetPlaceOrderR toWorksheetPlaceOrderR(WorksheetPlaceOrder worksheetPlaceOrder);

    List<WorksheetPlaceOrderR> toWorksheetPlaceOrderRs(List<WorksheetPlaceOrder> worksheetPlaceOrderList);

    List<WorksheetCheckCountR> toWorksheetCheckCountRs(List<WorksheetCheck> worksheetCheckList);

    WorksheetCheckCountR toWorksheetCheckCountR(WorksheetCheck worksheetCheck);
}
