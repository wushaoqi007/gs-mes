package com.greenstone.mes.asset.application.event.listener;

import com.greenstone.mes.asset.application.dto.cqe.event.AssetTypeEditE;
import com.greenstone.mes.asset.application.event.AssetTypeEditEvent;
import com.greenstone.mes.asset.application.service.AssetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AssetTypeEditEventListener implements ApplicationListener<AssetTypeEditEvent> {

    private AssetService assetService;

    public AssetTypeEditEventListener(AssetService assetService) {
        this.assetService = assetService;
    }

    @Override
    public void onApplicationEvent(AssetTypeEditEvent event) {
        AssetTypeEditE eventData = event.getSource();
        assetService.typeEditEvent(eventData);
    }

}