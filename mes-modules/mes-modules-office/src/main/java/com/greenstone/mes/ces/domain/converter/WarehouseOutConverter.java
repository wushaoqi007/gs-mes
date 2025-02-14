package com.greenstone.mes.ces.domain.converter;

import com.greenstone.mes.ces.domain.entity.WarehouseOut;
import com.greenstone.mes.ces.domain.entity.WarehouseOutDetail;
import com.greenstone.mes.ces.infrastructure.persistence.WarehouseOutDO;
import com.greenstone.mes.ces.infrastructure.persistence.WarehouseOutDetailDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-06-5-9:58
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WarehouseOutConverter {

    WarehouseOut toWarehouseOut(WarehouseOutDO warehouseOutDO);

    @Mapping(target = "id", source = "warehouseOutDO.id")
    @Mapping(target = "serialNo", source = "warehouseOutDO.serialNo")
    @Mapping(target = "warehouseCode", source = "warehouseOutDO.warehouseCode")
    @Mapping(target = "status", source = "warehouseOutDO.status")
    @Mapping(target = "outDate", source = "warehouseOutDO.outDate")
    @Mapping(target = "recipientId", source = "warehouseOutDO.recipientId")
    @Mapping(target = "recipientName", source = "warehouseOutDO.recipientName")
    @Mapping(target = "sponsorId", source = "warehouseOutDO.sponsorId")
    @Mapping(target = "sponsorName", source = "warehouseOutDO.sponsorName")
    @Mapping(target = "handleDate", source = "warehouseOutDO.handleDate")
    @Mapping(target = "remark", source = "warehouseOutDO.remark")
    @Mapping(target = "items", source = "itemDOs")
    WarehouseOut toWarehouseOut(WarehouseOutDO warehouseOutDO, List<WarehouseOutDetailDO> itemDOs);

    WarehouseOutDO toWarehouseOutDO(WarehouseOut warehouseOut);

    WarehouseOutDetailDO toWarehouseOutDetailDO(WarehouseOutDetail item);

    List<WarehouseOutDetailDO> toWarehouseOutDetailDOs(List<WarehouseOutDetail> items);

    WarehouseOutDetail toWarehouseOutDetailS(WarehouseOutDetailDO itemDO);

    List<WarehouseOutDetail> toWarehouseOutDetailS(List<WarehouseOutDetailDO> itemDOs);
}
