package com.greenstone.mes.external.domain.converter;

import com.greenstone.mes.external.application.dto.result.TaskResult;
import com.greenstone.mes.external.domain.entity.*;
import com.greenstone.mes.external.infrastructure.persistence.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/23 11:08
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProcessConverter {
    // BillProcDef
    ProcessDefinitionDO toProcessDefinitionDO(ProcessDefinition billProcdef);

    ProcessDefinition toProcessDefinition(ProcessDefinitionDO billProcdefDO);

    HiProcessDefinitionDo toHiProcessDefinitionDO(ProcessDefinition billProcdef);

    // BillTask
    ProcessTask toTask(ProcessInstance processInstance);

    ProcessTask toTask(TaskDO taskDO);

    List<ProcessTask> toTasks(List<TaskDO> taskDOS);

    TaskDO toTaskDO(ProcessTask task);

    TaskResult toTaskR(ProcessTask task);

    List<TaskResult> toTaskRs(List<ProcessTask> tasks);

    // BillProcInst
    ProcessInstance toBillProcInst(ProcessInstanceDO processInstanceDO);

    ProcessInstanceDO toBillProcInstDO(ProcessInstance processInstance);

    // NodeDef
    NodeDefinition toNodeDef(NodeDefinitionDO nodeDefinitionDO);

    NodeDefinitionDO toNodeDefDO(NodeDefinition nodeDefinition);

    List<NodeDefinitionDO> toNodeDefDOList(List<NodeDefinition> nodeDefinitionList);

    // TaskIdentityLink
    TaskIdentityLinkDO toTaskIdentityLinkDO(TaskIdentityLink link);

    List<TaskIdentityLinkDO> toTaskIdentityLinkDOs(List<TaskIdentityLink> links);

    TaskIdentityLink toTaskIdentityLink(TaskIdentityLinkDO linkDO);

    List<TaskIdentityLink> toTaskIdentityLinks(List<TaskIdentityLinkDO> linkDOs);

    // ProcessCopy
    ProcessCopyDO toProcessCopyDO(ProcessCopy processCopy);

    ProcessCopy toProcessCopy(ProcessCopyDO processCopyDO);

    List<ProcessCopy> toProcessCopies(List<ProcessCopyDO> processCopyDOs);
}
