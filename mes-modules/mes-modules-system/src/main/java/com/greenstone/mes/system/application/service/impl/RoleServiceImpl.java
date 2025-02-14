package com.greenstone.mes.system.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.system.application.dto.cmd.RoleAllocateUsersCmd;
import com.greenstone.mes.system.application.dto.cmd.RoleUserChangeCmd;
import com.greenstone.mes.system.application.dto.cmd.RoleUserRemoveCmd;
import com.greenstone.mes.system.application.service.RoleService;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.domain.entity.MemberPermission;
import com.greenstone.mes.system.domain.entity.Role;
import com.greenstone.mes.system.domain.entity.RoleUser;
import com.greenstone.mes.system.domain.repository.MemberNavigationRepository;
import com.greenstone.mes.system.domain.repository.MemberPermissionRepository;
import com.greenstone.mes.system.domain.repository.RoleRepository;
import com.greenstone.mes.system.domain.repository.UserRepository;
import com.greenstone.mes.system.infrastructure.enums.MemberType;
import com.greenstone.mes.system.infrastructure.po.MemberNavigationDO;
import com.greenstone.mes.system.infrastructure.po.MemberPermissionDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2024-10-21-10:26
 */
@AllArgsConstructor
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final MemberPermissionRepository memberPermissionRepository;
    private final MemberNavigationRepository memberNavigationRepository;
    private final UserRepository userRepository;

    @Override
    public void saveRole(Role role) {
        roleRepository.saveRole(role);
    }

    @Override
    public void updateRole(Role role) {
        roleRepository.updateRole(role);
    }

    @Transactional
    @Override
    public void removeRole(Long roleId, Boolean keepMemberPerm) {
        // 删除角色时，同时删除有该角色的用户。保留成员权限时，把角色的权限给里面的成员；否则直接删除角色相关权限。
        List<RoleUser> roleUsers = roleRepository.selectRoleUsersByRole(roleId);
        if (CollUtil.isNotEmpty(roleUsers)) {
            removePermission(roleId, keepMemberPerm, roleUsers);
            removeNavigation(roleId, keepMemberPerm, roleUsers);
        }
        // 删除角色用户
        roleRepository.removeRoleUserByRoleId(roleId);
        // 删除角色
        roleRepository.removeRole(roleId);
        // 删除用户表的角色关联
        userRepository.removeRole(null, roleId);
    }

    public void removePermission(Long roleId, Boolean keepMemberPerm, List<RoleUser> roleUsers) {
        List<MemberPermission> existMemberPerms = memberPermissionRepository.selectMemberPermissionsByMemberId(roleId);
        if (CollUtil.isNotEmpty(existMemberPerms)) {
            // 保留成员权限
            if (keepMemberPerm) {
                List<MemberPermissionDO> memberPermissions = new ArrayList<>();
                for (MemberPermission existMemberPerm : existMemberPerms) {
                    for (RoleUser roleUser : roleUsers) {
                        memberPermissions.add(MemberPermissionDO.builder().memberId(roleUser.getUserId())
                                .memberType(MemberType.USER.getType()).functionPermissionId(existMemberPerm.getFunctionPermissionId()).build());
                    }
                }
                // 新增用户权限
                memberPermissionRepository.addMemberPermissions(memberPermissions);
            }
            // 删除角色权限
            memberPermissionRepository.deleteMemberPermissionByMemberId(roleId);
        }
    }

    public void removeNavigation(Long roleId, Boolean keepMemberPerm, List<RoleUser> roleUsers) {
        List<MemberNavigationDO> existMemberNavigations = memberNavigationRepository.selectMemberNavigationsByMemberId(roleId);
        if (CollUtil.isNotEmpty(existMemberNavigations)) {
            // 保留成员导航
            if (keepMemberPerm) {
                List<MemberNavigationDO> memberNavigations = new ArrayList<>();
                for (MemberNavigationDO existMemberNavigation : existMemberNavigations) {
                    for (RoleUser roleUser : roleUsers) {
                        memberNavigations.add(MemberNavigationDO.builder().memberId(roleUser.getUserId())
                                .memberType(MemberType.USER.getType()).navigationId(existMemberNavigation.getNavigationId()).build());
                    }
                }
                // 新增用户导航
                memberNavigationRepository.addMemberNavigations(memberNavigations);
            }
            // 删除角色导航
            memberNavigationRepository.deleteByMemberId(roleId);
        }
    }


    @Override
    public List<Role> list(Role role) {
        return roleRepository.list(role);
    }

    @Override
    public Role detail(Long roleId) {
        Role role = roleRepository.exist(roleId);
        List<User> users = allocatedUsers(roleId);
        role.setUsers(users);
        return role;
    }

    @Transactional
    @Override
    public void allocateUsers(RoleAllocateUsersCmd allocateUsersCmd) {
        // 待分配用户的角色关系
        List<RoleUser> roleUsers = roleRepository.selectRoleUsersByUserIds(allocateUsersCmd.getUserIds());
        // 所有角色
        List<Role> roles = roleRepository.listAll();
        // 待分配的角色
        Optional<Role> findAllocatedRole = roles.stream().filter(r -> Objects.equals(r.getRoleId(), allocateUsersCmd.getRoleId())).findFirst();
        if (findAllocatedRole.isEmpty()) {
            throw new ServiceException(StrUtil.format("待分配的角色未找到，roleId:{}", allocateUsersCmd.getRoleId()));
        }
        // 待分配的角色信息
        Role allocatedRole = findAllocatedRole.get();
        List<RoleUser> allocatedRoleUser = new ArrayList<>();
        for (Long userId : allocateUsersCmd.getUserIds()) {
            // 用户存在其他角色
            if (CollUtil.isNotEmpty(roleUsers)) {
                Optional<RoleUser> find = roleUsers.stream().filter(r -> Objects.equals(r.getUserId(), userId)).findFirst();
                if (find.isPresent()) {
                    RoleUser exist = find.get();
                    Optional<Role> find2 = roles.stream().filter(r -> Objects.equals(r.getRoleId(), exist.getRoleId())).findFirst();
                    if (find2.isPresent()) {
                        Role existRole = find2.get();
                        if (Objects.equals(allocatedRole.getRoleId(), existRole.getRoleId())) {
                            throw new ServiceException(StrUtil.format("该用户已分配该角色：{}", existRole.getRoleName()));
                        } else {
                            throw new ServiceException(StrUtil.format("该用户已分配其他角色：{}", existRole.getRoleName()));
                        }
                    }
                }
            }
            // 分配用户
            allocatedRoleUser.add(RoleUser.builder().roleId(allocatedRole.getRoleId()).userId(userId).build());
        }
        roleRepository.addRoleUserBatch(allocatedRoleUser);
        // 更新用户表角色
        for (RoleUser roleUser : allocatedRoleUser) {
            userRepository.changeRole(roleUser.getUserId(), roleUser.getRoleId());
        }
        // 更新角色的用户数量
        roleRepository.updateRoleUserNum(allocatedRole.getRoleId());
        // 如果用户之前有权限，删除个人权限
        for (Long userId : allocateUsersCmd.getUserIds()) {
            memberPermissionRepository.deleteMemberPermissionByMemberId(userId);
            memberNavigationRepository.deleteByMemberId(userId);
        }

    }

    @Transactional
    @Override
    public void changeRoleUser(RoleUserChangeCmd changeCmd) {
        updateRoleUserWithUpdateUserNum(changeCmd);
        // 更新用户表角色
        userRepository.changeRole(changeCmd.getUserId(), changeCmd.getRoleId());
    }

    @Transactional
    @Override
    public void updateRoleUser(RoleUserChangeCmd changeCmd) {
        // 如果是角色权限，则删除个人权限
        if (MemberType.ROLE.getType().equals(changeCmd.getPermissionType())) {
            updateRoleUserWithUpdateUserNum(changeCmd);
            memberPermissionRepository.deleteMemberPermissionByMemberId(changeCmd.getUserId());
            memberNavigationRepository.deleteByMemberId(changeCmd.getUserId());
        }
    }

    public void updateRoleUserWithUpdateUserNum(RoleUserChangeCmd changeCmd) {
        List<RoleUser> roleUsers = roleRepository.selectRoleUsersByUserIds(List.of(changeCmd.getUserId()));
        for (RoleUser roleUser : roleUsers) {
            roleRepository.removeRoleUser(roleUser);
            // 删除用户时，减用户数量
            roleRepository.updateRoleUserNum(roleUser.getRoleId());
        }
        if (changeCmd.getRoleId() != null) {
            Role role = roleRepository.exist(changeCmd.getRoleId());
            roleRepository.addRoleUser(RoleUser.builder().roleId(changeCmd.getRoleId()).userId(changeCmd.getUserId()).build());
            // 新增用户关系时，加用户数量
            roleRepository.updateRoleUserNum(role.getRoleId());
        }
    }

    @Transactional
    @Override
    public void addRoleUser(RoleUserChangeCmd changeCmd) {
        if (changeCmd.getRoleId() != null) {
            Role role = roleRepository.exist(changeCmd.getRoleId());
            roleRepository.addRoleUser(RoleUser.builder().roleId(changeCmd.getRoleId()).userId(changeCmd.getUserId()).build());
            // 新增用户关系时，加用户数量
            roleRepository.updateRoleUserNum(role.getRoleId());
        }
        // 如果是角色权限，则删除个人权限
        if (MemberType.ROLE.getType().equals(changeCmd.getPermissionType())) {
            memberPermissionRepository.deleteMemberPermissionByMemberId(changeCmd.getUserId());
            memberNavigationRepository.deleteByMemberId(changeCmd.getUserId());
        }
    }

    @Transactional
    @Override
    public void removeRoleUser(RoleUserRemoveCmd removeCmd) {
        Role role = roleRepository.exist(removeCmd.getRoleId());
        RoleUser roleUser = roleRepository.selectRoleUser(removeCmd.getRoleId(), removeCmd.getUserId());
        if (roleUser == null) {
            throw new ServiceException(StrUtil.format("用户未拥有该角色：{}", role.getRoleName()));
        }
        roleRepository.removeRoleUser(RoleUser.builder().roleId(removeCmd.getRoleId()).userId(removeCmd.getUserId()).build());
        // 删除用户时，减用户数量
        roleRepository.updateRoleUserNum(role.getRoleId());
        // 更新用户表角色
        userRepository.removeRole(removeCmd.getUserId(), null);
    }

    @Override
    public List<User> unallocatedUsers(User user) {
        return roleRepository.selectUnallocatedUsers(user);
    }

    @Override
    public List<User> allocatedUsers(Long roleId) {
        return roleRepository.selectAllocatedUsers(roleId);
    }

    @Override
    public List<Role> selectUnallocatedPermRoles(Long functionPermissionId) {
        List<Role> roles = roleRepository.listAll();
        // 已分配该权限组的角色
        List<MemberPermissionDO> memberPermissions = memberPermissionRepository.selectMemberPermissionsByFunctionPermId(functionPermissionId);
        Set<Long> roleSet = memberPermissions.stream().filter(m -> m.getMemberType().equals(MemberType.ROLE.getType())).map(MemberPermissionDO::getMemberId).collect(Collectors.toSet());
        // 未分配该权限组角色=所有角色-已分配该权限组的角色
        return roles.stream().filter(r -> !roleSet.contains(r.getRoleId())).toList();
    }
}
