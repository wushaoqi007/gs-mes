package com.greenstone.mes.material.interfaces.transfer;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.application.dto.ProjectImportCommand;
import com.greenstone.mes.material.domain.ProjectDO;
import com.greenstone.mes.material.domain.entity.Project;
import com.greenstone.mes.material.interfaces.response.ProjectListResp;
import com.greenstone.mes.material.request.ProjectImportVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-01-09-10:39
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class, List.class, StrUtil.class}
)
public interface ProjectTransfer {

    List<ProjectImportCommand.ProjectInfo> toImportCommands(List<ProjectImportVO> projectImportVOList);


    @Mapping(target = "softwareJoin", expression = "java(\"1\".equals(projectImportVO.getSoftwareJoin()))")
    ProjectImportCommand.ProjectInfo toImportCommand(ProjectImportVO projectImportVO);

    ProjectDO toProjectDo(Project project);

    @Mapping(target = "projectInitiationTime", expression = "java(projectInfoImportCommand.validateAndFormatProjectInitiationTime(projectInfoImportCommand.getProjectInitiationTime()))")
    @Mapping(target = "designDeadline", expression = "java(projectInfoImportCommand.validateAndFormatDesignDeadline(projectInfoImportCommand.getDesignDeadline()))")
    @Mapping(target = "customerDeadline", expression = "java(projectInfoImportCommand.validateAndFormatCustomerDeadline(projectInfoImportCommand.getCustomerDeadline()))")
    @Mapping(target = "orderReceiveTime", expression = "java(projectInfoImportCommand.validateAndFormatOrderReceiveTime(projectInfoImportCommand.getOrderReceiveTime()))")
    Project toProject(ProjectImportCommand.ProjectInfo projectInfoImportCommand);

    List<Project> toProjects(List<ProjectDO> projectDOS);

    Project toProject(ProjectDO projectDO);

    List<ProjectListResp> toProjectListRespList(List<Project> resultList);
    ProjectListResp toProjectListResp(Project project);
}
