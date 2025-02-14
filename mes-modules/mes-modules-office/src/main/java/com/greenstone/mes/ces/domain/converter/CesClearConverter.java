package com.greenstone.mes.ces.domain.converter;

import com.greenstone.mes.ces.domain.entity.CesClear;
import com.greenstone.mes.ces.domain.entity.CesClearItem;
import com.greenstone.mes.ces.infrastructure.persistence.CesClearDO;
import com.greenstone.mes.ces.infrastructure.persistence.CesClearItemDO;
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
public interface CesClearConverter {

    CesClear toCesClear(CesClearDO cesClearDO);

    @Mapping(target = "id", source = "cesClearDO.id")
    @Mapping(target = "serialNo", source = "cesClearDO.serialNo")
    @Mapping(target = "clearDate", source = "cesClearDO.clearDate")
    @Mapping(target = "remark", source = "cesClearDO.remark")
    @Mapping(target = "items", source = "itemDOs")
    @Mapping(target = "clearById", source = "cesClearDO.clearById")
    @Mapping(target = "clearByName", source = "cesClearDO.clearByName")
    @Mapping(target = "clearByNo", source = "cesClearDO.clearByNo")
    CesClear toCesClear(CesClearDO cesClearDO, List<CesClearItemDO> itemDOs);

    CesClearDO toCesClearDO(CesClear cesClear);

    CesClearItemDO toCesClearItemDO(CesClearItem item);

    List<CesClearItemDO> toCesClearItemDOs(List<CesClearItem> items);

    CesClearItem toCesClearItemS(CesClearItemDO itemDO);

    List<CesClearItem> toCesClearItemS(List<CesClearItemDO> itemDOs);
}
