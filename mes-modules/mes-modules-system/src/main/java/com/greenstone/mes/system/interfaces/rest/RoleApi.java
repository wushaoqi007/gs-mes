package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.system.application.dto.cmd.RoleAllocateUsersCmd;
import com.greenstone.mes.system.application.dto.cmd.RoleRemoveCmd;
import com.greenstone.mes.system.application.service.RoleService;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.domain.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-21-10:37
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/roles")
public class RoleApi extends BaseController {
    private final RoleService roleService;

    @ApiLog
    @PostMapping
    public AjaxResult add(@Validated @RequestBody Role role) {
        roleService.saveRole(role);
        return AjaxResult.success("新增成功");
    }

    @ApiLog
    @PutMapping(value = "/{roleId}")
    public AjaxResult edit(@PathVariable("roleId") @NotBlank(message = "请指定需要编辑的角色") Long roleId, @Validated @RequestBody Role role) {
        role.setRoleId(roleId);
        roleService.updateRole(role);
        return AjaxResult.success("更新成功");
    }

    @DeleteMapping("/{roleId}")
    public AjaxResult remove(@PathVariable("roleId") @NotBlank(message = "请指定需要删除的角色") Long roleId, @Validated @RequestBody RoleRemoveCmd removeCmd) {
        roleService.removeRole(roleId, removeCmd.getKeepMemberPerm());
        return AjaxResult.success("删除成功");
    }

    @GetMapping
    public AjaxResult list(Role role) {
        List<Role> results = roleService.list(role);
        return AjaxResult.success(results);
    }

    @GetMapping(value = "/{roleId}")
    public AjaxResult detail(@PathVariable("roleId") @NotBlank(message = "请指定需要查询的角色") Long roleId) {
        return AjaxResult.success(roleService.detail(roleId));
    }

    @ApiLog
    @PostMapping(value = "/{roleId}/allocateUsers")
    public AjaxResult allocateUsers(@PathVariable("roleId") @NotBlank(message = "请指定分配用户的角色") Long roleId, @Validated @RequestBody RoleAllocateUsersCmd allocateUsersCmd) {
        allocateUsersCmd.setRoleId(roleId);
        roleService.allocateUsers(allocateUsersCmd);
        return AjaxResult.success("完成角色分配");
    }

    @GetMapping(value = "/unallocated/users")
    public AjaxResult unallocatedUsers(User user) {
        return AjaxResult.success(roleService.unallocatedUsers(user));
    }

    @GetMapping(value = "/{roleId}/users")
    public AjaxResult allocatedUsers(@PathVariable("roleId") @NotBlank(message = "请指定需要查询的角色") Long roleId) {
        return AjaxResult.success(roleService.allocatedUsers(roleId));
    }

    @GetMapping(value = "/byUnallocatedPermission/{functionPermissionId}")
    public AjaxResult unallocatedPermRoles(@PathVariable("functionPermissionId") Long functionPermissionId) {
        return AjaxResult.success(roleService.selectUnallocatedPermRoles(functionPermissionId));
    }

}
