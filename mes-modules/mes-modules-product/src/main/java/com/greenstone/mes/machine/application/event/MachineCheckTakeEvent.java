package com.greenstone.mes.machine.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.machine.application.dto.event.MachineCheckTakeE;

public class MachineCheckTakeEvent extends BaseApplicationEvent<MachineCheckTakeE> {

    public MachineCheckTakeEvent(MachineCheckTakeE source) {
        super(source);
    }
}
