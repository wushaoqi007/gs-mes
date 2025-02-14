package com.greenstone.mes.asset.application.event.listener;

import com.greenstone.mes.asset.application.dto.cqe.event.AssetRevertE;
import com.greenstone.mes.asset.application.event.AssetRevertEvent;
import com.greenstone.mes.asset.application.service.AssetHandleLogService;
import com.greenstone.mes.asset.application.service.AssetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AssetRevertEventListener implements ApplicationListener<AssetRevertEvent> {

    private final AssetService assetService;
    private final AssetHandleLogService assetHandleLogService;

    public AssetRevertEventListener(AssetService assetService, AssetHandleLogService assetHandleLogService) {
        this.assetService = assetService;
        this.assetHandleLogService = assetHandleLogService;
    }

    @Override
    public void onApplicationEvent(AssetRevertEvent event) {
        AssetRevertE eventData = event.getSource();
        assetService.revertEvent(eventData);
        assetHandleLogService.revertEvent(eventData);
    }

}