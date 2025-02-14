package com.greenstone.mes.ces.domain.converter;

import com.greenstone.mes.ces.domain.entity.WarehouseStock;
import com.greenstone.mes.ces.infrastructure.persistence.WarehouseStockDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-06-05-13:20
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WarehouseStockConverter {
    WarehouseStock toWarehouseStock(WarehouseStockDO warehouseStockDO);
    List<WarehouseStock> toWarehouseStockS(List<WarehouseStockDO> warehouseStockDOS);

    WarehouseStockDO toWarehouseStockDO(WarehouseStock warehouseStock);
}
