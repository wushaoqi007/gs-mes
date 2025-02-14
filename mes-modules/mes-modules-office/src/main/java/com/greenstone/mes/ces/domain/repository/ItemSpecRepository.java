package com.greenstone.mes.ces.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.ces.application.dto.query.ItemSpecFuzzyQuery;
import com.greenstone.mes.ces.domain.converter.ItemConverter;
import com.greenstone.mes.ces.domain.entity.ItemSpec;
import com.greenstone.mes.ces.enums.ItemStatus;
import com.greenstone.mes.ces.infrastructure.mapper.ItemSpecMapper;
import com.greenstone.mes.ces.infrastructure.persistence.ItemSpecDO;
import com.greenstone.mes.common.core.enums.ItemError;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-05-22-10:33
 */
@Slf4j
@Service
public class ItemSpecRepository {
    @Autowired
    private ItemSpecMapper specMapper;
    @Autowired
    private ItemConverter itemConverter;
    @Autowired
    private ItemTypeRepository itemTypeRepository;

    public List<ItemSpec> listByTypeCode(String typeCode) {
        List<ItemSpecDO> doList;
        ItemSpecDO selectDO = ItemSpecDO.builder().build();
        if (StrUtil.isNotEmpty(typeCode)) {
            selectDO.setTypeCode(typeCode);
        }
        doList = specMapper.list(selectDO);
        return itemConverter.toItemSpecList(doList);
    }

    public void save(ItemSpec spec) {
        boolean isNewOne = spec.getId() == null;
        // 新增规格型号
        if (isNewOne) {
            // 校验：物品分类是否存在
            if (Objects.isNull(itemTypeRepository.getByCode(spec.getTypeCode()))) {
                throw new ServiceException(ItemError.E80101);
            }
            spec.setStatus(ItemStatus.NORMAL);
            specMapper.insert(itemConverter.toSpecInsertDO(spec));
        }
        // 更新规格型号
        else {
            // 校验：修改的数据是否存在
            if (Objects.isNull(specMapper.selectById(spec.getId()))) {
                throw new ServiceException(ItemError.E81001);
            }
            specMapper.updateById(itemConverter.toSpecUpdateDO(spec));
        }
    }

    public ItemSpec getById(Long id) {
        return itemConverter.toItemSpecification(specMapper.selectById(id));
    }

    public void remove(Long id) {
        specMapper.deleteById(id);
    }

    public void moveTo(String fromTypeCode, String toTypeCode, String newTypeName) {
        LambdaUpdateWrapper<ItemSpecDO> updateWrapper =
                Wrappers.lambdaUpdate(ItemSpecDO.class).eq(ItemSpecDO::getTypeCode, fromTypeCode).set(ItemSpecDO::getTypeCode, toTypeCode).set(ItemSpecDO::getTypeName, newTypeName);
        specMapper.update(updateWrapper);
    }

    public boolean existByTypeCode(String typeCode) {
        return specMapper.exists(ItemSpecDO.builder().typeCode(typeCode).build());
    }

    public boolean existByItemCode(String itemCode) {
        return specMapper.exists(ItemSpecDO.builder().itemCode(itemCode).build());
    }

    public ItemSpec detail(String itemCode) {
        return itemConverter.toItemSpecification(specMapper.getOneOnly(ItemSpecDO.builder().itemCode(itemCode).build()));
    }

    public List<ItemSpec> list(ItemSpecFuzzyQuery fuzzyQuery) {
        QueryWrapper<ItemSpecDO> fuzzyQueryWrapper = new QueryWrapper<>();
        fuzzyQueryWrapper.orderByDesc("create_time");
        if (StrUtil.isNotEmpty(fuzzyQuery.getKey()) && CollUtil.isNotEmpty(fuzzyQuery.getFields())) {
            fuzzyQueryWrapper.and(wrapper -> {
                for (String field : fuzzyQuery.getFields()) {
                    String fieldCode = StrUtil.toSymbolCase(field, '_');
                    wrapper.or().like(fieldCode, fuzzyQuery.getKey());
                }
            });
        }
        List<ItemSpecDO> doList = specMapper.selectList(fuzzyQueryWrapper);
        return itemConverter.toItemSpecList(doList);
    }
}
