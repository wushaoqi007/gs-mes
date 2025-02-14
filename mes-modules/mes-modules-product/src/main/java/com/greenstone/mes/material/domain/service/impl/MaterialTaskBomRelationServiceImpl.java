
package com.greenstone.mes.material.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.material.domain.MaterialTaskBomRelation;
import com.greenstone.mes.material.domain.service.IMaterialTaskBomRelationService;
import com.greenstone.mes.material.infrastructure.mapper.MaterialTaskBomRelationMapper;
import org.springframework.stereotype.Service;

/**
 * 任务的组件关联service业务层处理
 *
 * @author wushaoqi
 * @date 2022-08-08-10:58
 */
@Service
public class MaterialTaskBomRelationServiceImpl extends ServiceImpl<MaterialTaskBomRelationMapper, MaterialTaskBomRelation> implements IMaterialTaskBomRelationService {
}
