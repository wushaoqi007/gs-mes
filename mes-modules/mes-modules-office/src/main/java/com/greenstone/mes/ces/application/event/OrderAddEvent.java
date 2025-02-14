package com.greenstone.mes.ces.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.ces.application.dto.event.OrderAddE;

import java.io.Serial;

public class OrderAddEvent extends BaseApplicationEvent<OrderAddE> {

    @Serial
    private static final long serialVersionUID = -2818493507457883484L;

    public OrderAddEvent(OrderAddE source) {
        super(source);
    }

}
