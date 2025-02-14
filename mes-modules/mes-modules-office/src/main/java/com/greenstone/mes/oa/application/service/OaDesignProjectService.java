package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.oa.domain.OaDesignProject;
import com.greenstone.mes.oa.request.OaDesignProjectAddReq;
import com.greenstone.mes.oa.request.OaDesignProjectEditReq;
import com.greenstone.mes.oa.response.OaDesignProjectListResp;

import java.util.List;

public interface OaDesignProjectService extends IServiceWrapper<OaDesignProject> {

    List<OaDesignProject> getProject(String projectCode);

    void saveProject(List<OaDesignProjectAddReq> addReqs);

    void updateProject(List<OaDesignProjectEditReq> editReqs);

    List<OaDesignProjectListResp> getProjects(List<OaDesignProject> projects);

}
