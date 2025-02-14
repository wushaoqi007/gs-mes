package com.greenstone.mes.system.domain.service;

import com.greenstone.mes.system.dto.result.UserPermissionResult;

import java.util.List;
import java.util.Set;

public interface SysPermissionService {
    /**
     * 获取角色数据权限
     *
     * @param userId 用户Id
     * @return 角色权限信息
     */
    Set<String> getRolePermission(Long userId);

    /**
     * 获取菜单数据权限
     *
     * @param userId 用户Id
     * @return 菜单权限信息
     */
    List<UserPermissionResult> getUserPermissions(Long userId);
}
