package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.material.domain.MaterialTask;
import com.greenstone.mes.material.request.MaterialTaskListReq;
import com.greenstone.mes.material.response.MaterialTaskListResp;

import java.util.List;

/**
 * 任务管控service
 *
 * @author wushaoqi
 * @date 2022-08-08-10:53
 */
public interface IMaterialTaskService extends IServiceWrapper<MaterialTask> {
    /**
     * 查询任务列表
     */
    List<MaterialTaskListResp> selectMaterialTaskList(MaterialTaskListReq materialTaskListReq);

    /**
     * 查询任务列表(未关闭)
     */
    List<MaterialTaskListResp> selectMaterialTaskNotCloseList(MaterialTaskListReq materialTaskListReq);

    /**
     * 查询任务列表(由我参与)
     */
    List<MaterialTaskListResp> selectMaterialTaskMyList(MaterialTaskListReq materialTaskListReq);
}
