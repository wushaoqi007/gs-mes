package com.greenstone.mes.system.domain.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.greenstone.mes.system.domain.service.*;
import com.greenstone.mes.system.dto.result.UserPermissionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.greenstone.mes.system.api.domain.SysUser;

@RequiredArgsConstructor
@Service
public class SysPermissionServiceImpl implements SysPermissionService {

    private final ISysRoleService roleService;

    private final MenuService menuService;

    private final PermService permService;

    private final UserService userService;

    /**
     * 获取角色数据权限
     *
     * @param userId 用户Id
     * @return 角色权限信息
     */
    @Override
    public Set<String> getRolePermission(Long userId) {
        Set<String> roles = new HashSet<>();
        // 管理员拥有所有权限
        if (SysUser.isAdmin(userId)) {
            roles.add("admin");
        } else {
            roles.add("user");
        }
        return roles;
    }

    /**
     * 获取菜单数据权限
     *
     * @param userId 用户Id
     * @return 菜单权限信息
     */
    @Override
    public List<UserPermissionResult> getUserPermissions(Long userId) {
        return userService.selectUserPermissions(userId);
    }
}
