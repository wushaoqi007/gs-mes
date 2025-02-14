package com.greenstone.mes.machine.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.machine.application.dto.event.MachineStockChangeE;

import java.io.Serial;

public class MachineStockChangeEvent extends BaseApplicationEvent<MachineStockChangeE> {

    @Serial
    private static final long serialVersionUID = -3806274890542602322L;

    public MachineStockChangeEvent(MachineStockChangeE source) {
        super(source);
    }
}
