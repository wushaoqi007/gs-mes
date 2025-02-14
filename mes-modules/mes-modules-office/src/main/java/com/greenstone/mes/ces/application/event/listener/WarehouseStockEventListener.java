package com.greenstone.mes.ces.application.event.listener;

import com.greenstone.mes.ces.application.dto.event.WarehouseStockE;
import com.greenstone.mes.ces.application.event.WarehouseStockEvent;
import com.greenstone.mes.ces.application.service.WarehouseStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WarehouseStockEventListener implements ApplicationListener<WarehouseStockEvent> {

    private final WarehouseStockService warehouseStockService;

    public WarehouseStockEventListener(WarehouseStockService warehouseStockService) {
        this.warehouseStockService = warehouseStockService;
    }

    @Override
    public void onApplicationEvent(WarehouseStockEvent event) {
        WarehouseStockE eventData = event.getSource();
        warehouseStockService.transfer(eventData);
    }

}