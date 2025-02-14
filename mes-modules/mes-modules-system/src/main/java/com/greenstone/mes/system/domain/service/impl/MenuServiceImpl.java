package com.greenstone.mes.system.domain.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.constant.UserConstants;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.core.utils.TreeUtils;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.system.api.domain.SysRole;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.application.dto.cmd.MenuMoveCmd;
import com.greenstone.mes.system.application.dto.result.MenuTree;
import com.greenstone.mes.system.application.service.FunctionService;
import com.greenstone.mes.system.consts.SysConst;
import com.greenstone.mes.system.domain.Permission;
import com.greenstone.mes.system.domain.SysMenu;
import com.greenstone.mes.system.domain.converter.SysConverter;
import com.greenstone.mes.system.domain.entity.Function;
import com.greenstone.mes.system.domain.service.MenuService;
import com.greenstone.mes.system.domain.service.UserService;
import com.greenstone.mes.system.domain.vo.TreeSelect;
import com.greenstone.mes.system.dto.cmd.CustomFormMenuAddCmd;
import com.greenstone.mes.system.dto.cmd.CustomFormMenuEditCmd;
import com.greenstone.mes.system.dto.cmd.MenuAddCmd;
import com.greenstone.mes.system.dto.cmd.MenuEditCmd;
import com.greenstone.mes.system.dto.result.FormDefinitionVo;
import com.greenstone.mes.system.dto.result.MemberNavigationResult;
import com.greenstone.mes.system.dto.result.TableFieldVo;
import com.greenstone.mes.system.dto.result.UserPermissionResult;
import com.greenstone.mes.system.infrastructure.mapper.MenuMapper;
import com.greenstone.mes.system.infrastructure.mapper.SysMenuMapper;
import com.greenstone.mes.system.infrastructure.mapper.SysRoleMapper;
import com.greenstone.mes.system.infrastructure.mapper.SysRoleMenuMapper;
import com.greenstone.mes.system.infrastructure.po.MenuPo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单 业务层处理
 *
 * @author gu_renkai
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MenuServiceImpl implements MenuService {

    private final SysMenuMapper sysMenuMapper;

    private final SysRoleMapper roleMapper;

    private final SysRoleMenuMapper roleMenuMapper;

    private final MenuMapper menuMapper;

    private final SysConverter sysConverter;

    private final FunctionService functionService;

    private final UserService userService;

    /**
     * 重置菜单的排序，按照原来的排序重置为 0 开始的排序
     */
    @Override
    public void resetMenuOrder() {
        log.info("SysMenuServiceImpl.resetMenuOrder: start");
        resetMenuOrder("0", true);
        log.info("SysMenuServiceImpl.resetMenuOrder: end");
    }

    @Override
    public void moveMenu(MenuMoveCmd moveCmd) {
        log.info("SysMenuServiceImpl.setMenuOrder: start");
        if (StrUtil.isBlank(moveCmd.getParentMenuId())) {
            moveCmd.setParentMenuId(SysConst.MENU_ROOT_ID);
        }
        // 查询字段：菜单id，排序，查询条件：菜单父ID
        LambdaQueryWrapper<MenuPo> queryWrapper =
                Wrappers.lambdaQuery(MenuPo.class).select(MenuPo::getMenuId, MenuPo::getMenuName, MenuPo::getOrderNum).eq(MenuPo::getParentId,
                        moveCmd.getParentMenuId());
        List<MenuPo> childMenus = menuMapper.selectList(queryWrapper);
        List<MenuPo> sortedMenus = childMenus.stream().filter(menu -> !menu.getMenuId().equals(moveCmd.getMenuId())).sorted(Comparator.comparing(MenuPo::getOrderNum)).toList();
        for (int i = 0; i < sortedMenus.size(); i++) {
            MenuPo menuPo = sortedMenus.get(i);
            int order = moveCmd.getOrderNum() > i ? i : i + 1;
            if (order != menuPo.getOrderNum()) {
                // 在这之后的菜单排序需要 +1
                LambdaUpdateWrapper<MenuPo> updateWrapper = Wrappers.lambdaUpdate(MenuPo.class).eq(MenuPo::getMenuId, menuPo.getMenuId()).set(MenuPo::getOrderNum, order);
                menuMapper.update(updateWrapper);
                log.debug("menu {} {} update with order num {}.", menuPo.getMenuId(), menuPo.getMenuName(), order);
            } else {
                log.debug("menu {} {} no need update", menuPo.getMenuId(), menuPo.getMenuName());
            }
        }
        menuMapper.updateById(MenuPo.builder().menuId(moveCmd.getMenuId()).parentId(moveCmd.getParentMenuId()).orderNum(moveCmd.getOrderNum()).build());
        log.info("SysMenuServiceImpl.setMenuOrder: end");
    }


    private void resetMenuOrder(String parentId, boolean reorderChild) {
        LambdaQueryWrapper<MenuPo> queryWrapper = Wrappers.lambdaQuery(MenuPo.class).select(MenuPo::getMenuId).select(MenuPo::getOrderNum).eq(MenuPo::getParentId, parentId);
        List<MenuPo> childMenus = menuMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(childMenus)) {
            List<MenuPo> sortedMenus = childMenus.stream().sorted(Comparator.comparing(MenuPo::getOrderNum)).toList();
            for (int i = 0; i < sortedMenus.size(); i++) {
                MenuPo menuPo = sortedMenus.get(i);
                if (menuPo.getOrderNum() != i) {
                    LambdaUpdateWrapper<MenuPo> updateWrapper = Wrappers.lambdaUpdate(MenuPo.class).eq(MenuPo::getMenuId, menuPo.getMenuId()).set(MenuPo::getOrderNum, i);
                    menuMapper.update(updateWrapper);
                    log.debug("menu {} {} update with order num {}.", menuPo.getMenuId(), menuPo.getMenuName(), i);
                } else {
                    log.debug("menu {} {} no need update", menuPo.getMenuId(), menuPo.getMenuName());
                }
            }
            if (reorderChild) {
                for (MenuPo sortedMenu : sortedMenus) {
                    resetMenuOrder(sortedMenu.getMenuId(), reorderChild);
                }
            }
        }
    }

    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<MenuPo> selectMenuList(Long userId) {
        return selectMenuList(new SysMenu(), userId);
    }

    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    @Override
    public List<MenuPo> selectMenuList(SysMenu menu, Long userId) {
        List<MenuPo> menuList = null;
        // 管理员显示所有菜单信息
        if (SysUser.isAdmin(userId)) {
            menuList = sysMenuMapper.selectMenuList(menu);
        } else {
            menu.getParams().put("userId", userId);
            menuList = sysMenuMapper.selectMenuListByUserId(menu);
        }
        return menuList;
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        List<String> perms = sysMenuMapper.selectMenuPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户名称
     * @return 菜单列表
     */
    @Override
    public List<MenuTree> selectMenuTreeByUserId(Long userId) {
        List<MenuTree> menuTrees = new ArrayList<>();
        List<MemberNavigationResult> navigations = userService.selectUserNavigations(userId);
        navigations = navigations.stream().filter(MemberNavigationResult::getActive).toList();

        List<Function> functions;
        if (SecurityUtils.getLoginUser().isAdmin()) {
            functions = functionService.getFunctions();
            for (Function function : functions) {
                function.setPermission(Permission.builder().rights(List.of("manage")).build());
            }
        } else {
            List<UserPermissionResult> permissionResults = userService.selectUserPermissions(userId);
            functions = permissionResults.stream().map(this::toFunction).toList();
        }
        for (MemberNavigationResult navigation : navigations) {
            Function function =
                    functions.stream().filter(f -> f.getId().equals(navigation.getFunctionId())).findFirst().orElse(null);
            menuTrees.add(buildMenuTree(navigation, function));
        }

        return TreeUtils.toTree(menuTrees, 0L, Comparator.comparingInt(MenuTree::getOrderNum));
    }

    private MenuTree buildMenuTree(MemberNavigationResult navigation, Function function) {
        MenuTree menuTree = new MenuTree();
        menuTree.setMenuId(navigation.getNavigationId());
        menuTree.setParentId(navigation.getParentId());
        menuTree.setMenuName(navigation.getNavigationName());
        menuTree.setCategory(navigation.getCategory());
        menuTree.setMenuType(navigation.getNavigationType());
        menuTree.setActive(navigation.getActive());
        menuTree.setVisible(navigation.getVisible());
        menuTree.setShowNavigation(navigation.getShowNavigation());
        menuTree.setCacheable(navigation.getCacheable());
        menuTree.setOpenInNewtab(navigation.getOpenInNewtab());
        menuTree.setIcon(navigation.getIcon());
        menuTree.setLink(navigation.getLink());
        menuTree.setQueryParam(navigation.getQueryParam());
        menuTree.setOrderNum(navigation.getOrderNum());
        switch (navigation.getCategory()) {
            case "module" -> {
                menuTree.setComponent("layout");
                menuTree.setPath("");
            }
            case "category", "group" -> {
                menuTree.setComponent("");
                menuTree.setPath("");
            }
            case "navigation" -> {
                if (function == null) {
                    if ("function".equals(navigation.getNavigationType())) {
                        throw new ServiceException("系统错误，导航缺失对应的功能：" + navigation.getNavigationName());
                    }
                    menuTree.setPath(navigation.getLink());
                } else {
                    menuTree.setComponent(function.getComponent());
                    if ("table".equals(function.getType())) {
                        menuTree.setPath(StrUtil.format("/navigations/{}/tables/{}", navigation.getNavigationId(), navigation.getFunctionId()));
                    } else if ("page".equals(function.getType())) {
                        menuTree.setPath(StrUtil.format("/navigations/{}/pages/{}", navigation.getNavigationId(), navigation.getFunctionId()));
                    }
                }
            }
        }
        menuTree.setFunction(function);
        return menuTree;
    }

    private Function toFunction(UserPermissionResult permission) {
        Function function = new Function();
        function.setId(permission.getFunctionId());
        function.setName(permission.getFunctionName());
        function.setType(permission.getFunctionType());
        function.setComponent(permission.getComponent());
        function.setSource(permission.getSource());
        function.setTemplateId(permission.getTemplateId());
        function.setOrderNum(permission.getOrderNum());
        function.setUsingProcess(permission.getUsingProcess());
        function.setFormComponent(permission.getFormComponent());

        function.setPermission(permission.getPermission());
        return function;
    }

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<String> selectMenuListByRoleId(Long roleId) {
        SysRole role = roleMapper.selectRoleById(roleId);
        return sysMenuMapper.selectMenuListByRoleId(roleId, role.isMenuCheckStrictly());
    }

    /**
     * 构建前端所需要下拉树结构
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<MenuPo> menus) {
        List<MenuTree> menuTreeList = sysConverter.toMenuTrees(menus);
        List<MenuTree> menuTrees = TreeUtils.toTree(menuTreeList, "0");
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    @Override
    public List<MenuTree> buildMenuTree(List<MenuPo> menus) {
        List<MenuTree> menuTreeList = sysConverter.toMenuTrees(menus);
        return TreeUtils.toTree(menuTreeList, "0");
    }

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @Override
    public MenuPo selectMenuById(String menuId) {
        LambdaQueryWrapper<MenuPo> queryWrapper = Wrappers.lambdaQuery(MenuPo.class)
                .select(MenuPo::getMenuId, MenuPo::getMenuName, MenuPo::getMenuType, MenuPo::getPath, MenuPo::getComponent, MenuPo::getIcon,
                        MenuPo::getActivable, MenuPo::getVisible, MenuPo::getCacheable, MenuPo::getFrame, MenuPo::getUsingProcess)
                .eq(MenuPo::getMenuId, menuId);
        return menuMapper.selectOne(queryWrapper);
    }

    @Override
    public MenuPo selectFormById(String menuId) {
        LambdaQueryWrapper<MenuPo> queryWrapper =
                Wrappers.lambdaQuery(MenuPo.class)
                        .select(MenuPo::getMenuId, MenuPo::getMenuName, MenuPo::getMenuType, MenuPo::getPath, MenuPo::getComponent, MenuPo::getIcon,
                                MenuPo::getUsingProcess, MenuPo::getDefaultJson, MenuPo::getCustomJson)
                        .eq(MenuPo::getMenuId, menuId);
        return menuMapper.selectOne(queryWrapper);
    }

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean hasChildByMenuId(String menuId) {
        int result = sysMenuMapper.hasChildByMenuId(menuId);
        return result > 0;
    }

    /**
     * 查询菜单使用数量
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean checkMenuExistRole(String menuId) {
        int result = roleMenuMapper.checkMenuExistRole(menuId);
        return result > 0;
    }

    @Override
    public void deleteRoleMenuByMenuId(String menuId) {
        roleMenuMapper.deleteRoleMenuByMenuId(menuId);
    }

    @Override
    public int addMenu(MenuAddCmd addCmd) {
        MenuPo menuPo = sysConverter.toMenuPo(addCmd);
        Long existNumber = menuMapper.selectCount(MenuPo.builder().parentId(addCmd.getParentId()).build());
        menuPo.setOrderNum(existNumber.intValue());
        return menuMapper.insert(menuPo);
    }

    /**
     * 修改保存菜单信息
     *
     * @param editCmd 菜单信息
     * @return 结果
     */
    @Override
    public int updateMenu(MenuEditCmd editCmd) {
        MenuPo menuPo = sysConverter.toMenuPo(editCmd);
        return menuMapper.updateById(menuPo);
    }

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public int deleteMenuById(String menuId) {
        return menuMapper.deleteById(menuId);
    }

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public String checkMenuNameUnique(SysMenu menu) {
        String menuId = StringUtils.isNull(menu.getMenuId()) ? "-1" : menu.getMenuId();
        SysMenu info = sysMenuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId());
        if (StringUtils.isNotNull(info) && !info.getMenuId().equals(menuId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public void addCustomMenu(CustomFormMenuAddCmd addCmd) {
        MenuAddCmd menuAddCmd = MenuAddCmd.builder()
                .menuId(addCmd.getMenuId())
                .menuName(addCmd.getMenuName())
                .menuType(SysConst.MenuType.SYS_EDITABLE_MENU)
                .parentId(addCmd.getParentId())
                .path("/customForm/" + addCmd.getMenuId())
                .component("customerForm/index")
                .icon(addCmd.getIcon())
                .dataTableName(addCmd.getDataTableName())
                .frame(false)
                .activable(true)
                .visible(true)
                .cacheable(true)
                .usingProcess(addCmd.getUsingProcess()).build();
        this.addMenu(menuAddCmd);
        updateTableFields(addCmd.getMenuId());
    }

    @Override
    public void editCustomMenu(CustomFormMenuEditCmd editCmd) {
        MenuPo menuUpdate = MenuPo.builder().menuId(editCmd.getMenuId()).menuName(editCmd.getMenuName())
                .icon(editCmd.getIcon()).customJson(editCmd.getCustomJson()).usingProcess(editCmd.getUsingProcess()).build();
        menuMapper.updateById(menuUpdate);
    }

    @Override
    public FormDefinitionVo getFormDefinition(String menuId) {
        LambdaQueryWrapper<MenuPo> queryWrapper = Wrappers.lambdaQuery(MenuPo.class).select(MenuPo::getMenuId, MenuPo::getMenuName, MenuPo::getIcon, MenuPo::getFieldsJson,
                        MenuPo::getDefaultJson, MenuPo::getCustomJson, MenuPo::getUsingProcess, MenuPo::getProcessDefinitionId)
                .eq(MenuPo::getMenuId, menuId);
        MenuPo formDefinition = menuMapper.selectOne(queryWrapper);
        if (formDefinition == null) {
            throw new RuntimeException("系统内部错误：表单信息缺失");
        }
        return sysConverter.toFormDefinitionVo(formDefinition);
    }


    /**
     * 更新表单的表格字段属性，供表格展示时选择
     *
     * @param menuId 菜单id
     */
    private void updateTableFields(String menuId) {
        LambdaQueryWrapper<MenuPo> queryWrapper = Wrappers.lambdaQuery(MenuPo.class).select(MenuPo::getMenuType,
                MenuPo::getDefaultJson, MenuPo::getCustomJson).eq(MenuPo::getMenuId, menuId);
        MenuPo menuPo = menuMapper.selectOne(queryWrapper);
        if (menuPo != null) {
            List<TableFieldVo> tableFields = new ArrayList<>();
            if (SysConst.MenuType.SYS_EDITABLE_MENU == menuPo.getMenuType()) {
                tableFields.add(TableFieldVo.builder().label("流水号").value("serialNo").source(SysConst.FieldSource.SYSTEM).show(true).build());
                tableFields.add(TableFieldVo.builder().label("状态").value("status").source(SysConst.FieldSource.SYSTEM).show(true).build());
            }
            Optional.of(menuPo.getDefaultJson()).ifPresent(jsonStr -> getDefaultTableFields(JSONObject.parseObject(jsonStr), tableFields,
                    SysConst.FieldSource.SYSTEM));
            Optional.of(menuPo.getCustomJson()).ifPresent(jsonStr -> getDefaultTableFields(JSONObject.parseObject(jsonStr), tableFields,
                    SysConst.FieldSource.CUSTOM));
        }
    }

    /**
     * 通过表单定义解析出表格的字段
     *
     * @param jsonObject  表单定义的JSON
     * @param tableFields 产生的表格字段
     */
    private void getDefaultTableFields(JSONObject jsonObject, List<TableFieldVo> tableFields, int source) {
        JSONArray widgetList = jsonObject.getJSONArray("widgetList");
        for (int i = 0; i < widgetList.size(); i++) {
            JSONObject widget = widgetList.getJSONObject(i);
            String type = widget.getString("type");
            switch (type) {
                case "grid" -> {
                    JSONArray cols = widget.getJSONArray("cols");
                    for (int j = 0; j < cols.size(); j++) {
                        JSONObject childJson = cols.getJSONObject(j);
                        getDefaultTableFields(childJson, tableFields, source);
                    }
                }
                case "static-text" -> {
                }
                default -> {
                    boolean isTableColumn = widget.getBooleanValue("isTableColumn", true);
                    if (isTableColumn) {
                        String id = widget.getString("id");
                        String label = widget.getJSONObject("options").getString("label");
                        boolean defaultShowInTable = widget.getBooleanValue("defaultShowInTable", true);
                        tableFields.add(TableFieldVo.builder().label(label).value(id).source(source).show(defaultShowInTable).build());
                    }
                }
            }
        }
    }

}
