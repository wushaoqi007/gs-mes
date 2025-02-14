package com.greenstone.mes.machine.application.event.listener;

import com.greenstone.mes.machine.application.event.MachineStockChangeEvent;
import com.greenstone.mes.machine.application.service.MachineStockChangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MachineStockChangeListener implements ApplicationListener<MachineStockChangeEvent> {

    private final MachineStockChangeService StockChangeService;

    public MachineStockChangeListener(MachineStockChangeService StockChangeService) {
        this.StockChangeService = StockChangeService;
    }

    @Override
    public void onApplicationEvent(MachineStockChangeEvent event) {
        // 变更零件操作
        StockChangeService.operationAfterStockChange(event.getSource());
    }
}
