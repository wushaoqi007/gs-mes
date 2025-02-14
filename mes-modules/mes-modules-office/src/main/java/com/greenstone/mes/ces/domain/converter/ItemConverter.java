package com.greenstone.mes.ces.domain.converter;

import com.greenstone.mes.ces.domain.entity.ItemSpec;
import com.greenstone.mes.ces.domain.entity.ItemType;
import com.greenstone.mes.ces.infrastructure.persistence.ItemSpecDO;
import com.greenstone.mes.ces.infrastructure.persistence.ItemTypeDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-22-10:48
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ItemConverter {

    // ItemType
    ItemTypeDO toItemTypeDO(ItemType itemType);

    List<ItemType> toItemTypeList(List<ItemTypeDO> doList);

    ItemType toItemType(ItemTypeDO ItemTypeDO);

    // ItemSpecification
    ItemSpec toItemSpecification(ItemSpecDO itemSpecDO);

    List<ItemSpec> toItemSpecList(List<ItemSpecDO> itemSpecDO);

    ItemSpecDO toSpecInsertDO(ItemSpec specification);

    @Mapping(target = "typeCode", ignore = true)
    ItemSpecDO toSpecUpdateDO(ItemSpec specification);
}
