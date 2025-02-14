package com.greenstone.mes.system.domain.service;

import com.greenstone.mes.system.application.dto.cmd.MenuMoveCmd;
import com.greenstone.mes.system.application.dto.result.MenuTree;
import com.greenstone.mes.system.domain.SysMenu;
import com.greenstone.mes.system.domain.vo.TreeSelect;
import com.greenstone.mes.system.dto.cmd.CustomFormMenuAddCmd;
import com.greenstone.mes.system.dto.cmd.CustomFormMenuEditCmd;
import com.greenstone.mes.system.dto.cmd.MenuAddCmd;
import com.greenstone.mes.system.dto.cmd.MenuEditCmd;
import com.greenstone.mes.system.dto.result.FormDefinitionVo;
import com.greenstone.mes.system.infrastructure.po.MenuPo;

import java.util.List;
import java.util.Set;

/**
 * 菜单 业务层
 *
 * @author gu_renkai
 */
public interface MenuService {

    /**
     * 重置菜单的排序，按照原来的排序重置为 0 开始的排序
     */
    void resetMenuOrder();

    /**
     * 菜单排序，移动菜单时将自动排序
     *
     * @param sortCmd 排序对象
     */
    void moveMenu(MenuMoveCmd sortCmd);

    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<MenuPo> selectMenuList(Long userId);

    /**
     * 根据用户查询系统菜单列表
     *
     * @param menu   菜单信息
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<MenuPo> selectMenuList(SysMenu menu, Long userId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据用户ID查询菜单树信息
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<MenuTree> selectMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    List<String> selectMenuListByRoleId(Long roleId);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    List<TreeSelect> buildMenuTreeSelect(List<MenuPo> menus);


    List<MenuTree> buildMenuTree(List<MenuPo> menus);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    MenuPo selectMenuById(String menuId);

    MenuPo selectFormById(String menuId);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    boolean hasChildByMenuId(String menuId);

    /**
     * 查询菜单是否存在角色
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    boolean checkMenuExistRole(String menuId);

    void deleteRoleMenuByMenuId(String menuId);

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int addMenu(MenuAddCmd menu);

    /**
     * 修改保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int updateMenu(MenuEditCmd menu);

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    int deleteMenuById(String menuId);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    String checkMenuNameUnique(SysMenu menu);

    // ---------------------- 提供给自动逸表单的操作 ----------------------

    FormDefinitionVo getFormDefinition(String menuId);

    void addCustomMenu(CustomFormMenuAddCmd addCmd);

    void editCustomMenu(CustomFormMenuEditCmd editCmd);

}
