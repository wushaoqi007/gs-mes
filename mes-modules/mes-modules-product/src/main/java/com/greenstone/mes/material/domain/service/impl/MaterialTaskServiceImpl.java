package com.greenstone.mes.material.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.material.domain.MaterialTask;
import com.greenstone.mes.material.domain.service.IMaterialTaskService;
import com.greenstone.mes.material.infrastructure.mapper.MaterialTaskMapper;
import com.greenstone.mes.material.request.MaterialTaskListReq;
import com.greenstone.mes.material.response.MaterialTaskListResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务管控service业务层处理
 *
 * @author wushaoqi
 * @date 2022-08-08-10:58
 */
@Service
public class MaterialTaskServiceImpl extends ServiceImpl<MaterialTaskMapper, MaterialTask> implements IMaterialTaskService {

    @Autowired
    private MaterialTaskMapper materialTaskMapper;


    @Override
    public List<MaterialTaskListResp> selectMaterialTaskList(MaterialTaskListReq materialTaskListReq) {
        return materialTaskMapper.selectMaterialTaskList(materialTaskListReq);
    }

    @Override
    public List<MaterialTaskListResp> selectMaterialTaskNotCloseList(MaterialTaskListReq materialTaskListReq) {
        return materialTaskMapper.selectMaterialTaskNotCloseList(materialTaskListReq);
    }

    @Override
    public List<MaterialTaskListResp> selectMaterialTaskMyList(MaterialTaskListReq materialTaskListReq) {
        return materialTaskMapper.selectMaterialTaskMyList(materialTaskListReq);
    }

}
