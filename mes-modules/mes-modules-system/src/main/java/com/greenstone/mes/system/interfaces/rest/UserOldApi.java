package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.constant.UserConstants;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.system.api.domain.SysRole;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.api.model.LoginUser;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.domain.service.*;
import com.greenstone.mes.system.dto.cmd.UserWxSyncCmd;
import com.greenstone.mes.system.dto.query.UserQuery;
import com.greenstone.mes.system.dto.result.UserPermissionResult;
import com.greenstone.mes.system.dto.result.UserResult;
import com.greenstone.mes.system.infrastructure.constant.WorkwxConst;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/user")
public class UserOldApi extends BaseController {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private UserService userService;

    /**
     * 获取用户信息
     */
    @GetMapping
    public TableDataInfo getCropUsers(UserQuery query) {
        User sysUser = SecurityUtils.getLoginUser().getUser();
        if (!sysUser.isAdmin()) {
            query.setWxCpId(sysUser.getWxUserId());
        }
        startPage();
        List<UserResult> userResults = userService.queryUserBriefInfos(query);
        return getDataTable(userResults);
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysUser user) {
        startPage();
        List<SysUser> list = sysUserService.selectUserList(user);
        return getDataTable(list);
    }

    @GetMapping("/withoutEmpNo")
    public AjaxResult withoutEmpNo() {
        List<SysUser> list = userService.getUsersWithoutEmpNo();
        return AjaxResult.success(list);
    }

    /**
     * 获取格林系统的用户简要信息
     */
    @PostMapping("/brief/corp/greenstone")
    public TableDataInfo gsUserBriefs(@RequestBody(required = false) UserQuery query) {
        query.setWxCpId(WorkwxConst.CpId.GREENSTONE);
        startPage();
        List<UserResult> userResults = userService.queryUserBriefInfos(query);
        return getDataTable(userResults);
    }

    /**
     * 获取用户简要信息
     */
    @PostMapping("/brief")
    public TableDataInfo userBriefs(@RequestBody(required = false) UserQuery query) {
        startPage();
        List<UserResult> userResults = userService.queryUserBriefInfos(query);
        return getDataTable(userResults);
    }

    /**
     * 获取用户企业微信信息
     */
    @PostMapping("/workwx")
    public TableDataInfo userWorkwxInfos(@RequestBody UserQuery query) {
        startPage();
        List<UserResult> userResults = userService.queryUserWorkwxInfos(query);
        return getDataTable(userResults);
    }

    /**
     * 同步用户的企业微信信息
     */
    @PostMapping("/workwx/sync")
    public AjaxResult workwxSync(@RequestBody UserWxSyncCmd syncCmd) {
        userService.syncWorkwxUser(syncCmd);
        return AjaxResult.success();
    }

