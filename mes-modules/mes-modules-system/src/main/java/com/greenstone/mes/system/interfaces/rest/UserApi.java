package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.system.application.dto.cmd.RoleUserChangeCmd;
import com.greenstone.mes.system.application.dto.cmd.RoleUserRemoveCmd;
import com.greenstone.mes.system.application.service.RoleService;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.domain.service.SysPermissionService;
import com.greenstone.mes.system.domain.service.UserService;
import com.greenstone.mes.system.dto.result.UserPermissionResult;
import com.greenstone.mes.system.interfaces.rest.cmd.UserAddCmd;
import com.greenstone.mes.system.interfaces.rest.cmd.UserEditCmd;
import com.greenstone.mes.system.interfaces.rest.cmd.UserResetPassword;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

/**
 * 用户信息
 *
 * @author gruenkai
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserApi extends BaseController {

    private final UserService userService;
    private final RoleService roleService;
    private final SysPermissionService permissionService;

    @GetMapping("/byLoginUsername")
    public AjaxResult getUserLoginAuth(@RequestParam("loginUsername") String loginUsername) {
        return AjaxResult.success(userService.getUserByLoginUsername(loginUsername));
    }

    @GetMapping("/byWx")
    public User getUserByWx(@RequestParam("wxCpId") String wxCpId, @RequestParam("wxUserId") String wxUserId) {
        return userService.getUserByWx(wxCpId, wxUserId);
    }

    @PostMapping
    public void createUser(@RequestBody @Validated UserAddCmd userAdd) {
        userAdd.setPassword(SecurityUtils.encryptPassword(userAdd.getPassword()));
        userService.createUser(userAdd);
    }

    @PutMapping("/{userId}")
    public void updateUser(@RequestBody @Validated UserEditCmd editCmd) {
        userService.updateUser(editCmd);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable("userId") Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/getByMail/{mail}")
    public User getUserByMail(@PathVariable("mail") @NotBlank(message = "请输入邮箱") String mail) {
        return userService.getUserByMail(mail);
    }

    @GetMapping
    public TableDataInfo getUsers(User user) {
        startPage();
        return getDataTable(userService.getUsers(user));
    }

    @GetMapping("/byWxCp")
    public List<User> getByWxCp() {
        return userService.getByWxCp();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
    }

    @PutMapping("/{userId}/resetPassword")
    public void resetPassword(@RequestBody UserResetPassword resetPassword) {
        resetPassword.setPassword(SecurityUtils.encryptPassword(resetPassword.getPassword()));
        userService.resetPassword(resetPassword);
    }

    @ApiLog
    @PutMapping(value = "/{userId}/changeRole")
    public AjaxResult changeRole(@PathVariable("userId") @NotBlank(message = "请指定需要调整角色的用户") Long userId, @Validated @RequestBody RoleUserChangeCmd changeCmd) {
        changeCmd.setUserId(userId);
        roleService.changeRoleUser(changeCmd);
        return AjaxResult.success("完成用户的角色调整");
    }

    @ApiLog
    @PutMapping(value = "/{userId}/removeRole")
    public AjaxResult removeRoleUser(@PathVariable("userId") @NotBlank(message = "请指定取消角色授权的用户") Long userId, @Validated @RequestBody RoleUserRemoveCmd removeCmd) {
        removeCmd.setUserId(userId);
        roleService.removeRoleUser(removeCmd);
        return AjaxResult.success("已取消用户的角色授权");
    }

    @GetMapping(value = "/byUnallocatedPermission/{functionPermissionId}")
    public AjaxResult unallocatedPermUsers(@PathVariable("functionPermissionId") Long functionPermissionId) {
        return AjaxResult.success(userService.selectUnallocatedPermUsers(functionPermissionId));
    }

    @GetMapping(value = "/{userId}/permissions")
    public AjaxResult getUserPermissions(@PathVariable("userId") @NotBlank(message = "请指定需要查询的用户") Long userId) {
        return AjaxResult.success(userService.selectUserPermissions(userId));
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getInfo")
    public AjaxResult getInfo() {
        Long userId = SecurityUtils.getUserId();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(userId);
        // 权限集合
        List<UserPermissionResult> userPermissions = permissionService.getUserPermissions(userId);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", userService.getUserById(userId));
        ajax.put("roles", roles);
        ajax.put("permissions", userPermissions);
        return ajax;
    }

    @GetMapping(value = "/{userId}/navigations")
    public AjaxResult getUserNavigations(@PathVariable("userId") @NotBlank(message = "请指定需要查询的用户") Long userId) {
        return AjaxResult.success(userService.selectUserNavigations(userId));
    }

    @GetMapping(value = "/{userId}/functions")
    public AjaxResult getUserFunctions(@PathVariable("userId") @NotBlank(message = "请指定需要查询的用户") Long userId) {
        return AjaxResult.success(userService.selectUserFunctions(userId));
    }

    @PostMapping("/syncWxEmployeeNo")
    public AjaxResult syncWxEmployeeNo() {
        userService.syncWxEmployeeNo();
        return AjaxResult.success();
    }
}
