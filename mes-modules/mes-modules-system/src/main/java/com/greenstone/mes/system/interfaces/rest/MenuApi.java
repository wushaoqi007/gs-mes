package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.common.security.annotation.RequiresRoles;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.system.application.dto.cmd.MenuMoveCmd;
import com.greenstone.mes.system.application.dto.result.MenuTree;
import com.greenstone.mes.system.domain.SysMenu;
import com.greenstone.mes.system.domain.service.MenuService;
import com.greenstone.mes.system.dto.cmd.MenuAddCmd;
import com.greenstone.mes.system.dto.cmd.MenuEditCmd;
import com.greenstone.mes.system.infrastructure.po.MenuPo;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单信息
 *
 * @author ruoyi
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/menu")
public class MenuApi extends BaseController {

    private final MenuService menuService;

    @RequiresRoles("admin")
    @PostMapping("/resetSort")
    public AjaxResult resetSort() {
        menuService.resetMenuOrder();
        return AjaxResult.success("重置排序完成");
    }

    @RequiresRoles("admin")
    @PutMapping("/move")
    @ApiLog
    @Transactional
    public AjaxResult sort(@RequestBody MenuMoveCmd sortCmd) {
        menuService.moveMenu(sortCmd);
        return AjaxResult.success("排序完成");
    }

    /**
     * 获取菜单列表
     */
    @GetMapping("/list")
    public AjaxResult list(SysMenu menu) {
        Long userId = SecurityUtils.getUserId();
        List<MenuPo> menus = menuService.selectMenuList(menu, userId);
        return AjaxResult.success(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @GetMapping(value = "/{menuId}")
    public AjaxResult getBriefMenuInfo(@PathVariable String menuId) {
        return AjaxResult.success(menuService.selectMenuById(menuId));
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @GetMapping(value = "/{menuId}/form")
    public AjaxResult getFormInfo(@PathVariable String menuId) {
        return AjaxResult.success(menuService.selectFormById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect(SysMenu menu) {
        Long userId = SecurityUtils.getUserId();
        List<MenuPo> menus = menuService.selectMenuList(menu, userId);
        return AjaxResult.success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/tree")
    public AjaxResult tree(SysMenu menu) {
        Long userId = SecurityUtils.getUserId();
        List<MenuPo> menus = menuService.selectMenuList(menu, userId);
        return AjaxResult.success(menuService.buildMenuTree(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public AjaxResult roleMenuTreeselect(@PathVariable("roleId") Long roleId) {
        AjaxResult ajax = AjaxResult.success();
        Long userId = SecurityUtils.getUserId();
        List<MenuPo> menus = menuService.selectMenuList(userId);
        ajax.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        ajax.put("menus", menuService.buildMenuTreeSelect(menus));
        return ajax;
    }

    /**
     * 新增菜单
     */
    @Transactional
    @RequiresRoles("admin")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody MenuAddCmd menuAddCmd) {
        if (menuAddCmd.getFrame() != null && menuAddCmd.getFrame() && !StringUtils.ishttp(menuAddCmd.getPath())) {
            return AjaxResult.error("新增菜单'" + menuAddCmd.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        return toAjax(menuService.addMenu(menuAddCmd));
    }

    /**
     * 修改菜单
     */
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody MenuEditCmd menuEditCmd) {
        if (menuEditCmd.getFrame() != null && menuEditCmd.getFrame() && !StringUtils.ishttp(menuEditCmd.getPath())) {
            return AjaxResult.error("修改菜单'" + menuEditCmd.getMenuName() + "'失败，地址必须以http(s)://开头");
        } else if (menuEditCmd.getMenuId().equals(menuEditCmd.getParentId())) {
            return AjaxResult.error("修改菜单'" + menuEditCmd.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        return toAjax(menuService.updateMenu(menuEditCmd));
    }

    /**
     * 删除菜单
     */
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public AjaxResult remove(@PathVariable("menuId") String menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return AjaxResult.error("存在子菜单,不允许删除");
        }
        menuService.deleteRoleMenuByMenuId(menuId);
        return toAjax(menuService.deleteMenuById(menuId));
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<MenuTree> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menus);
    }
}