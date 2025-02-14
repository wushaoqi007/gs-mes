package com.greenstone.mes.material.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.material.domain.MaterialTask;
import com.greenstone.mes.material.request.MaterialTaskListReq;
import com.greenstone.mes.material.response.MaterialTaskListResp;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialTaskMapper extends BaseMapper<MaterialTask> {

    /**
     * 查询任务列表
     */
    List<MaterialTaskListResp> selectMaterialTaskList(MaterialTaskListReq materialTaskListReq);

    /**
     * 查询任务列表（未关闭）
     */
    List<MaterialTaskListResp> selectMaterialTaskNotCloseList(MaterialTaskListReq materialTaskListReq);

    /**
     * 查询任务列表（由我参与）
     */
    List<MaterialTaskListResp> selectMaterialTaskMyList(MaterialTaskListReq materialTaskListReq);
}
