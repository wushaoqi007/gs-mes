package com.greenstone.mes.material.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.material.domain.MaterialTaskMember;
import com.greenstone.mes.material.domain.service.IMaterialTaskMemberService;
import com.greenstone.mes.material.infrastructure.mapper.MaterialTaskMemberMapper;
import org.springframework.stereotype.Service;

/**
 * 任务的人员关联service业务层处理
 *
 * @author wushaoqi
 * @date 2022-08-08-10:58
 */
@Service
public class MaterialTaskMemberServiceImpl extends ServiceImpl<MaterialTaskMemberMapper, MaterialTaskMember> implements IMaterialTaskMemberService {
}
