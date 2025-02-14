package com.greenstone.mes.material.application.service;

import com.greenstone.mes.common.core.web.page.PageList;
import com.greenstone.mes.material.application.dto.ProjectImportCommand;
import com.greenstone.mes.material.dto.ProjectListQuery;
import com.greenstone.mes.material.interfaces.response.ProjectListResp;

import javax.validation.Valid;

/**
 * @author wushaoqi
 * @date 2023-01-09-15:28
 */
public interface ProjectManger {

    void importProject(@Valid ProjectImportCommand projectImportCommand);

    PageList<ProjectListResp> list(ProjectListQuery listQuery);
}
