package com.greenstone.mes.material.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.material.event.data.StockEventData;

import java.io.Serial;

public class StockEvent extends BaseApplicationEvent<StockEventData> {

    @Serial
    private static final long serialVersionUID = 1128389406199932835L;

    public StockEvent(StockEventData source) {
        super(source);
    }

}
