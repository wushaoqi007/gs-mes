package com.greenstone.mes.asset.application.event.listener;

import com.greenstone.mes.asset.application.dto.cqe.event.AssetTypeAddE;
import com.greenstone.mes.asset.application.event.AssetTypeAddEvent;
import com.greenstone.mes.asset.application.service.AssetService;
import com.greenstone.mes.asset.application.service.AssetSpecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AssetTypeAddEventListener implements ApplicationListener<AssetTypeAddEvent> {

    private final AssetService assetService;
    private final AssetSpecService assetSpecService;

    public AssetTypeAddEventListener(AssetService assetService, AssetSpecService assetSpecService) {
        this.assetService = assetService;
        this.assetSpecService = assetSpecService;
    }

    @Override
    public void onApplicationEvent(AssetTypeAddEvent event) {
        AssetTypeAddE eventData = event.getSource();
        assetService.typeAddEvent(eventData);
        assetSpecService.typeAddEvent(eventData);
    }

}