package com.greenstone.mes.material.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.material.event.data.StockUpdateEventData;

import java.io.Serial;

public class StockUpdateEvent extends BaseApplicationEvent<StockUpdateEventData> {


    @Serial
    private static final long serialVersionUID = -4292997240170832982L;

    public StockUpdateEvent(StockUpdateEventData source) {
        super(source);
    }

}
