package com.greenstone.mes.system.domain.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.domain.converter.RoleConverter;
import com.greenstone.mes.system.domain.entity.Role;
import com.greenstone.mes.system.domain.entity.RoleUser;
import com.greenstone.mes.system.infrastructure.mapper.RoleMapper;
import com.greenstone.mes.system.infrastructure.mapper.RoleUserMapper;
import com.greenstone.mes.system.infrastructure.po.RoleDO;
import com.greenstone.mes.system.infrastructure.po.RoleUserDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class RoleRepository {
    private final RoleMapper roleMapper;
    private final RoleConverter converter;
    private final RoleUserMapper roleUserMapper;


    public void saveRole(Role role) {
        role.setRoleId(null);
        RoleDO find = roleMapper.getOneOnly(Wrappers.query(RoleDO.builder().roleName(role.getRoleName()).build()));
        if (find != null) {
            throw new ServiceException(StrUtil.format("角色名称重复：{}", role.getRoleName()));
        }
        RoleDO roleDO = converter.entity2Do(role);
        roleMapper.insert(roleDO);
    }

    public void updateRole(Role role) {
        RoleDO find = roleMapper.getOneOnly(Wrappers.query(RoleDO.builder().roleName(role.getRoleName()).build()));
        if (find != null && !Objects.equals(find.getRoleId(), role.getRoleId())) {
            throw new ServiceException(StrUtil.format("角色名称重复：{}", role.getRoleName()));
        }
        LambdaUpdateWrapper<RoleDO> updateWrapper = Wrappers.lambdaUpdate(RoleDO.class)
                .eq(RoleDO::getRoleId, role.getRoleId()).set(RoleDO::getRoleName, role.getRoleName());
        roleMapper.update(updateWrapper);
    }

    public void updateRoleUserNum(Long roleId) {
        Long count = roleUserMapper.selectCount(RoleUserDO.builder().roleId(roleId).build());
        LambdaUpdateWrapper<RoleDO> updateWrapper = Wrappers.lambdaUpdate(RoleDO.class)
                .eq(RoleDO::getRoleId, roleId).set(RoleDO::getUserNum, count);
        roleMapper.update(updateWrapper);
    }

    public void removeRole(Long roleId) {
        exist(roleId);
        LambdaQueryWrapper<RoleDO> deleteWrapper = Wrappers.lambdaQuery(RoleDO.class)
                .eq(RoleDO::getRoleId, roleId);
        roleMapper.delete(deleteWrapper);
    }

    public List<Role> list(Role role) {
        LambdaQueryWrapper<RoleDO> queryWrapper = Wrappers.lambdaQuery(RoleDO.class);
        if (StrUtil.isNotBlank(role.getRoleName())) {
            queryWrapper.like(RoleDO::getRoleName, role.getRoleName());
        }
        queryWrapper.orderByAsc(RoleDO::getCreateTime);
        List<RoleDO> roleDOS = roleMapper.selectList(queryWrapper);
        return converter.dos2Entities(roleDOS);
    }

    public List<Role> listAll() {
        LambdaQueryWrapper<RoleDO> queryWrapper = Wrappers.lambdaQuery(RoleDO.class);
        List<RoleDO> roleDOS = roleMapper.selectList(queryWrapper);
        return converter.dos2Entities(roleDOS);
    }

    public Role exist(Long roleId) {
        RoleDO roleDO = roleMapper.getOneOnly(RoleDO.builder().roleId(roleId).build());
        if (roleDO == null) {
            throw new ServiceException(StrUtil.format("角色不存在，roleId:{}", roleId));
        }
        return converter.do2Entity(roleDO);
    }

    public Role selectRoleById(Long roleId) {
        RoleDO roleDO = roleMapper.getOneOnly(RoleDO.builder().roleId(roleId).build());
        return converter.do2Entity(roleDO);
    }

    public RoleUser selectRoleUserByUserId(Long userId) {
        RoleUserDO roleUserDOS = roleUserMapper.getOneOnly(RoleUserDO.builder().userId(userId).build());
        return converter.roleUserDo2Entity(roleUserDOS);
    }

    public List<RoleUser> selectRoleUsersByUserIds(List<Long> userIds) {
        LambdaQueryWrapper<RoleUserDO> queryWrapper = Wrappers.lambdaQuery(RoleUserDO.class).in(RoleUserDO::getUserId, userIds);
        List<RoleUserDO> roleUserDOS = roleUserMapper.selectList(queryWrapper);
        return converter.roleUserDos2Entities(roleUserDOS);
    }

    public List<RoleUser> selectRoleUsersByRole(Long roleId) {
        LambdaQueryWrapper<RoleUserDO> queryWrapper = Wrappers.lambdaQuery(RoleUserDO.class).eq(RoleUserDO::getRoleId, roleId);
        List<RoleUserDO> roleUserDOS = roleUserMapper.selectList(queryWrapper);
        return converter.roleUserDos2Entities(roleUserDOS);
    }

    public RoleUser selectRoleUser(Long roleId, Long userId) {
        RoleUserDO roleUserDO = roleUserMapper.getOneOnly(RoleUserDO.builder().roleId(roleId).userId(userId).build());
        return converter.roleUserDo2Entity(roleUserDO);
    }

    public void addRoleUserBatch(List<RoleUser> allocatedRoleUser) {
        List<RoleUserDO> insertDos = converter.roleUserEntities2Dos(allocatedRoleUser);
        roleUserMapper.insertBatchSomeColumn(insertDos);
    }

    public void addRoleUser(RoleUser roleUser) {
        RoleUserDO insertDo = converter.roleUserEntity2Do(roleUser);
        roleUserMapper.insert(insertDo);
    }

    public void removeRoleUser(RoleUser roleUser) {
        LambdaQueryWrapper<RoleUserDO> deleteWrapper = Wrappers.lambdaQuery(RoleUserDO.class)
                .eq(RoleUserDO::getRoleId, roleUser.getRoleId())
                .eq(RoleUserDO::getUserId, roleUser.getUserId());
        roleUserMapper.delete(deleteWrapper);
    }

    public void removeRoleUserByRoleId(Long roleId) {
        LambdaQueryWrapper<RoleUserDO> deleteWrapper = Wrappers.lambdaQuery(RoleUserDO.class)
                .eq(RoleUserDO::getRoleId, roleId);
        roleUserMapper.delete(deleteWrapper);
    }

    public void removeRoleUserByUserId(Long userId) {
        LambdaQueryWrapper<RoleUserDO> deleteWrapper = Wrappers.lambdaQuery(RoleUserDO.class)
                .eq(RoleUserDO::getUserId, userId);
        roleUserMapper.delete(deleteWrapper);
    }

    public List<User> selectUnallocatedUsers(User user) {
        return roleUserMapper.selectUnallocatedUsers(user);
    }

    public List<User> selectAllocatedUsers(Long roleId) {
        return roleUserMapper.selectAllocatedUsers(roleId);
    }
}
