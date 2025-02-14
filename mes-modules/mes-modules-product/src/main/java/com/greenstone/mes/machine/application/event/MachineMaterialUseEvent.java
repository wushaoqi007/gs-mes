package com.greenstone.mes.machine.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.machine.application.dto.event.MachineMaterialUseE;

import java.io.Serial;

public class MachineMaterialUseEvent extends BaseApplicationEvent<MachineMaterialUseE> {

    @Serial
    private static final long serialVersionUID = 410716569912200447L;

    public MachineMaterialUseEvent(MachineMaterialUseE source) {
        super(source);
    }
}
