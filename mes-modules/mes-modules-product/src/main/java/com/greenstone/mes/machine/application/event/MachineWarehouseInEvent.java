package com.greenstone.mes.machine.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.machine.application.dto.event.MachineWarehouseInE;

import java.io.Serial;

public class MachineWarehouseInEvent extends BaseApplicationEvent<MachineWarehouseInE> {

    @Serial
    private static final long serialVersionUID = 4140464836089012453L;

    public MachineWarehouseInEvent(MachineWarehouseInE source) {
        super(source);
    }
}
