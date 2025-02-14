package com.greenstone.mes.ces.application.assembler;

import com.greenstone.mes.ces.application.dto.cmd.WarehouseInAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseInEditCmd;
import com.greenstone.mes.ces.application.dto.event.WarehouseStockE;
import com.greenstone.mes.ces.application.dto.result.WarehouseInResult;
import com.greenstone.mes.ces.domain.entity.WarehouseIn;
import com.greenstone.mes.ces.domain.entity.WarehouseInDetail;
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
public interface WarehouseInAssembler {
    WarehouseIn toWarehouseIn(WarehouseInAddCmd addCmd);

    WarehouseIn toWarehouseIn(WarehouseInEditCmd editCmd);

    WarehouseInResult toWarehouseInR(WarehouseIn warehouseIn);

    List<WarehouseInResult> toWarehouseInRs(List<WarehouseIn> warehouseIns);

    default WarehouseStockE toWarehouseInStockEvent(WarehouseIn warehouseIn) {
        WarehouseStockE warehouseStockE = WarehouseStockE.builder().
                operation(StockOperationType.IN).warehouseCode(warehouseIn.getWarehouseCode()).build();
        List<WarehouseStockE.Item> itemList = new ArrayList<>();
        warehouseStockE.setItems(itemList);
        for (WarehouseInDetail item : warehouseIn.getItems()) {
            itemList.add(WarehouseStockE.Item.builder().itemCode(item.getItemCode()).itemName(item.getItemName()).number(item.getInStockNum()).build());
        }
        return warehouseStockE;
    }
}
