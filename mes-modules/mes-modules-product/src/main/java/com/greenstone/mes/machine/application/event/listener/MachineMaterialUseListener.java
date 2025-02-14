package com.greenstone.mes.machine.application.event.listener;

import com.greenstone.mes.machine.application.event.MachineMaterialUseEvent;
import com.greenstone.mes.machine.application.service.MachineMaterialUseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MachineMaterialUseListener implements ApplicationListener<MachineMaterialUseEvent> {

    private final MachineMaterialUseService materialUseService;

    public MachineMaterialUseListener(MachineMaterialUseService materialUseService) {
        this.materialUseService = materialUseService;
    }

    @Override
    public void onApplicationEvent(MachineMaterialUseEvent event) {
        // 领用零件操作
        materialUseService.operationAfterMaterialUse(event.getSource());
    }
}
