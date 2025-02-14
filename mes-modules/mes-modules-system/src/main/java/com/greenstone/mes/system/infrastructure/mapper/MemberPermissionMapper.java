package com.greenstone.mes.system.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.system.domain.entity.MemberPermission;
import com.greenstone.mes.system.dto.result.MemberFunctionResult;
import com.greenstone.mes.system.infrastructure.po.MemberPermissionDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-22-9:24
 */
@Repository
public interface MemberPermissionMapper extends EasyBaseMapper<MemberPermissionDO> {

    List<MemberPermission> selectMemberPermissionsByMemberId(Long memberId);

    List<MemberPermission> selectDetailsByMemberId(Long memberId);

    List<MemberFunctionResult> selectMemberFunctions(Long memberId);
}
