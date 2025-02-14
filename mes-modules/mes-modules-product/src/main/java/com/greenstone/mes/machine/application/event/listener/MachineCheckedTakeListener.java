package com.greenstone.mes.machine.application.event.listener;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineSignCmd;
import com.greenstone.mes.machine.application.event.MachineCheckedTakeEvent;
import com.greenstone.mes.machine.application.service.MachineCheckedTakeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MachineCheckedTakeListener implements ApplicationListener<MachineCheckedTakeEvent> {

    private final MachineCheckedTakeService checkedTakeService;

    public MachineCheckedTakeListener(MachineCheckedTakeService checkedTakeService) {
        this.checkedTakeService = checkedTakeService;
    }

    @Override
    public void onApplicationEvent(MachineCheckedTakeEvent event) {
        // 合格品取件零件转移操作
        checkedTakeService.operationAfterCheckedTake(event.getSource());
        // 发送签字审批
//        checkedTakeService.sign(MachineSignCmd.builder().serialNo(event.getSource().getSerialNo()).build());
    }
}
