package com.greenstone.mes.material.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.material.domain.entity.ProcessOrder;

import java.io.Serial;

public class PartOrderSaveEvent extends BaseApplicationEvent<ProcessOrder> {

    @Serial
    private static final long serialVersionUID = -1472250430760036862L;

    public PartOrderSaveEvent(ProcessOrder source) {
        super(source);
    }

}
