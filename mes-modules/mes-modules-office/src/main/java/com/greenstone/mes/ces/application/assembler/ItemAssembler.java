package com.greenstone.mes.ces.application.assembler;

import com.greenstone.mes.ces.application.dto.cmd.ItemSpecAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.ItemSpecEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.ItemTypeAddCmd;
import com.greenstone.mes.ces.application.dto.event.ItemTypeAddE;
import com.greenstone.mes.ces.application.dto.event.ItemTypeRemoveE;
import com.greenstone.mes.ces.application.dto.result.ItemSpecResult;
import com.greenstone.mes.ces.application.dto.result.ItemTypeResult;
import com.greenstone.mes.ces.domain.entity.ItemSpec;
import com.greenstone.mes.ces.domain.entity.ItemType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-22-10:53
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ItemAssembler {

    //ItemType
    ItemTypeResult toItemTypeResult(ItemType itemType);

    List<ItemTypeResult> toItemTypeResultS(List<ItemType> itemTypeList);

    ItemType toItemType(ItemTypeAddCmd addCmd);

    //ItemSpec
    ItemSpecResult toItemSpecResult(ItemSpec itemSpec);

    List<ItemSpecResult> toItemSpecResultS(List<ItemSpec> itemSpecList);

    ItemSpec toItemSpec(ItemSpecAddCmd addCmd);

    ItemSpec toItemSpec(ItemSpecEditCmd editCmd);


    // event
    ItemTypeAddE toItemTypeAddEventData(ItemType itemType);

    ItemTypeRemoveE toItemTypeRemoveEventData(ItemType itemType);
}
