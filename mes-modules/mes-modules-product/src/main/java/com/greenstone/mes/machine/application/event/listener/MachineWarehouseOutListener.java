package com.greenstone.mes.machine.application.event.listener;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineSignCmd;
import com.greenstone.mes.machine.application.event.MachineWarehouseOutEvent;
import com.greenstone.mes.machine.application.service.MachineWarehouseOutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MachineWarehouseOutListener implements ApplicationListener<MachineWarehouseOutEvent> {

    private final MachineWarehouseOutService warehouseOutService;

    public MachineWarehouseOutListener(MachineWarehouseOutService warehouseOutService) {
        this.warehouseOutService = warehouseOutService;
    }

    @Override
    public void onApplicationEvent(MachineWarehouseOutEvent event) {
        // 出库零件操作
        warehouseOutService.operationAfterWarehouseOut(event.getSource());
        // 签字
//        warehouseOutService.sign(MachineSignCmd.builder().serialNo(event.getSource().getSerialNo()).build());
    }
}
