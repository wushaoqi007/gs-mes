package com.greenstone.mes.machine.application.event.listener;

import com.greenstone.mes.machine.application.event.MachineCheckEvent;
import com.greenstone.mes.machine.application.service.MachineCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MachineCheckListener implements ApplicationListener<MachineCheckEvent> {

    private final MachineCheckService checkService;

    public MachineCheckListener(MachineCheckService checkService) {
        this.checkService = checkService;
    }

    @Override
    public void onApplicationEvent(MachineCheckEvent event) {
        // 质检取件零件转移操作
//        checkService.operationAfterCheck(event.getSource());
    }
}
