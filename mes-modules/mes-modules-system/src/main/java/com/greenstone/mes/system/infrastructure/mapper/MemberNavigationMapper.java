package com.greenstone.mes.system.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.system.domain.entity.MemberNavigation;
import com.greenstone.mes.system.dto.result.MemberNavigationResult;
import com.greenstone.mes.system.infrastructure.po.MemberNavigationDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-22-9:25
 */
@Repository
public interface MemberNavigationMapper extends EasyBaseMapper<MemberNavigationDO> {
    List<MemberNavigation> selectDetailsByMemberId(Long memberId);

    List<MemberNavigationResult> selectMemberNavigation(Long memberId);
}
