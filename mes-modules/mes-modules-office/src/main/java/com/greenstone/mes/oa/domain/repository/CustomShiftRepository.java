package com.greenstone.mes.oa.domain.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.oa.domain.converter.CustomShiftConverter;
import com.greenstone.mes.oa.domain.entity.CustomShift;
import com.greenstone.mes.oa.infrastructure.mapper.CustomShiftMapper;
import com.greenstone.mes.oa.infrastructure.persistence.CustomShiftDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author wsqwork
 * @date 2024/12/12 15:31
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CustomShiftRepository {

    private final CustomShiftConverter converter;
    private final CustomShiftMapper customShiftMapper;


    public List<CustomShift> list(CustomShift customShift) {
        LambdaQueryWrapper<CustomShiftDO> queryWrapper = Wrappers.lambdaQuery(CustomShiftDO.class);
        queryWrapper.like(StrUtil.isNotBlank(customShift.getName()), CustomShiftDO::getName, customShift.getName());
        queryWrapper.orderByDesc(CustomShiftDO::getCreateTime);
        return converter.toEntities(customShiftMapper.selectList(queryWrapper));
    }

    public CustomShift detail(Long id) {
        return converter.toEntity(customShiftMapper.selectById(id));
    }

    public void insert(CustomShift customShift) {
        customShiftMapper.insert(converter.toDO(customShift));
    }

    public void update(CustomShift customShift) {
        customShiftMapper.updateById(converter.toDO(customShift));
    }

    public void delete(Long id) {
        customShiftMapper.deleteById(id);
    }

    public void deleteBatch(Long[] ids) {
        LambdaQueryWrapper<CustomShiftDO> queryWrapper = Wrappers.lambdaQuery(CustomShiftDO.class).in(CustomShiftDO::getId, Arrays.asList(ids));
        customShiftMapper.delete(queryWrapper);
    }
}
