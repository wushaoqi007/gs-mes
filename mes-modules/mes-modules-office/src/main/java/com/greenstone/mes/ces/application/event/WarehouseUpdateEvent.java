package com.greenstone.mes.ces.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.ces.application.dto.event.WarehouseUpdateE;

import java.io.Serial;

public class WarehouseUpdateEvent extends BaseApplicationEvent<WarehouseUpdateE> {

    @Serial
    private static final long serialVersionUID = 3062191654933751691L;

    public WarehouseUpdateEvent(WarehouseUpdateE source) {
        super(source);
    }

}
