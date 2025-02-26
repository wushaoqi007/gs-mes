package com.greenstone.mes.system.infrastructure.mapper;

import com.greenstone.mes.system.domain.SysRoleMenu;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色与菜单关联表 数据层
 *
 * @author ruoyi
 */
@Repository
public interface SysRoleMenuMapper {
    /**
     * 查询菜单使用数量
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    public int checkMenuExistRole(String menuId);

    /**
     * 删除菜单和角色关联
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    public void deleteRoleMenuByMenuId(String menuId);

    /**
     * 通过角色ID删除角色和菜单关联
     *
     * @param roleId 角色ID
     * @return 结果
     */
    public int deleteRoleMenuByRoleId(Long roleId);

    /**
     * 批量删除角色菜单关联信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteRoleMenu(Long[] ids);

    /**
     * 批量新增角色菜单信息
     *
     * @param roleMenuList 角色菜单列表
     * @return 结果
     */
    public int batchRoleMenu(List<SysRoleMenu> roleMenuList);
}
