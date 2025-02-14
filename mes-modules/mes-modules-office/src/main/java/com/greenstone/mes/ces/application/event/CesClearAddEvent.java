package com.greenstone.mes.ces.application.event;

import com.greenstone.mes.ces.application.dto.event.CesClearAddE;
import com.greenstone.mes.common.event.BaseApplicationEvent;

import java.io.Serial;

public class CesClearAddEvent extends BaseApplicationEvent<CesClearAddE> {

    @Serial
    private static final long serialVersionUID = 4095556739005073321L;

    public CesClearAddEvent(CesClearAddE source) {
        super(source);
    }

}
