package com.greenstone.mes.machine.application.event.listener;

import com.greenstone.mes.machine.domain.service.MachineStockRecordManager;
import com.greenstone.mes.warehouse.domain.StockCmd;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class DoStockListener {

    private final MachineStockRecordManager machineStockRecordManager;

    @EventListener
    public void onApplicationEvent(StockCmd stockCmd) {
        // 保存出入库记录
        machineStockRecordManager.saveStockRecord(stockCmd);
    }

}
