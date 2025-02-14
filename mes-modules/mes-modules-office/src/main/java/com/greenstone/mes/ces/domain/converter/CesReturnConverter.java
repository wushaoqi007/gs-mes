package com.greenstone.mes.ces.domain.converter;

import com.greenstone.mes.ces.domain.entity.CesReturn;
import com.greenstone.mes.ces.domain.entity.CesReturnItem;
import com.greenstone.mes.ces.infrastructure.persistence.CesReturnDO;
import com.greenstone.mes.ces.infrastructure.persistence.CesReturnItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CesReturnConverter {

    CesReturn toCesReturn(CesReturnDO cesReturnDO);

    @Mapping(target = "id", source = "cesReturnDO.id")
    @Mapping(target = "serialNo", source = "cesReturnDO.serialNo")
    @Mapping(target = "returnDate", source = "cesReturnDO.returnDate")
    @Mapping(target = "remark", source = "cesReturnDO.remark")
    @Mapping(target = "items", source = "itemDOs")
    @Mapping(target = "returnById", source = "cesReturnDO.returnById")
    @Mapping(target = "returnByName", source = "cesReturnDO.returnByName")
    @Mapping(target = "returnByNo", source = "cesReturnDO.returnByNo")
    CesReturn toCesReturn(CesReturnDO cesReturnDO, List<CesReturnItemDO> itemDOs);

    CesReturnDO toCesReturnDO(CesReturn cesReturn);

    CesReturnItemDO toCesReturnItemDO(CesReturnItem item);

    List<CesReturnItemDO> toCesReturnItemDOs(List<CesReturnItem> items);

    CesReturnItem toCesReturnItemS(CesReturnItemDO itemDO);

    List<CesReturnItem> toCesReturnItemS(List<CesReturnItemDO> itemDOs);
}
