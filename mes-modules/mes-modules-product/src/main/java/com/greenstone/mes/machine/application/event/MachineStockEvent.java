package com.greenstone.mes.machine.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.machine.application.dto.event.MachineStockE;
import com.greenstone.mes.material.event.data.StockEventData;

import java.io.Serial;

public class MachineStockEvent extends BaseApplicationEvent<MachineStockE> {


    @Serial
    private static final long serialVersionUID = 195849064555702568L;

    public MachineStockEvent(MachineStockE source) {
        super(source);
    }

}
