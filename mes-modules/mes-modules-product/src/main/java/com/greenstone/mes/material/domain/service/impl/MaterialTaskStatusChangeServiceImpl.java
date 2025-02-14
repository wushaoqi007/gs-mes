package com.greenstone.mes.material.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.material.domain.MaterialTaskStatusChange;
import com.greenstone.mes.material.domain.service.IMaterialTaskStatusChangeService;
import com.greenstone.mes.material.infrastructure.mapper.MaterialTaskStatusChangeMapper;
import org.springframework.stereotype.Service;

/**
 * 任务状态变更记录service业务层处理
 *
 * @author wushaoqi
 * @date 2022-08-08-10:58
 */
@Service
public class MaterialTaskStatusChangeServiceImpl extends ServiceImpl<MaterialTaskStatusChangeMapper, MaterialTaskStatusChange> implements IMaterialTaskStatusChangeService {
}
