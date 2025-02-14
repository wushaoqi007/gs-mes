package com.greenstone.mes.ces.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.ces.application.assembler.ItemAssembler;
import com.greenstone.mes.ces.application.dto.cmd.ItemTypeAddCmd;
import com.greenstone.mes.ces.application.dto.event.ItemTypeAddE;
import com.greenstone.mes.ces.application.dto.event.ItemTypeRemoveE;
import com.greenstone.mes.ces.application.dto.result.ItemTypeResult;
import com.greenstone.mes.ces.application.event.ItemTypeAddEvent;
import com.greenstone.mes.ces.application.event.ItemTypeRemoveEvent;
import com.greenstone.mes.ces.application.service.ItemTypeService;
import com.greenstone.mes.ces.domain.entity.ItemType;
import com.greenstone.mes.ces.domain.repository.ItemSpecRepository;
import com.greenstone.mes.ces.domain.repository.ItemTypeRepository;
import com.greenstone.mes.common.core.enums.ItemError;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-22-10:38
 */
@Slf4j
@Service
public class ItemTypeServiceImpl implements ItemTypeService {

    private final ItemTypeRepository itemTypeRepository;
    private final ItemAssembler itemAssembler;
    private final ApplicationEventPublisher eventPublisher;
    private final ItemSpecRepository itemSpecRepository;

    public ItemTypeServiceImpl(ItemTypeRepository itemTypeRepository, ItemAssembler itemAssembler,
                               ApplicationEventPublisher eventPublisher, ItemSpecRepository itemSpecRepository) {
        this.itemTypeRepository = itemTypeRepository;
        this.itemAssembler = itemAssembler;
        this.eventPublisher = eventPublisher;
        this.itemSpecRepository = itemSpecRepository;
    }

    @Override
    public void remove(String typeCode) {
        // 校验：分类是否存在
        ItemType self = itemTypeRepository.getByCode(typeCode);
        if (self == null) {
            throw new ServiceException(ItemError.E80109);
        }
        boolean haveChildren = itemTypeRepository.haveChildren(self.getTypeCode());
        // 只能删除末级分类
        if (haveChildren) {
            throw new ServiceException(ItemError.E80110);
        }
        // 校验：是否有同级的末级分类
        Long children = itemTypeRepository.countChildren(self.getParentTypeCode());
        boolean havePeer = children > 1L;
        // 校验：是否被规格型号引用
        boolean existSpecByType = itemSpecRepository.existByTypeCode(self.getTypeCode());
        if (havePeer && existSpecByType) {
            throw new ServiceException(ItemError.E80107);
        }

        itemTypeRepository.removeById(self.getId());

        // 通知：删除没有同级的末级分类，自动将末级分类下的型号和库存移动到上一级分类
        if (!havePeer) {
            ItemTypeRemoveE eventData = itemAssembler.toItemTypeRemoveEventData(self);
            eventPublisher.publishEvent(new ItemTypeRemoveEvent(eventData));
        }
    }

    @Override
    public List<ItemTypeResult> list() {
        List<ItemType> itemTypeList = itemTypeRepository.listAll();
        return itemAssembler.toItemTypeResultS(itemTypeList);
    }

    @Override
    public void add(ItemTypeAddCmd addCmd) {
        log.info("ItemTypeAddCmd params:{}", addCmd);
        ItemType itemType = itemAssembler.toItemType(addCmd);

        boolean isNewOne = itemType.getId() == null;
        // 校验：原物品类型是否存在
        ItemType itemTypeExist = itemTypeRepository.getById(itemType.getId());
        if (!isNewOne && itemTypeExist == null) {
            throw new ServiceException(ItemError.E80101);
        }
        // 校验：不允许新增已使用的分类编码
        ItemType itemTypeWithCodeUsed = itemTypeRepository.getByCode(itemType.getTypeCode());
        boolean codeHasBeenUsed = itemTypeWithCodeUsed != null;
        if (isNewOne && codeHasBeenUsed) {
            throw new ServiceException(ItemError.E80105);
        }
        // 校验：不允许更新为已使用的分类编码
        boolean codeUsedByAnOther = codeHasBeenUsed && !itemType.getId().equals(itemTypeWithCodeUsed.getId());
        if (!isNewOne && codeUsedByAnOther) {
            throw new ServiceException(ItemError.E80105);
        }

        ItemType parentType = null;
        boolean haveParent = StrUtil.isNotEmpty(itemType.getParentTypeCode());
        if (haveParent) {
            // 校验：父物品分类不能是自身
            if (itemType.getTypeCode().equals(itemType.getParentTypeCode())) {
                throw new ServiceException(ItemError.E80111);
            }
            // 校验：上级节点是否存在
            parentType = itemTypeRepository.getByCode(itemType.getParentTypeCode());
            if (parentType == null) {
                throw new ServiceException(ItemError.E80104);
            }
        }
        // 校验：更新时，不能将分类移动到末级分类下，如果没有移动结构则不需要校验
        boolean parentTypeChanged = !isNewOne && haveParent && !itemType.getParentTypeCode().equals(itemTypeExist.getParentTypeCode());
        if (parentTypeChanged) {
            if (!itemTypeRepository.haveChildren(parentType.getTypeCode())) {
                throw new ServiceException(ItemError.E80106);
            }
        }

        // 保存物品分类
        if (isNewOne) {
            log.info("is new one,save ItemType params:{}", itemType);
            itemTypeRepository.save(itemType, true);
        }
        // 更新物品分类，保存后需要更新层级结构，在这里一起更新了
        setTypeHierarchy(itemType, parentType);
        log.info("save ItemType params:{}", itemType);
        itemTypeRepository.save(itemType, false);

        // 更新子分类的信息
        List<ItemType> children = itemTypeRepository.listChildren(itemType.getTypeCode());
        if (CollUtil.isNotEmpty(children)) {
            for (ItemType child : children) {
                child.setParentTypeCode(itemType.getTypeCode());
                setTypeHierarchy(child, itemType);
            }
            itemTypeRepository.updateChildren(children);
        }

        // 通知：新增
        if (isNewOne) {
            ItemTypeAddE eventData = itemAssembler.toItemTypeAddEventData(itemType);
            eventPublisher.publishEvent(new ItemTypeAddEvent(eventData));
        }

    }

    private void setTypeHierarchy(ItemType self, ItemType parent) {
        String idHierarchy = (parent == null) ? String.valueOf(self.getId()) : parent.getIdHierarchy() + "|" + self.getId();
        self.setIdHierarchy(idHierarchy);
        String nameHierarchy = (parent == null) ? self.getTypeName() : self.getTypeName() + " / " + parent.getNameHierarchy();
        self.setNameHierarchy(nameHierarchy);
    }
}
