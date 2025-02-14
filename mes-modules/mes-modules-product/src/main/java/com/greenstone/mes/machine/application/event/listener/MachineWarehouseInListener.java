package com.greenstone.mes.machine.application.event.listener;

import com.greenstone.mes.machine.application.event.MachineWarehouseInEvent;
import com.greenstone.mes.machine.application.service.MachineWarehouseInService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MachineWarehouseInListener implements ApplicationListener<MachineWarehouseInEvent> {

    private final MachineWarehouseInService warehouseInService;

    public MachineWarehouseInListener(MachineWarehouseInService warehouseInService) {
        this.warehouseInService = warehouseInService;
    }

    @Override
    public void onApplicationEvent(MachineWarehouseInEvent event) {
        // 入库零件操作
//        warehouseInService.operationAfterWarehouseIn(event.getSource());
    }
}
