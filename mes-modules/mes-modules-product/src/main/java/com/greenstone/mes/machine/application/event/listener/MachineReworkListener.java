package com.greenstone.mes.machine.application.event.listener;

import com.greenstone.mes.machine.application.event.MachineReworkEvent;
import com.greenstone.mes.machine.application.service.MachineReworkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MachineReworkListener implements ApplicationListener<MachineReworkEvent> {

    private final MachineReworkService reworkService;

    public MachineReworkListener(MachineReworkService reworkService) {
        this.reworkService = reworkService;
    }

    @Override
    public void onApplicationEvent(MachineReworkEvent event) {
        // 去返工零件转移操作
        reworkService.operationAfterRework(event.getSource());
    }
}
