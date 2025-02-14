package com.greenstone.mes.ces.domain.converter;

import com.greenstone.mes.ces.domain.entity.WarehouseIn;
import com.greenstone.mes.ces.domain.entity.WarehouseInDetail;
import com.greenstone.mes.ces.infrastructure.persistence.WarehouseInDO;
import com.greenstone.mes.ces.infrastructure.persistence.WarehouseInDetailDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-06-2-9:58
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WarehouseInConverter {

    WarehouseIn toWarehouseIn(WarehouseInDO warehouseInDO);

    @Mapping(target = "id", source = "warehouseInDO.id")
    @Mapping(target = "serialNo", source = "warehouseInDO.serialNo")
    @Mapping(target = "warehouseCode", source = "warehouseInDO.warehouseCode")
    @Mapping(target = "status", source = "warehouseInDO.status")
    @Mapping(target = "inDate", source = "warehouseInDO.inDate")
    @Mapping(target = "sponsorId", source = "warehouseInDO.sponsorId")
    @Mapping(target = "sponsorName", source = "warehouseInDO.sponsorName")
    @Mapping(target = "handleDate", source = "warehouseInDO.handleDate")
    @Mapping(target = "remark", source = "warehouseInDO.remark")
    @Mapping(target = "items", source = "itemDOs")
    WarehouseIn toWarehouseIn(WarehouseInDO warehouseInDO, List<WarehouseInDetailDO> itemDOs);

    WarehouseInDO toWarehouseInDO(WarehouseIn warehouseIn);

    WarehouseInDetailDO toWarehouseInDetailDO(WarehouseInDetail item);

    List<WarehouseInDetailDO> toWarehouseInDetailDOs(List<WarehouseInDetail> items);

    WarehouseInDetail toWarehouseInDetailS(WarehouseInDetailDO itemDO);

    List<WarehouseInDetail> toWarehouseInDetailS(List<WarehouseInDetailDO> itemDOs);
}
