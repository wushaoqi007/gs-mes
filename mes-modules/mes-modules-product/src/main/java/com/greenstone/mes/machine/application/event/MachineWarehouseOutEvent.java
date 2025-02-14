package com.greenstone.mes.machine.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.machine.application.dto.event.MachineWarehouseOutE;

import java.io.Serial;

public class MachineWarehouseOutEvent extends BaseApplicationEvent<MachineWarehouseOutE> {

    @Serial
    private static final long serialVersionUID = -1530537492803476961L;

    public MachineWarehouseOutEvent(MachineWarehouseOutE source) {
        super(source);
    }
}
