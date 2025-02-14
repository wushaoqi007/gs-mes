package com.greenstone.mes.machine.application.event.listener;

import com.greenstone.mes.machine.application.assemble.MachineStockAssembler;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockRecordSaveCommand;
import com.greenstone.mes.machine.application.dto.event.MachineStockE;
import com.greenstone.mes.machine.application.event.MachineStockEvent;
import com.greenstone.mes.machine.domain.service.MachineStockRecordManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class MachineStockListener implements ApplicationListener<MachineStockEvent> {

    private final MachineStockRecordManager stockRecordManager;
    private final MachineStockAssembler stockAssembler;

    @Override
    public void onApplicationEvent(MachineStockEvent event) {
        MachineStockE stockEventData = event.getSource();
        MachineStockRecordSaveCommand stockRecordSaveCommand = stockAssembler.toStockRecordSaveCommand(stockEventData);
        // 保存出入库记录
        stockRecordManager.saveStockRecord(stockRecordSaveCommand);
    }

}
