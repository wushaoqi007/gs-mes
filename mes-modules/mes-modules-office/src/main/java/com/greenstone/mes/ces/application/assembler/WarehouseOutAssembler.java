package com.greenstone.mes.ces.application.assembler;

import com.greenstone.mes.ces.application.dto.cmd.WarehouseOutAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseOutEditCmd;
import com.greenstone.mes.ces.application.dto.event.WarehouseStockE;
import com.greenstone.mes.ces.application.dto.result.WarehouseOutResult;
import com.greenstone.mes.ces.domain.entity.WarehouseOut;
import com.greenstone.mes.ces.domain.entity.WarehouseOutDetail;
import com.greenstone.mes.ces.infrastructure.enums.StockOperationType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WarehouseOutAssembler {
    WarehouseOut toWarehouseOut(WarehouseOutAddCmd addCmd);

    WarehouseOut toWarehouseOut(WarehouseOutEditCmd editCmd);

    WarehouseOutResult toWarehouseOutR(WarehouseOut warehouseOut);

    List<WarehouseOutResult> toWarehouseOutRs(List<WarehouseOut> warehouseOuts);

    default WarehouseStockE toWarehouseOutStockEvent(WarehouseOut warehouseOut) {
        WarehouseStockE warehouseStockE = WarehouseStockE.builder().
                operation(StockOperationType.OUT).warehouseCode(warehouseOut.getWarehouseCode()).build();
        List<WarehouseStockE.Item> itemList = new ArrayList<>();
        warehouseStockE.setItems(itemList);
        for (WarehouseOutDetail item : warehouseOut.getItems()) {
            itemList.add(WarehouseStockE.Item.builder().itemCode(item.getItemCode()).itemName(item.getItemName()).number(item.getOutStockNum()).build());
        }
        return warehouseStockE;
    }
}
