package com.greenstone.mes.ces.application.event;

import com.greenstone.mes.ces.application.dto.event.CesReturnAddE;
import com.greenstone.mes.common.event.BaseApplicationEvent;

import java.io.Serial;

public class CesReturnAddEvent extends BaseApplicationEvent<CesReturnAddE> {


    @Serial
    private static final long serialVersionUID = -9167389809389422753L;

    public CesReturnAddEvent(CesReturnAddE source) {
        super(source);
    }

}
