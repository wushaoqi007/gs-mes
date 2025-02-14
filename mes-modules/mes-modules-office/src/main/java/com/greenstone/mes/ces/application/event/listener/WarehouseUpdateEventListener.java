package com.greenstone.mes.ces.application.event.listener;

import com.greenstone.mes.ces.application.dto.event.WarehouseUpdateE;
import com.greenstone.mes.ces.application.event.WarehouseUpdateEvent;
import com.greenstone.mes.ces.application.service.WarehouseInService;
import com.greenstone.mes.ces.application.service.WarehouseOutService;
import com.greenstone.mes.ces.application.service.WarehouseStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WarehouseUpdateEventListener implements ApplicationListener<WarehouseUpdateEvent> {

    private final WarehouseStockService stockService;
    private final WarehouseInService warehouseInService;
    private final WarehouseOutService warehouseOutService;

    public WarehouseUpdateEventListener(WarehouseStockService stockService, WarehouseInService warehouseInService, WarehouseOutService warehouseOutService) {
        this.stockService = stockService;
        this.warehouseInService = warehouseInService;
        this.warehouseOutService = warehouseOutService;
    }


    @Override
    public void onApplicationEvent(WarehouseUpdateEvent event) {
        WarehouseUpdateE eventData = event.getSource();
        stockService.warehouseUpdateEvent(eventData);
        warehouseInService.warehouseUpdateEvent(eventData);
        warehouseOutService.warehouseUpdateEvent(eventData);
    }

}