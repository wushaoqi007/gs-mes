package com.greenstone.mes.ces.domain.converter;

import com.greenstone.mes.ces.domain.entity.Warehouse;
import com.greenstone.mes.ces.infrastructure.persistence.WarehouseDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-06-01-10:48
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WarehouseConverter {

    // Warehouse
    Warehouse toWarehouse(WarehouseDO warehouseDO);

    List<Warehouse> toWarehouseList(List<WarehouseDO> warehouseDO);

    WarehouseDO toWarehouseDO(Warehouse warehouse);

}
