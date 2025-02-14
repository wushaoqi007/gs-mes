package com.greenstone.mes.ces.application.event;

import com.greenstone.mes.ces.application.dto.event.RequisitionAddE;
import com.greenstone.mes.common.event.BaseApplicationEvent;

import java.io.Serial;

public class RequisitionAddEvent extends BaseApplicationEvent<RequisitionAddE> {

    @Serial
    private static final long serialVersionUID = 4095556739005073321L;

    public RequisitionAddEvent(RequisitionAddE source) {
        super(source);
    }

}
