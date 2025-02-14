package com.greenstone.mes.machine.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.machine.application.dto.event.MachineTransferE;

public class MachineTransferEvent extends BaseApplicationEvent<MachineTransferE> {


    public MachineTransferEvent(MachineTransferE source) {
        super(source);
    }
}
