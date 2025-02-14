package com.greenstone.mes.machine.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.machine.application.dto.event.MachineCheckedTakeE;

public class MachineCheckedTakeEvent extends BaseApplicationEvent<MachineCheckedTakeE> {

    public MachineCheckedTakeEvent(MachineCheckedTakeE source) {
        super(source);
    }
}
