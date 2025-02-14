package com.greenstone.mes.material.domain.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.web.page.PageList;
import com.greenstone.mes.material.domain.ProjectDO;
import com.greenstone.mes.material.domain.entity.Project;
import com.greenstone.mes.material.dto.ProjectListQuery;
import com.greenstone.mes.material.interfaces.transfer.ProjectTransfer;
import com.greenstone.mes.material.domain.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-01-09-11:05
 */
@Slf4j
@Service
public class ProjectRepository {

    private final ProjectService projectService;
    private final ProjectTransfer projectTransfer;

    @Autowired
    public ProjectRepository(ProjectService projectService, ProjectTransfer projectTransfer) {
        this.projectService = projectService;
        this.projectTransfer = projectTransfer;
    }

    public void saveProject(Project project) {
        ProjectDO existDo = projectService.selectByCode(project.getProjectCode());
        ProjectDO projectDO = projectTransfer.toProjectDo(project);
        if (Objects.isNull(existDo)) {
            projectService.save(projectDO);
            log.info("insert project:{}", projectDO);
        } else {
            projectDO.setId(existDo.getId());
            projectService.updateById(projectDO);
            log.info("update project:{}", projectDO);
        }

    }

    public PageList<Project> list(ProjectListQuery query) {
        ProjectDO queryDO = ProjectDO.builder().projectCode(query.getProjectCode()).build();
        LambdaQueryWrapper<ProjectDO> queryWrapper = Wrappers.lambdaQuery(queryDO)
                .ge(query.getProjectInitiationStart() != null, ProjectDO::getProjectInitiationTime, query.getProjectInitiationStart())
                .le(query.getProjectInitiationEnd() != null, ProjectDO::getProjectInitiationTime, query.getProjectInitiationEnd())
                .ge(query.getDesignDeadlineStart() != null, ProjectDO::getDesignDeadline, query.getDesignDeadlineStart())
                .le(query.getDesignDeadlineEnd() != null, ProjectDO::getDesignDeadline, query.getDesignDeadlineEnd())
                .ge(query.getCustomerDeadlineStart() != null, ProjectDO::getCustomerDeadline, query.getCustomerDeadlineStart())
                .le(query.getCustomerDeadlineEnd() != null, ProjectDO::getCustomerDeadline, query.getCustomerDeadlineEnd());
        List<ProjectDO> projectDOS = projectService.list(queryWrapper);
        List<Project> projects = projectTransfer.toProjects(projectDOS);
        return PageList.of(projectDOS, projects);
    }

    public List<Project> listByDesignDeadlineInOneYear(String year, String projectCode) {
        QueryWrapper<ProjectDO> queryWrapper = Wrappers.query(ProjectDO.builder().build());
        if (StrUtil.isNotEmpty(year)) {
            queryWrapper.eq("DATE_FORMAT(design_deadline,'%Y')", year);
        }
        if (StrUtil.isNotEmpty(projectCode)) {
            queryWrapper.eq("project_code", projectCode);
        }
        List<ProjectDO> projectDOS = projectService.list(queryWrapper);
        return projectTransfer.toProjects(projectDOS);
    }
}
