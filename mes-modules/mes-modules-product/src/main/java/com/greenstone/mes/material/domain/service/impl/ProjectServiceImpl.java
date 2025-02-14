package com.greenstone.mes.material.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.material.domain.ProjectDO;
import com.greenstone.mes.material.domain.service.ProjectService;
import com.greenstone.mes.material.infrastructure.mapper.ProjectMapper;
import org.springframework.stereotype.Service;

/**
 * 项目ServiceImpl
 *
 * @author wushaoqi
 * @date 2023-01-09-10:10
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, ProjectDO> implements ProjectService {

    @Override
    public ProjectDO selectByCode(String projectCode) {
        ProjectDO selectDo = ProjectDO.builder().projectCode(projectCode).build();
        return getOneOnly(selectDo);
    }
}