    /**
     * 获取所有用户
     */
    @GetMapping("/listAll")
    public AjaxResult listAll(SysUser user) {
        List<SysUser> list = sysUserService.allUsers(user);
        return AjaxResult.success(list);
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, @RequestBody SysUser user) {
        List<SysUser> list = sysUserService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = SecurityUtils.getUsername();
        String message = sysUserService.importUser(userList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info/{username}")
    public R<LoginUser> info(@PathVariable("username") String username) {
        User sysUser = userService.getUserByUsername(username);
        if (StringUtils.isNull(sysUser)) {
            return R.fail("用户名或密码错误");
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser.getUserId());
        // 权限集合
        List<UserPermissionResult> userPermissions = permissionService.getUserPermissions(sysUser.getUserId());
        LoginUser sysUserVo = new LoginUser();
        sysUserVo.setUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(new HashSet<>(userPermissions));
        return R.ok(sysUserVo);
    }

    @GetMapping("/basicInfo/{username}")
    public R<SysUser> basicInfo(@PathVariable("username") String username) {
        SysUser sysUser = sysUserService.selectUserByUserName(username);
        return R.ok(sysUser);
    }

    /**
     * 注册用户信息
     */
    @PostMapping("/register")
    public R<Boolean> register(@RequestBody SysUser sysUser) {
        String username = sysUser.getUserName();
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser")))) {
            return R.fail("当前系统没有开启注册功能！");
        }
        if (UserConstants.NOT_UNIQUE.equals(sysUserService.checkUserNameUnique(username))) {
            return R.fail("保存用户'" + username + "'失败，注册账号已存在");
        }
        return R.ok(sysUserService.registerUser(sysUser));
    }

    /**
     * 根据用户编号获取详细信息
     */
    @GetMapping(value = {"/", "/{userId}"})
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        sysUserService.checkUserDataScope(userId);
        AjaxResult ajax = AjaxResult.success();
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        ajax.put("posts", postService.selectPostAll());
        if (StringUtils.isNotNull(userId)) {
            SysUser sysUser = sysUserService.selectUserById(userId);
            ajax.put(AjaxResult.DATA_TAG, sysUser);
            ajax.put("postIds", postService.selectPostListByUserId(userId));
            ajax.put("roleIds", sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user) {
        if (UserConstants.NOT_UNIQUE.equals(sysUserService.checkUserNameUnique(user.getUserName()))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(sysUserService.checkPhoneUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(sysUserService.checkEmailUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(sysUserService.createUser(user));
    }

    /**
     * 修改用户
     */
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user) {
        sysUserService.checkUserAllowed(user);
//        if (StringUtils.isNotEmpty(user.getPhonenumber())
//                && UserConstants.NOT_UNIQUE.equals(iSysUserService.checkPhoneUnique(user))) {
//            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
//        } else if (StringUtils.isNotEmpty(user.getEmail())
//                && UserConstants.NOT_UNIQUE.equals(iSysUserService.checkEmailUnique(user))) {
//            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
//        }
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(sysUserService.updateUser(user));
    }

    /**
     * 修改用户基本信息
     */
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/basics")
    public AjaxResult basicsEdit(@Validated @RequestBody SysUser user) {
        return toAjax(sysUserService.updateUserProfile(user));
    }

    /**
     * 删除用户
     */
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        if (ArrayUtils.contains(userIds, SecurityUtils.getUserId())) {
            return AjaxResult.error("当前用户不能删除");
        }
        return toAjax(sysUserService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user) {
        sysUserService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(sysUserService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user) {
        sysUserService.checkUserAllowed(user);
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(sysUserService.updateUserStatus(user));
    }


    /**
     * 根据用户编号获取授权角色
     */
    @GetMapping("/authRole/{userId}")
    public AjaxResult authRole(@PathVariable("userId") Long userId) {
        AjaxResult ajax = AjaxResult.success();
        SysUser user = sysUserService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        ajax.put("user", user);
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * 用户授权角色
     */
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds) {
        sysUserService.insertUserAuth(userId, roleIds);
        return success();
    }

    /**
     * 根据用户名获取用户昵称
     */
    @GetMapping("/userInfo/query")
    public AjaxResult getNickNameByUserName(SysUser user) {
        return AjaxResult.success(sysUserService.getNickNameByUserName(user.getUserName()));
    }

    /**
     * 根据用户ID获取用户昵称
     */
    @GetMapping("/info/name/{userId}")
    public AjaxResult getNickNameByUserId(@PathVariable(value = "userId") Long userId) {
        SysUser sysUser = sysUserService.selectUserById(userId);
        if (StringUtils.isNull(sysUser)) {
            return AjaxResult.error("未知用户");
        }
        return AjaxResult.success("操作成功", sysUser.getNickName());
    }

    @GetMapping("/info/public/{userId}")
    public AjaxResult getUserPublicInfo(@PathVariable(value = "userId") Long userId) {
        SysUser sysUser = sysUserService.selectUserById(userId);
        if (StringUtils.isNull(sysUser)) {
            return AjaxResult.error("未知用户");
        }
        SysUser user = new SysUser();
        user.setUserId(sysUser.getUserId());
        user.setUserName(sysUser.getUserName());
        user.setNickName(sysUser.getNickName());
        user.setEmail(sysUser.getEmail());
        user.setDeptId(sysUser.getDeptId());
        return AjaxResult.success("操作成功", user);
    }

    @GetMapping("/userinfo/{userId}")
    public AjaxResult info(@PathVariable("userId") Long userId) {
        SysUser user = sysUserService.selectUserById(userId);
        return AjaxResult.success(user);
    }

    @GetMapping("/info/detail/{wxUserId}")
    public AjaxResult getUserInfoByWxUserId(@PathVariable("wxUserId") String wxUserId) {
        SysUser user = sysUserService.selectUserByWxUserId(wxUserId);
        return AjaxResult.success(user);
    }

    @GetMapping("/nickname")
    public TableDataInfo userNameInfo() {
        List<SysUser> list = userService.list();
        list = list.stream().filter(u -> !"admin".equals(u.getNickName())).toList();
        return getDataTable(list);
    }

    @PostMapping("/innerUser")
    public AjaxResult getSysUser(@RequestBody SysUser sysUser) {
        SysUser user = sysUserService.getSysUser(sysUser);
        return AjaxResult.success(user);
    }

    @PostMapping("/innerUsers")
    public AjaxResult getSysUsers(@RequestBody SysUser sysUser) {
        return AjaxResult.success(sysUserService.getSysUsers(sysUser));
    }

}