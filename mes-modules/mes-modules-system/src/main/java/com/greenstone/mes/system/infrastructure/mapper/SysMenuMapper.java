package com.greenstone.mes.system.infrastructure.mapper;

import com.greenstone.mes.system.domain.SysMenu;
import com.greenstone.mes.system.infrastructure.po.MenuPo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜单表 数据层
 *
 * @author ruoyi
 */
@Repository
public interface SysMenuMapper {
    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    List<MenuPo> selectMenuList(SysMenu menu);

    /**
     * 根据用户所有权限
     *
     * @return 权限列表
     */
    List<String> selectMenuPerms();

    /**
     * 根据用户查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    List<MenuPo> selectMenuListByUserId(SysMenu menu);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据用户ID查询菜单
     *
     * @return 菜单列表
     */
    List<MenuPo> selectMenuTreeAll();

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<MenuPo> selectMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId            角色ID
     * @param menuCheckStrictly 菜单树选择项是否关联显示
     * @return 选中菜单列表
     */
    List<String> selectMenuListByRoleId(@Param("roleId") Long roleId, @Param("menuCheckStrictly") boolean menuCheckStrictly);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    int hasChildByMenuId(String menuId);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menuName 菜单名称
     * @param parentId 父菜单ID
     * @return 结果
     */
    SysMenu checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") String parentId);
}
