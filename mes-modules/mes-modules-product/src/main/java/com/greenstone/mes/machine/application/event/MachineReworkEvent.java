package com.greenstone.mes.machine.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.machine.application.dto.event.MachineReworkE;

import java.io.Serial;

public class MachineReworkEvent extends BaseApplicationEvent<MachineReworkE> {

    @Serial
    private static final long serialVersionUID = -6477947969311515300L;

    public MachineReworkEvent(MachineReworkE source) {
        super(source);
    }
}
