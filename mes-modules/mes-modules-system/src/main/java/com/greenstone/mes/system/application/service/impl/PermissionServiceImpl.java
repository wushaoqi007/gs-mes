package com.greenstone.mes.system.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.system.application.dto.cmd.FunctionPermissionSaveCmd;
import com.greenstone.mes.system.application.dto.cmd.MemberPermissionSaveCmd;
import com.greenstone.mes.system.application.dto.result.*;
import com.greenstone.mes.system.application.service.NavigationService;
import com.greenstone.mes.system.application.service.PermissionService;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.domain.entity.Function;
import com.greenstone.mes.system.domain.entity.MemberPermission;
import com.greenstone.mes.system.domain.entity.Role;
import com.greenstone.mes.system.domain.entity.RoleUser;
import com.greenstone.mes.system.domain.repository.*;
import com.greenstone.mes.system.domain.service.UserService;
import com.greenstone.mes.system.infrastructure.enums.FunctionType;
import com.greenstone.mes.system.infrastructure.enums.MemberType;
import com.greenstone.mes.system.infrastructure.po.FunctionPermissionDO;
import com.greenstone.mes.system.infrastructure.po.MemberNavigationDO;
import com.greenstone.mes.system.infrastructure.po.MemberPermissionDO;
import com.greenstone.mes.system.infrastructure.po.PermissionGroupTempDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author wushaoqi
 * @date 2024-10-22-9:40
 */
