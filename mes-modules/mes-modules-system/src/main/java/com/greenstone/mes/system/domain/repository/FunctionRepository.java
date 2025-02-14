package com.greenstone.mes.system.domain.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.system.domain.converter.FunctionConverter;
import com.greenstone.mes.system.domain.entity.Function;
import com.greenstone.mes.system.infrastructure.mapper.FunctionMapper;
import com.greenstone.mes.system.infrastructure.po.FunctionDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2024-10-17-16:33
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class FunctionRepository {
    private final FunctionMapper functionMapper;
    private final FunctionConverter converter;


    public void saveFunction(Function function) {
        FunctionDO find = functionMapper.getOneOnly(Wrappers.query(FunctionDO.builder().name(function.getName()).build()));
        if (find != null) {
            throw new ServiceException(StrUtil.format("功能名称重复：{}", function.getName()));
        }
        FunctionDO functionDO = converter.entity2Do(function);
        functionMapper.insert(functionDO);
        function.setId(functionDO.getId());
    }

    public void updateFunction(Function function) {
        FunctionDO find = functionMapper.getOneOnly(Wrappers.query(FunctionDO.builder().name(function.getName()).build()));
        if (find != null && !Objects.equals(find.getId(), function.getId())) {
            throw new ServiceException(StrUtil.format("功能名称重复：{}", function.getName()));
        }
        FunctionDO functionDO = converter.entity2Do(function);
        functionMapper.updateById(functionDO);
    }

    public void removeFunction(Long id) {
        functionMapper.deleteById(id);
    }

    public List<Function> listAll() {
        LambdaQueryWrapper<FunctionDO> queryWrapper = Wrappers.lambdaQuery(FunctionDO.class);
        queryWrapper.orderByAsc(FunctionDO::getOrderNum);
        List<FunctionDO> functionDOS = functionMapper.selectList(queryWrapper);
        return converter.dos2Entities(functionDOS);
    }

    public Function selectById(Long id) {
        FunctionDO functionDO = functionMapper.selectById(id);
        return converter.do2Entity(functionDO);
    }

    public Function exist(Long id) {
        FunctionDO functionDO = functionMapper.selectById(id);
        if (functionDO == null) {
            throw new ServiceException(StrUtil.format("功能不存在，功能id:{}", id));
        }
        return converter.do2Entity(functionDO);
    }

    public Long selectCount() {
        return functionMapper.selectCount(FunctionDO.builder().build());
    }

    public void updateSort(Long id, Integer orderNum) {
        LambdaUpdateWrapper<FunctionDO> updateWrapper = Wrappers.lambdaUpdate(FunctionDO.class)
                .eq(FunctionDO::getId, id).set(FunctionDO::getOrderNum, orderNum);
        functionMapper.update(updateWrapper);
    }

}
