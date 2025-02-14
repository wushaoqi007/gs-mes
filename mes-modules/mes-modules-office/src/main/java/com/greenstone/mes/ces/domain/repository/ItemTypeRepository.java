package com.greenstone.mes.ces.domain.repository;

import com.greenstone.mes.ces.domain.converter.ItemConverter;
import com.greenstone.mes.ces.domain.entity.ItemType;
import com.greenstone.mes.ces.infrastructure.mapper.ItemTypeMapper;
import com.greenstone.mes.ces.infrastructure.persistence.ItemTypeDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-22-10:33
 */
@Slf4j
@Service
public class ItemTypeRepository {

    @Autowired
    private ItemTypeMapper itemTypeMapper;

    @Autowired
    private ItemConverter itemConverter;

    public List<ItemType> listAll() {
        return itemConverter.toItemTypeList(itemTypeMapper.selectList(null));
    }


    public void save(ItemType itemType, boolean isNew) {
        ItemTypeDO itemTypeDO = itemConverter.toItemTypeDO(itemType);
        if (isNew) {
            itemTypeMapper.insert(itemTypeDO);
            itemType.setId(itemTypeDO.getId());
        } else {
            itemTypeMapper.updateById(itemTypeDO);
        }
    }

    public void updateChildren(List<ItemType> children) {
        for (ItemType child : children) {
            itemTypeMapper.updateById(itemConverter.toItemTypeDO(child));
        }
    }

    public void removeById(Long id) {
        itemTypeMapper.deleteById(id);
    }

    public ItemType getById(Long id) {
        return itemConverter.toItemType(itemTypeMapper.selectById(id));
    }

    public ItemType getByCode(String typeCode) {
        ItemTypeDO typeDO = itemTypeMapper.getOneOnly(ItemTypeDO.builder().typeCode(typeCode).build());
        return itemConverter.toItemType(typeDO);
    }

    public boolean haveChildren(String typeCode) {
        return null != itemTypeMapper.getOneOnly(ItemTypeDO.builder().parentTypeCode(typeCode).build());
    }

    public Long countChildren(String parentTypeCode) {
        return itemTypeMapper.selectCount(ItemTypeDO.builder().parentTypeCode(parentTypeCode).build());
    }

    public List<ItemType> listChildren(String parentTypeCode) {
        List<ItemTypeDO> children = itemTypeMapper.list(ItemTypeDO.builder().parentTypeCode(parentTypeCode).build());
        return itemConverter.toItemTypeList(children);
    }
}
