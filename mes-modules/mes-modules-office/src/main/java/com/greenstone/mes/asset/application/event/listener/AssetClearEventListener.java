package com.greenstone.mes.asset.application.event.listener;

import com.greenstone.mes.asset.application.dto.cqe.event.AssetClearE;
import com.greenstone.mes.asset.application.event.AssetClearEvent;
import com.greenstone.mes.asset.application.service.AssetHandleLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class AssetClearEventListener implements ApplicationListener<AssetClearEvent> {

    private final AssetHandleLogService handleLogService;

    @Override
    public void onApplicationEvent(AssetClearEvent event) {
        AssetClearE eventData = event.getSource();
        handleLogService.clearEvent(eventData);
    }

}