@AllArgsConstructor
@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    private final NavigationService navigationService;
    private final MemberNavigationRepository memberNavigationRepository;
    private final MemberPermissionRepository memberPermissionRepository;
    private final FunctionPermissionRepository functionPermissionRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final FunctionRepository functionRepository;
    private final PermissionGroupTempRepository permissionGroupTempRepository;

    @Transactional
    @Override
    public void setMemberPermissions(MemberPermissionSaveCmd saveCmd) {
        // 已分配角色的用户先删除用户角色
        if (saveCmd.getMemberType().equals(MemberType.USER.getType())) {
            roleRepository.removeRoleUserByUserId(saveCmd.getMemberId());
            // 更新用户表角色
            userRepository.removeRole(saveCmd.getMemberId(), null);
        }
        // 所有功能权限组
        List<FunctionPermissionDO> functionPermissions = functionPermissionRepository.listAll();
        // 所有已启用的导航
        List<NavigationResult> navigations = navigationService.listAllActive();
        List<MemberPermissionDO> insertMemberPermissionDOs = new ArrayList<>();
        List<MemberNavigationDO> insertMemberNavigationDOs = new ArrayList<>();
        // 用于校验：成员只能拥有功能的一种权限
        List<Long> allocatedFunctionIds = new ArrayList<>();
        // 待保存功能权限组
        for (Long functionPermissionId : saveCmd.getFunctionPermissionIds()) {
            Optional<FunctionPermissionDO> findFp = functionPermissions.stream().filter(fp -> Objects.equals(fp.getId(), functionPermissionId)).findFirst();
            if (findFp.isEmpty()) {
                throw new ServiceException(StrUtil.format("功能权限组未找到，功能权限组id:{}", functionPermissionId));
            }
            FunctionPermissionDO functionPermission = findFp.get();
            Optional<NavigationResult> findNa = navigations.stream().filter(n -> Objects.equals(n.getFunctionId(), functionPermission.getFunctionId())).findFirst();
            if (findNa.isEmpty()) {
                throw new ServiceException(StrUtil.format("该功能未配置导航，无法配置权限，功能id:{}", functionPermission.getFunctionId()));
            }
            if (allocatedFunctionIds.contains(functionPermission.getFunctionId())) {
                throw new ServiceException(StrUtil.format("成员只能拥有功能的一种权限，重复功能id：{}，重复权限组id:{}", functionPermission.getFunctionId(), functionPermissionId));
            }
            allocatedFunctionIds.add(functionPermission.getFunctionId());
            // 新增成员权限
            insertMemberPermissionDOs.add(MemberPermissionDO.builder().memberId(saveCmd.getMemberId())
                    .memberType(saveCmd.getMemberType()).functionPermissionId(functionPermissionId).build());
        }
        // 待分配的导航
        List<Long> waitAllocatedNavigationIds = saveCmd.getNavigationIds().stream().distinct().toList();
        for (Long navigationId : waitAllocatedNavigationIds) {
            // 新增成员导航
            insertMemberNavigationDOs.add(MemberNavigationDO.builder().memberId(saveCmd.getMemberId())
                    .memberType(saveCmd.getMemberType()).navigationId(navigationId).build());
        }
        // 删除成员原有的权限
        memberPermissionRepository.deleteMemberPermissionByMemberId(saveCmd.getMemberId());
        // 删除成员原有的导航
        memberNavigationRepository.deleteByMemberId(saveCmd.getMemberId());
        memberPermissionRepository.addMemberPermissions(insertMemberPermissionDOs);
        memberNavigationRepository.addMemberNavigations(insertMemberNavigationDOs);
    }

    @Transactional
    @Override
    public void setFunctionPermissions(FunctionPermissionSaveCmd saveCmd) {
        List<Long> userIds = saveCmd.getMembers().stream().filter(m -> m.getMemberType().equals(MemberType.USER.getType())).map(FunctionPermissionSaveCmd.MemberInfo::getMemberId).toList();
        // 已分配角色的用户不能单独分配权限
        if (CollUtil.isNotEmpty(userIds)) {
            List<RoleUser> roleUsers = roleRepository.selectRoleUsersByUserIds(userIds);
            if (CollUtil.isNotEmpty(roleUsers)) {
                throw new ServiceException(StrUtil.format("已分配角色的用户不能单独分配权限:{}", roleUsers));
            }
        }
        // 该功能权限组
        FunctionPermissionDO functionPermission = functionPermissionRepository.selectByFunctionPermissionId(saveCmd.getFunctionPermissionId());
        if (functionPermission == null) {
            throw new ServiceException(StrUtil.format("功能权限组未找到，功能权限组id:{}", saveCmd.getFunctionPermissionId()));
        }
        // 该功能所有权限组
        List<FunctionPermissionDO> allFunctionPermission = functionPermissionRepository.selectByFunctionId(functionPermission.getFunctionId());
        List<Long> allFunctionPermIds = allFunctionPermission.stream().map(FunctionPermissionDO::getId).toList();
        // 该功能已启用的导航
        List<NavigationSelectResult> navigationSelectResults = navigationService.selectByFunctionId(functionPermission.getFunctionId());
        if (CollUtil.isEmpty(navigationSelectResults)) {
            throw new ServiceException(StrUtil.format("该功能未配置导航，无法配置权限，功能id:{}", functionPermission.getFunctionId()));
        }
        List<Long> oldNavigationIds = navigationSelectResults.stream().map(NavigationSelectResult::getNavigationId).toList();
        List<MemberPermissionDO> insertMemberPermissionDOs = new ArrayList<>();
        List<MemberNavigationDO> insertMemberNavigationDOs = new ArrayList<>();
        // 待分配的导航
        List<Long> waitAllocatedNavigationIds = saveCmd.getNavigationIds().stream().distinct().toList();
        for (Long waitAllocatedNavigationId : waitAllocatedNavigationIds) {
            if (!oldNavigationIds.contains(waitAllocatedNavigationId)) {
                throw new ServiceException(StrUtil.format("选择的导航不属于该功能，导航id:{}", waitAllocatedNavigationId));
            }
        }
        for (FunctionPermissionSaveCmd.MemberInfo member : saveCmd.getMembers()) {
            // 先删除成员原来在该功能有的权限
            memberPermissionRepository.deleteMemberOldPerm(member.getMemberId(), allFunctionPermIds);
            // 删除成员原有的该功能导航
            memberNavigationRepository.deleteMemberOldNavigation(member.getMemberId(), oldNavigationIds);
            // 新增成员权限
            insertMemberPermissionDOs.add(MemberPermissionDO.builder().memberId(member.getMemberId()).memberType(member.getMemberType()).functionPermissionId(saveCmd.getFunctionPermissionId()).build());
            // 新增成员导航
            for (Long navigationId : waitAllocatedNavigationIds) {
                insertMemberNavigationDOs.add(MemberNavigationDO.builder().memberId(member.getMemberId()).memberType(member.getMemberType()).navigationId(navigationId).build());
            }
        }
        memberPermissionRepository.addMemberPermissions(insertMemberPermissionDOs);
        memberNavigationRepository.addMemberNavigations(insertMemberNavigationDOs);
    }

    @Transactional
    @Override
    public void removePermission(Long memberId, Long functionPermissionId) {
        MemberPermissionDO memberPermission = memberPermissionRepository.selectByMemberIdAndFunctionPermissionId(memberId, functionPermissionId);
        if (memberPermission == null) {
            throw new ServiceException(StrUtil.format("未找到该权限，成员id:{}，功能权限组id:{}", memberId, functionPermissionId));
        }
        memberPermissionRepository.deleteMemberPermission(memberPermission);
        // 删除导航成员关系
        FunctionPermissionDO functionPermission = functionPermissionRepository.selectByFunctionPermissionId(functionPermissionId);
        if (functionPermission != null) {
            List<NavigationSelectResult> navigationSelectResults = navigationService.selectByFunctionId(functionPermission.getFunctionId());
            if (CollUtil.isNotEmpty(navigationSelectResults)) {
                List<Long> navigationIds = navigationSelectResults.stream().map(NavigationSelectResult::getNavigationId).toList();
                memberNavigationRepository.deleteMemberOldNavigation(memberId, navigationIds);
            }
        }
    }

    @Override
    public void initFunctionPerm() {
        List<FunctionPermissionDO> insertFunctionPermissions = new ArrayList<>();
        List<Function> functions = functionRepository.listAll();
        List<PermissionGroupTempDO> permissionGroupTemps = permissionGroupTempRepository.listAll();
        List<PermissionGroupTempDO> pagePermissionGroupTemp = permissionGroupTemps.stream().filter(PermissionGroupTempDO::getPagePermission).toList();
        List<PermissionGroupTempDO> tablePermissionGroupTemp = permissionGroupTemps.stream().filter(p -> !p.getPagePermission()).toList();
        for (Function function : functions) {
            if (function.getType().equals(FunctionType.PAGE.getType())) {
                for (PermissionGroupTempDO permissionGroupTemp : pagePermissionGroupTemp) {
                    insertFunctionPermissions.add(FunctionPermissionDO.builder().functionId(function.getId())
                            .permissionGroupName(permissionGroupTemp.getTypeName())
                            .permissionGroupTypeName(permissionGroupTemp.getTypeName())
                            .rights(permissionGroupTemp.getRights())
                            .viewFilter(permissionGroupTemp.getViewFilter())
                            .updateFilter(permissionGroupTemp.getUpdateFilter()).build());
                }
            } else {
                for (PermissionGroupTempDO permissionGroupTemp : tablePermissionGroupTemp) {
                    insertFunctionPermissions.add(FunctionPermissionDO.builder().functionId(function.getId())
                            .permissionGroupName(permissionGroupTemp.getTypeName())
                            .permissionGroupTypeName(permissionGroupTemp.getTypeName())
                            .rights(permissionGroupTemp.getRights())
                            .viewFilter(permissionGroupTemp.getViewFilter())
                            .updateFilter(permissionGroupTemp.getUpdateFilter()).build());
                }
            }
        }
        functionPermissionRepository.addFunctionPermissions(insertFunctionPermissions);
    }

    @Override
    public MemberNavigationTreeResult selectMemberNavigationTree(Long memberId) {
        // 查询所有已启用的导航
        List<NavigationResult> navigationResults = navigationService.listAllActive();
        List<NavigationTree> navigationTreeList = navigationService.buildNavigationTree(navigationResults);
        // 查询成员已授权的导航
        List<MemberNavigationDO> memberNavigationList = memberNavigationRepository.selectMemberNavigationsByMemberId(memberId);
        List<Long> checkedNavigationIds = new ArrayList<>();
        if (CollUtil.isNotEmpty(memberNavigationList)) {
            checkedNavigationIds = memberNavigationList.stream().map(MemberNavigationDO::getNavigationId).toList();
        }
        return MemberNavigationTreeResult.builder().checkedNavigationIds(checkedNavigationIds).navigations(navigationTreeList).build();
    }

    @Override
    public Map<Long, Long> selectMemberPermissions(Long memberId) {
        Map<Long, Long> memberPermMap = new HashMap<>();
        List<MemberPermission> memberPermissions = memberPermissionRepository.selectMemberPermissionsByMemberId(memberId);
        if (CollUtil.isNotEmpty(memberPermissions)) {
            for (MemberPermission memberPermission : memberPermissions) {
                memberPermMap.put(memberPermission.getFunctionId(), memberPermission.getFunctionPermissionId());
            }
        } else {
            log.info("该成员未拥有任何权限，成员id:{}", memberId);
        }
        return memberPermMap;
    }

    @Override
    public List<FunctionPermissionWithMembersResult> selectFunctionPermissionsWithMembers(Long functionId) {
        List<FunctionPermissionWithMembersResult> results = new ArrayList<>();
        // 查询功能
        functionRepository.exist(functionId);
        // 查询功能有哪些权限组
        List<FunctionPermissionDO> functionPermissions = functionPermissionRepository.selectByFunctionId(functionId);
        // 所有用户和角色
        List<User> allUsers = userService.getUsers(User.builder().build());
        List<Role> allRoles = roleRepository.listAll();
        for (FunctionPermissionDO functionPermission : functionPermissions) {
            FunctionPermissionWithMembersResult result = FunctionPermissionWithMembersResult.builder()
                    .functionId(functionPermission.getFunctionId())
                    .functionPermissionId(functionPermission.getId())
                    .permissionGroupName(functionPermission.getPermissionGroupName())
                    .permissionGroupTypeName(functionPermission.getPermissionGroupTypeName())
                    .rights(functionPermission.getRights())
                    .viewFilter(functionPermission.getViewFilter())
                    .updateFilter(functionPermission.getUpdateFilter()).build();
            results.add(result);
            // 功能的权限组下有哪些成员
            List<MemberPermissionDO> memberPermissions = memberPermissionRepository.selectMemberPermissionsByFunctionPermId(functionPermission.getId());
            if (CollUtil.isNotEmpty(memberPermissions)) {
                List<FunctionPermissionWithMembersResult.MemberInfo> members = new ArrayList<>();
                result.setMembers(members);
                for (MemberPermissionDO memberPermission : memberPermissions) {
                    FunctionPermissionWithMembersResult.MemberInfo memberInfo = FunctionPermissionWithMembersResult.MemberInfo.builder().memberId(memberPermission.getMemberId()).memberType(memberPermission.getMemberType()).build();
                    members.add(memberInfo);
                    // 填充成员名称信息
                    if (memberPermission.getMemberType().equals(MemberType.USER.getType())) {
                        Optional<User> findUser = allUsers.stream().filter(u -> Objects.equals(u.getUserId(), memberPermission.getMemberId())).findFirst();
                        findUser.ifPresent(userPo -> memberInfo.setMemberName(userPo.getNickName()));
                    }
                    if (memberPermission.getMemberType().equals(MemberType.ROLE.getType())) {
                        Optional<Role> findRole = allRoles.stream().filter(r -> Objects.equals(r.getRoleId(), memberPermission.getMemberId())).findFirst();
                        findRole.ifPresent(role -> memberInfo.setMemberName(role.getRoleName()));
                    }
                }
            }
        }
        return results;
    }

}
