package com.greenstone.mes.material.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.material.event.data.StockOperationEventData;

import java.io.Serial;

public class StockOperationEvent extends BaseApplicationEvent<StockOperationEventData> {

    @Serial
    private static final long serialVersionUID = 1128389406199932835L;

    public StockOperationEvent(StockOperationEventData source) {
        super(source);
    }

}
