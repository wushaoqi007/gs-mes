package com.greenstone.mes.material.application.service.impl;

import com.greenstone.mes.common.core.web.page.PageList;
import com.greenstone.mes.material.application.dto.ProjectImportCommand;
import com.greenstone.mes.material.domain.entity.Project;
import com.greenstone.mes.material.dto.ProjectListQuery;
import com.greenstone.mes.material.interfaces.response.ProjectListResp;
import com.greenstone.mes.material.interfaces.transfer.ProjectTransfer;
import com.greenstone.mes.material.application.service.ProjectManger;
import com.greenstone.mes.material.domain.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-01-09-15:29
 */
@Slf4j
@Validated
@Service
public class ProjectMangerImpl implements ProjectManger {

    private final ProjectTransfer projectTransfer;

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectMangerImpl(ProjectTransfer projectTransfer, ProjectRepository projectRepository) {
        this.projectTransfer = projectTransfer;
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional
    public void importProject(@Valid ProjectImportCommand projectImportCommand) {
        projectImportCommand.trim();

        for (ProjectImportCommand.ProjectInfo projectInfoImportCommand : projectImportCommand.getProjects()) {
            Project project = projectTransfer.toProject(projectInfoImportCommand);
            projectRepository.saveProject(project);
        }
    }

    @Override
    public PageList<ProjectListResp> list(ProjectListQuery listQuery) {
        PageList<Project> pageList = projectRepository.list(listQuery);
        List<ProjectListResp> projectListRespList = projectTransfer.toProjectListRespList(pageList.getResultList());

        return PageList.of(pageList, projectListRespList);
    }
}
