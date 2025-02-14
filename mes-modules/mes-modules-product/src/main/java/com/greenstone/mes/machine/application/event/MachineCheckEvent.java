package com.greenstone.mes.machine.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.machine.application.dto.event.MachineCheckE;

import java.io.Serial;

public class MachineCheckEvent extends BaseApplicationEvent<MachineCheckE> {

    @Serial
    private static final long serialVersionUID = -6477947969311515300L;

    public MachineCheckEvent(MachineCheckE source) {
        super(source);
    }
}
