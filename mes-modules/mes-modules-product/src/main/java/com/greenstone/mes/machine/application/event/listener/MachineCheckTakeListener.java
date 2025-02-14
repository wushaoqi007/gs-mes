package com.greenstone.mes.machine.application.event.listener;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineSignCmd;
import com.greenstone.mes.machine.application.event.MachineCheckTakeEvent;
import com.greenstone.mes.machine.application.service.MachineCheckTakeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MachineCheckTakeListener implements ApplicationListener<MachineCheckTakeEvent> {

    private final MachineCheckTakeService checkTakeService;

    public MachineCheckTakeListener(MachineCheckTakeService checkTakeService) {
        this.checkTakeService = checkTakeService;
    }

    @Override
    public void onApplicationEvent(MachineCheckTakeEvent event) {
        // 质检取件零件转移操作
        checkTakeService.operationAfterCheckTake(event.getSource());
        // 发送签字审批
//        checkTakeService.sign(MachineSignCmd.builder().serialNo(event.getSource().getSerialNo()).build());
    }
}
