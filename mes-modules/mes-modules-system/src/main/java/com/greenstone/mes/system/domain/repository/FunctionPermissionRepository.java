package com.greenstone.mes.system.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.system.infrastructure.mapper.FunctionPermissionMapper;
import com.greenstone.mes.system.infrastructure.po.FunctionPermissionDO;
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
public class FunctionPermissionRepository {
    private final FunctionPermissionMapper functionPermissionMapper;

    public List<FunctionPermissionDO> listAll() {
        log.info("查询所有功能权限组");
        return functionPermissionMapper.list(FunctionPermissionDO.builder().build());
    }

    public List<FunctionPermissionDO> selectByFunctionId(Long functionId) {
        List<FunctionPermissionDO> list = functionPermissionMapper.list(FunctionPermissionDO.builder().functionId(functionId).build());
        if (CollUtil.isEmpty(list)) {
            throw new ServiceException(StrUtil.format("该功能未配置权限组，功能id:{}", functionId));
        }
        return list;
    }

    public List<FunctionPermissionDO> list(Long functionId) {
        return functionPermissionMapper.list(FunctionPermissionDO.builder().functionId(functionId).build());
    }

    public FunctionPermissionDO selectByFunctionPermissionId(Long functionPermissionId) {
        return functionPermissionMapper.selectById(functionPermissionId);
    }

    public void deleteByFunctionId(Long functionId) {
        log.info("根据功能删除权限组，功能id:{}", functionId);
        LambdaQueryWrapper<FunctionPermissionDO> queryWrapper = Wrappers.lambdaQuery(FunctionPermissionDO.class)
                .eq(FunctionPermissionDO::getFunctionId, functionId);
        functionPermissionMapper.delete(queryWrapper);
    }

    public void addFunctionPermissions(List<FunctionPermissionDO> insertFunctionPermissions) {
        log.info("新增功能权限组：{}", insertFunctionPermissions);
        functionPermissionMapper.insertBatchSomeColumn(insertFunctionPermissions);
    }
}
