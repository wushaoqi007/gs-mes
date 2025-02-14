package com.greenstone.mes.system.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.system.dto.query.UserQuery;
import com.greenstone.mes.system.infrastructure.mapper.UserMapper;
import com.greenstone.mes.system.infrastructure.po.UserPo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserRepository {

    private final UserMapper userMapper;

    public UserPo queryNormalInfo(UserQuery query) {
        return queryUser(query, null);
    }

    public List<UserPo> queryBriefInfos(UserQuery query) {
        LambdaQueryWrapper<UserPo> selectWrapper = Wrappers.lambdaQuery(UserPo.class)
                .select(UserPo::getUserId, UserPo::getNickName, UserPo::getEmployeeNo, UserPo::getPosition, UserPo::getDeptId);
        return queryUsers(query, selectWrapper);
    }

    public List<UserPo> queryWorkwxInfos(UserQuery query) {
        LambdaQueryWrapper<UserPo> selectWrapper = Wrappers.lambdaQuery(UserPo.class)
                .select(UserPo::getUserId, UserPo::getNickName, UserPo::getEmployeeNo, UserPo::getPosition, UserPo::getDeptId,
                        UserPo::getWxCpId, UserPo::getWxUserId);
        return queryUsers(query, selectWrapper);
    }

    public List<UserPo> queryNormalInfos(UserQuery query) {
        return queryUsers(query, null);
    }

    public void updateById(UserPo userDo) {
        userMapper.updateById(userDo);
    }

    public UserPo getById(Long userId) {
        return userMapper.selectById(userId);
    }

    /*-------------------------------------------------------------------------------------------*/

    private UserPo queryUser(UserQuery query, LambdaQueryWrapper<UserPo> selectWrapper) {
        LambdaQueryWrapper<UserPo> queryWrapper = getQueryWrapper(query, selectWrapper);
        return userMapper.selectOne(queryWrapper);
    }

    private List<UserPo> queryUsers(UserQuery query, LambdaQueryWrapper<UserPo> selectWrapper) {
        LambdaQueryWrapper<UserPo> queryWrapper = getQueryWrapper(query, selectWrapper);
        return userMapper.selectList(queryWrapper);
    }

    private LambdaQueryWrapper<UserPo> getQueryWrapper(UserQuery query, LambdaQueryWrapper<UserPo> selectWrapper) {
        LambdaQueryWrapper<UserPo> queryWrapper = selectWrapper;
        if (queryWrapper == null) {
            queryWrapper = Wrappers.lambdaQuery(UserPo.class)
                    .select(UserPo::getUserId, UserPo::getUserName, UserPo::getWxCpId, UserPo::getWxUserId,
                            UserPo::getSex, UserPo::getEmail, UserPo::getAvatar, UserPo::getEmployeeNo,
                            UserPo::getPhonenumber, UserPo::getDeptId, UserPo::getNickName,
                            UserPo::getPosition, UserPo::getCreateTime);
        }
        return queryWrapper
                .eq(UserPo::getDeleted, "0")
                .ne(UserPo::getUserId, 1)
                .eq(query.getUserId() != null, UserPo::getUserId, query.getUserId())
                .eq(query.getDeptId() != null, UserPo::getDeptId, query.getDeptId())
                .eq(StrUtil.isNotBlank(query.getUserName()), UserPo::getUserName, query.getUserName())
                .eq(StrUtil.isNotBlank(query.getEmployeeNo()), UserPo::getEmployeeNo, query.getEmployeeNo())
                .eq(StrUtil.isNotBlank(query.getNickName()), UserPo::getNickName, query.getNickName())
                .eq(StrUtil.isNotBlank(query.getWxCpId()), UserPo::getWxCpId, query.getWxCpId())
                .eq(StrUtil.isNotBlank(query.getWxUserId()), UserPo::getWxUserId, query.getWxUserId())
                .eq(StrUtil.isNotBlank(query.getEmail()), UserPo::getEmail, query.getEmail())
                .eq(StrUtil.isNotBlank(query.getPhonenumber()), UserPo::getPhonenumber, query.getPhonenumber())
                .eq(StrUtil.isNotBlank(query.getPhonenumber()), UserPo::getPhonenumber, query.getPhonenumber())
                .in(CollUtil.isNotEmpty(query.getUserIds()), UserPo::getUserId, query.getUserIds())
                .in(CollUtil.isNotEmpty(query.getDeptIds()), UserPo::getDeptId, query.getDeptIds())
                .ge(query.getBeginCreateTime() != null, UserPo::getCreateTime, query.getBeginCreateTime())
                .le(query.getEndCreateTime() != null, UserPo::getCreateTime, query.getEndCreateTime());
    }

    public void changeRole(Long userId, Long roleId) {
        userMapper.updateById(UserPo.builder().userId(userId).roleId(roleId).build());
    }

    public void removeRole(Long userId, Long roleId) {
        if (userId == null && roleId == null) {
            throw new ServiceException("删除用户角色时，请指定要删除的用户或角色");
        }
        LambdaUpdateWrapper<UserPo> updateWrapper = Wrappers.lambdaUpdate(UserPo.class);
        updateWrapper.set(UserPo::getRoleId, null);
        if (userId != null) {
            updateWrapper.eq(UserPo::getUserId, userId);
        }
        if (roleId != null) {
            updateWrapper.eq(UserPo::getRoleId, roleId);
        }
        userMapper.update(updateWrapper);
    }

}
