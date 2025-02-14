package com.greenstone.mes.ces.application.assembler;

import com.greenstone.mes.ces.application.dto.event.WarehouseStockE;
import com.greenstone.mes.ces.domain.entity.WarehouseStock;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-06-05-13:29
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WarehouseStockAssembler {

    default List<WarehouseStock> toWarehouseStockTransfers(WarehouseStockE stockE) {
        List<WarehouseStock> stockList = new ArrayList<>();
        for (WarehouseStockE.Item item : stockE.getItems()) {
            WarehouseStock warehouseStock = WarehouseStock.builder().
                    warehouseCode(stockE.getWarehouseCode()).
                    itemCode(item.getItemCode()).
                    number(item.getNumber()).build();
            stockList.add(warehouseStock);
        }
        return stockList;
    }
}
