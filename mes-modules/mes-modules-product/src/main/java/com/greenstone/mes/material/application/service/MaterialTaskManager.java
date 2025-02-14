package com.greenstone.mes.material.application.service;

import com.greenstone.mes.material.request.MaterialTaskAddReq;
import com.greenstone.mes.material.request.MaterialTaskEditReq;
import com.greenstone.mes.material.response.MaterialTaskDetailResp;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-08-08-14:20
 */
public interface MaterialTaskManager {

    /**
     * 新增任务
     */
    void insertMaterialTask(MaterialTaskAddReq materialTaskAddReq);

    /**
     * 根据id查询任务
     */
    MaterialTaskDetailResp selectMaterialTaskById(Long id);

    /**
     * 更新任务
     */
    void updateMaterialTask(MaterialTaskEditReq materialTaskEditReq);

    /**
     * 更新任务到下一步状态
     */
    void updateMaterialTaskStatus(MaterialTaskEditReq materialTaskEditReq);

    /**
     * 获取任务成员
     *
     * @param id 任务ID
     */
    List<MaterialTaskAddReq.MemberInfo> selectMaterialTaskMemberListById(Long id);
}
