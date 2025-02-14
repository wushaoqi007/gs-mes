package com.greenstone.mes.machine.application.event.listener;

import com.greenstone.mes.machine.application.event.MachineSurfaceTreatmentEvent;
import com.greenstone.mes.machine.application.service.MachineSurfaceTreatmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MachineSurfaceTreatmentListener implements ApplicationListener<MachineSurfaceTreatmentEvent> {

    private final MachineSurfaceTreatmentService surfaceTreatmentService;

    public MachineSurfaceTreatmentListener(MachineSurfaceTreatmentService surfaceTreatmentService) {
        this.surfaceTreatmentService = surfaceTreatmentService;
    }

    @Override
    public void onApplicationEvent(MachineSurfaceTreatmentEvent event) {
        // 去表处零件转移操作
        surfaceTreatmentService.doStockWhenTreatCommit(event.getSource());
    }
}
