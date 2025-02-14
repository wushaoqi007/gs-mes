package com.greenstone.mes.system.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.system.domain.entity.MemberPermission;
import com.greenstone.mes.system.dto.result.MemberFunctionResult;
import com.greenstone.mes.system.infrastructure.mapper.MemberPermissionMapper;
import com.greenstone.mes.system.infrastructure.po.MemberPermissionDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-22-9:37
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class MemberPermissionRepository {
    private final MemberPermissionMapper memberPermissionMapper;

    public List<MemberPermission> selectMemberPermissionsByMemberId(Long memberId) {
        log.info("查询成员权限，成员id:{}", memberId);
        return memberPermissionMapper.selectMemberPermissionsByMemberId(memberId);
    }

    public List<MemberFunctionResult> selectMemberFunctions(Long memberId) {
        log.info("查询成员功能权限，成员id:{}", memberId);
        return memberPermissionMapper.selectMemberFunctions(memberId);
    }

    public List<MemberPermission> selectDetailsByMemberId(Long memberId) {
        log.info("查询成员权限详情，成员id:{}", memberId);
        return memberPermissionMapper.selectDetailsByMemberId(memberId);
    }

    public List<MemberPermissionDO> selectMemberPermissionsByFunctionPermId(Long functionPermissionId) {
        log.info("查询成员权限，权限组id:{}", functionPermissionId);
        return memberPermissionMapper.list(MemberPermissionDO.builder().functionPermissionId(functionPermissionId).build());
    }

    public List<MemberPermissionDO> selectByFunctionPermIds(List<Long> functionPermissionIds) {
        log.info("查询成员权限，权限组id：{}", functionPermissionIds);
        LambdaQueryWrapper<MemberPermissionDO> queryWrapper = Wrappers.lambdaQuery(MemberPermissionDO.class)
                .in(MemberPermissionDO::getFunctionPermissionId, functionPermissionIds);
        return memberPermissionMapper.selectList(queryWrapper);
    }

    public void deleteMemberPermission(MemberPermissionDO memberPermission) {
        log.info("删除成员权限：{}", memberPermission);
        LambdaQueryWrapper<MemberPermissionDO> deleteWrapper = Wrappers.lambdaQuery(MemberPermissionDO.class)
                .eq(MemberPermissionDO::getMemberId, memberPermission.getMemberId())
                .eq(MemberPermissionDO::getFunctionPermissionId, memberPermission.getFunctionPermissionId());
        memberPermissionMapper.delete(deleteWrapper);
    }

    public void addMemberPermissions(List<MemberPermissionDO> insertMemberPermissionDOs) {
        log.info("新增成员权限：{}", insertMemberPermissionDOs);
        memberPermissionMapper.insertBatchSomeColumn(insertMemberPermissionDOs);
    }

    public void deleteMemberPermissionByMemberId(Long memberId) {
        log.info("删除成员权限，成员id:{}", memberId);
        LambdaQueryWrapper<MemberPermissionDO> deleteWrapper = Wrappers.lambdaQuery(MemberPermissionDO.class)
                .eq(MemberPermissionDO::getMemberId, memberId);
        memberPermissionMapper.delete(deleteWrapper);
    }

    public void deleteMemberOldPerm(Long memberId, List<Long> functionPermissionIds) {
        log.info("删除旧的成员权限，成员id:{},权限组id:{}", memberId, functionPermissionIds);
        LambdaQueryWrapper<MemberPermissionDO> deleteWrapper = Wrappers.lambdaQuery(MemberPermissionDO.class)
                .eq(MemberPermissionDO::getMemberId, memberId).in(MemberPermissionDO::getFunctionPermissionId, functionPermissionIds);
        memberPermissionMapper.delete(deleteWrapper);
    }

    public MemberPermissionDO selectByMemberIdAndFunctionPermissionId(Long memberId, Long functionPermissionId) {
        log.info("查询成员权限，成员id:{},权限组id:{}", memberId, functionPermissionId);
        if (memberId == null) {
            throw new ServiceException("成员id不能为空");
        }
        if (functionPermissionId == null) {
            throw new ServiceException("功能权限组id不能为空");
        }
        return memberPermissionMapper.getOneOnly(MemberPermissionDO.builder().memberId(memberId).functionPermissionId(functionPermissionId).build());
    }

    public void deleteMemberPermissionByFunPermId(List<Long> functionPermIds) {
        log.info("根据权限组删除成员权限，权限组id:{}", functionPermIds);
        LambdaQueryWrapper<MemberPermissionDO> deleteWrapper = Wrappers.lambdaQuery(MemberPermissionDO.class)
                .in(MemberPermissionDO::getFunctionPermissionId, functionPermIds);
        memberPermissionMapper.delete(deleteWrapper);
    }

    public void deleteMemberPermissions(List<MemberPermissionDO> deleteMemberPermissions) {
        log.info("需要删除的成员权限：{}", deleteMemberPermissions);
        for (MemberPermissionDO deleteMemberPermission : deleteMemberPermissions) {
            deleteMemberPermission(deleteMemberPermission);
        }
    }
}
