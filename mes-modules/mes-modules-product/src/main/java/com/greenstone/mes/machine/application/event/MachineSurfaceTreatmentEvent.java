package com.greenstone.mes.machine.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.machine.application.dto.event.MachineSurfaceTreatmentE;

import java.io.Serial;

public class MachineSurfaceTreatmentEvent extends BaseApplicationEvent<MachineSurfaceTreatmentE> {

    @Serial
    private static final long serialVersionUID = -6477947969311515300L;

    public MachineSurfaceTreatmentEvent(MachineSurfaceTreatmentE source) {
        super(source);
    }
}
