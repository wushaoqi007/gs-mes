package com.greenstone.mes.system.domain.repository;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.system.infrastructure.mapper.PermissionGroupTempMapper;
import com.greenstone.mes.system.infrastructure.po.PermissionGroupTempDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-22-9:27
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class PermissionGroupTempRepository {
    private final PermissionGroupTempMapper permissionGroupTempMapper;

    public List<PermissionGroupTempDO> listAll() {
        List<PermissionGroupTempDO> list = permissionGroupTempMapper.list(PermissionGroupTempDO.builder().build());
        if (CollUtil.isEmpty(list)) {
            throw new ServiceException("未配置功能权限组模板");
        }
        return list;
    }
}
