package com.greenstone.mes.ces.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.ces.application.dto.event.ReceiptAddE;

import java.io.Serial;

public class ReceiptAddEvent extends BaseApplicationEvent<ReceiptAddE> {

    @Serial
    private static final long serialVersionUID = -6750063697727196549L;

    public ReceiptAddEvent(ReceiptAddE source) {
        super(source);
    }

}
