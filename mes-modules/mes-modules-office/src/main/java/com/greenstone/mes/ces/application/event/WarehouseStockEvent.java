package com.greenstone.mes.ces.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.ces.application.dto.event.WarehouseStockE;

import java.io.Serial;

public class WarehouseStockEvent extends BaseApplicationEvent<WarehouseStockE> {


    @Serial
    private static final long serialVersionUID = 397041630642366286L;

    public WarehouseStockEvent(WarehouseStockE source) {
        super(source);
    }

}
