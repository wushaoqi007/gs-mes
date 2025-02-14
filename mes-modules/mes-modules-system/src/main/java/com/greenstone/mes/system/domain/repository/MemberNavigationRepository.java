package com.greenstone.mes.system.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.system.domain.entity.MemberNavigation;
import com.greenstone.mes.system.dto.result.MemberNavigationResult;
import com.greenstone.mes.system.infrastructure.mapper.MemberNavigationMapper;
import com.greenstone.mes.system.infrastructure.po.MemberNavigationDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-22-9:38
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class MemberNavigationRepository {
    private final MemberNavigationMapper memberNavigationMapper;

    public List<MemberNavigationDO> selectMemberNavigationsByMemberId(Long memberId) {
        log.info("查询成员导航权限，成员id:{}", memberId);
        return memberNavigationMapper.list(MemberNavigationDO.builder().memberId(memberId).build());
    }

    public List<MemberNavigationResult> selectMemberNavigation(Long memberId) {
        log.info("查询成员导航权限详情，成员id:{}", memberId);
        return memberNavigationMapper.selectMemberNavigation(memberId);
    }

    public List<MemberNavigation> selectDetailsByMemberId(Long memberId) {
        log.info("查询成员导航权限详情，成员id:{}", memberId);
        return memberNavigationMapper.selectDetailsByMemberId(memberId);
    }

    public List<MemberNavigationDO> selectByMemberIdsAndNavigationIds(List<Long> memberIds, List<Long> navigationIds) {
        log.info("查询成员导航权限，成员id:{},导航id:{}", memberIds, navigationIds);
        LambdaQueryWrapper<MemberNavigationDO> queryWrapper = Wrappers.lambdaQuery(MemberNavigationDO.class)
                .in(MemberNavigationDO::getMemberId, memberIds)
                .in(MemberNavigationDO::getNavigationId, navigationIds);
        return memberNavigationMapper.selectList(queryWrapper);
    }

    public void deleteByMemberId(Long memberId) {
        log.info("删除成员导航权限，成员id:{}", memberId);
        LambdaQueryWrapper<MemberNavigationDO> deleteWrapper = Wrappers.lambdaQuery(MemberNavigationDO.class)
                .eq(MemberNavigationDO::getMemberId, memberId);
        memberNavigationMapper.delete(deleteWrapper);
    }

    public void deleteMemberOldNavigation(Long memberId, List<Long> navigationIds) {
        log.info("删除旧成员导航权限，成员id:{},导航id:{}", memberId, navigationIds);
        LambdaQueryWrapper<MemberNavigationDO> deleteWrapper = Wrappers.lambdaQuery(MemberNavigationDO.class)
                .eq(MemberNavigationDO::getMemberId, memberId).in(MemberNavigationDO::getNavigationId, navigationIds);
        memberNavigationMapper.delete(deleteWrapper);
    }

    public void addMemberNavigations(List<MemberNavigationDO> insertMemberNavigationDOs) {
        log.info("新增成员导航权限：{}", insertMemberNavigationDOs);
        memberNavigationMapper.insertBatchSomeColumn(insertMemberNavigationDOs);
    }

    public void deleteByNavigationId(List<Long> navigationIds) {
        log.info("删除成员导航权限，导航id:{}", navigationIds);
        LambdaQueryWrapper<MemberNavigationDO> deleteWrapper = Wrappers.lambdaQuery(MemberNavigationDO.class)
                .in(MemberNavigationDO::getNavigationId, navigationIds);
        memberNavigationMapper.delete(deleteWrapper);
    }


}
