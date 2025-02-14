package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.material.domain.ProjectDO;

/**
 * 项目Service
 *
 * @author wushaoqi
 * @date 2023-01-09-10:09
 */
public interface ProjectService extends IServiceWrapper<ProjectDO> {

    ProjectDO selectByCode(String projectCode);
}
