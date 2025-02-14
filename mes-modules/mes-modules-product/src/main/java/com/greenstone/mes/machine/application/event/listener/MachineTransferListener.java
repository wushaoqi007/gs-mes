package com.greenstone.mes.machine.application.event.listener;

import com.greenstone.mes.machine.application.event.MachineTransferEvent;
import com.greenstone.mes.machine.application.service.MachineTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MachineTransferListener implements ApplicationListener<MachineTransferEvent> {

    private final MachineTransferService transferService;

    public MachineTransferListener(MachineTransferService transferService) {
        this.transferService = transferService;
    }

    @Override
    public void onApplicationEvent(MachineTransferEvent event) {
        // 调拨零件操作
        transferService.operationAfterTransfer(event.getSource());
    }
}
