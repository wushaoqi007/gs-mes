package com.greenstone.mes.ces.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.ces.application.assembler.ItemAssembler;
import com.greenstone.mes.ces.application.dto.cmd.ItemSpecAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.ItemSpecEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.ItemSpecRemoveCmd;
import com.greenstone.mes.ces.application.dto.event.ItemTypeAddE;
import com.greenstone.mes.ces.application.dto.event.ItemTypeRemoveE;
import com.greenstone.mes.ces.application.dto.query.ItemSpecFuzzyQuery;
import com.greenstone.mes.ces.application.dto.query.ItemSpecQuery;
import com.greenstone.mes.ces.application.dto.result.ItemSpecResult;
import com.greenstone.mes.ces.application.service.ItemSpecService;
import com.greenstone.mes.ces.domain.entity.ItemSpec;
import com.greenstone.mes.ces.domain.entity.ItemType;
import com.greenstone.mes.ces.domain.entity.WarehouseStock;
import com.greenstone.mes.ces.domain.repository.ItemSpecRepository;
import com.greenstone.mes.ces.domain.repository.ItemTypeRepository;
import com.greenstone.mes.ces.domain.repository.WarehouseStockRepository;
import com.greenstone.mes.ces.infrastructure.constant.ItemSnConst;
import com.greenstone.mes.common.core.enums.ItemError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-05-22-10:41
 */
@Slf4j
@Service
public class ItemSpecServiceImpl implements ItemSpecService {

    private final ItemSpecRepository itemSpecRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final ItemAssembler itemAssembler;
    private final RemoteSystemService systemService;
    private final WarehouseStockRepository stockRepository;

    public ItemSpecServiceImpl(ItemSpecRepository itemSpecRepository, ItemTypeRepository itemTypeRepository,
                               ItemAssembler itemAssembler, RemoteSystemService systemService,
                               WarehouseStockRepository stockRepository) {
        this.itemSpecRepository = itemSpecRepository;
        this.itemTypeRepository = itemTypeRepository;
        this.itemAssembler = itemAssembler;
        this.systemService = systemService;
        this.stockRepository = stockRepository;
    }


    @Override
    public List<ItemSpecResult> list(ItemSpecQuery query) {
        log.info("ItemSpecQuery params:{}", query);
        return listContainsChildren(query.getTypeCode(), new ArrayList<>());
    }

    public List<ItemSpecResult> listContainsChildren(String typeCode, List<ItemSpecResult> itemSpecResultList) {
        List<ItemType> itemTypeList = itemTypeRepository.listChildren(typeCode);
        if (CollUtil.isNotEmpty(itemTypeList)) {
            for (ItemType itemType : itemTypeList) {
                listContainsChildren(itemType.getTypeCode(), itemSpecResultList);
            }
        } else {
            itemSpecResultList.addAll(itemAssembler.toItemSpecResultS(itemSpecRepository.listByTypeCode(typeCode)));
        }
        return itemSpecResultList;
    }

    @Override
    public List<ItemSpecResult> search(ItemSpecFuzzyQuery query) {
        log.info("ItemSpecFuzzyQuery params:{}", query);
        return itemAssembler.toItemSpecResultS(itemSpecRepository.list(query));
    }

    @Override
    public void add(ItemSpecAddCmd addCmd) {
        log.info("ItemSpecAddCmd params:{}", addCmd);
        // 校验：物品类型是否存在
        ItemType itemTypeExist = itemTypeRepository.getByCode(addCmd.getTypeCode());
        if (itemTypeExist == null) {
            throw new ServiceException(ItemError.E80101);
        }
        // 校验：只允许选择末级分类
        boolean haveChildren = itemTypeRepository.haveChildren(addCmd.getTypeCode());
        if (haveChildren) {
            throw new ServiceException(ItemError.E82001);
        }
        // 赋值：默认的物品编码
        if (StrUtil.isEmpty(addCmd.getItemCode())) {
            SerialNoNextCmd nextCmd =
                    SerialNoNextCmd.builder().type(ItemSnConst.getItemSnType(addCmd.getTypeCode())).prefix(addCmd.getTypeCode() + DateUtil.yearSerialStrNow()).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);
            if (serialNoR == null || serialNoR.getSerialNo() == null) {
                throw new ServiceException(ItemError.E82005);
            }
            addCmd.setItemCode(serialNoR.getSerialNo());
        }
        // 校验：物品编码是否重复
        boolean itemSpecExist = itemSpecRepository.existByItemCode(addCmd.getItemCode());
        if (itemSpecExist) {
            throw new ServiceException(ItemError.E82003);
        }
        addCmd.setTypeName(itemTypeExist.getTypeName());
        itemSpecRepository.save(itemAssembler.toItemSpec(addCmd));
    }

    @Override
    public void edit(ItemSpecEditCmd editCmd) {
        log.info("ItemSpecEditCmd params:{}", editCmd);
        itemSpecRepository.save(itemAssembler.toItemSpec(editCmd));
    }

    @Override
    public void remove(ItemSpecRemoveCmd removeCmd) {
        log.info("ItemSpecRemoveCmd params:{}", removeCmd);
        // 校验：删除的数据是否存在
        ItemSpec itemSpec = itemSpecRepository.getById(removeCmd.getId());
        if (Objects.isNull(itemSpec)) {
            throw new ServiceException(ItemError.E81001);
        }
        // 校验：是否有库存
        List<WarehouseStock> stockList = stockRepository.getStockByItemCode(itemSpec.getItemCode());
        if (CollUtil.isNotEmpty(stockList)) {
            throw new ServiceException(ItemError.E81002);
        }
        itemSpecRepository.remove(removeCmd.getId());
    }

    @Transactional
    @Override
    public void typeAddEvent(ItemTypeAddE addE) {
        log.info("ItemTypeAddE params:{}", addE);
        // 若在末级下新增分类，则将原分类的型号规格移动到新分类下
        Long children = itemTypeRepository.countChildren(addE.getParentTypeCode());
        if (children == 1L) {
            itemSpecRepository.moveTo(addE.getParentTypeCode(), addE.getTypeCode(), addE.getTypeName());
        }
    }

    @Override
    public void typeRemoveEvent(ItemTypeRemoveE removeE) {
        log.info("ItemTypeRemoveE params:{}", removeE);
        // 删除末级分类，自动将末级分类下的型号和库存移动到上一级分类。
        if (StrUtil.isNotEmpty(removeE.getParentTypeCode())) {
            ItemType parentType = itemTypeRepository.getByCode(removeE.getParentTypeCode());
            itemSpecRepository.moveTo(removeE.getTypeCode(), removeE.getParentTypeCode(), parentType.getTypeName());
        }
    }
}
