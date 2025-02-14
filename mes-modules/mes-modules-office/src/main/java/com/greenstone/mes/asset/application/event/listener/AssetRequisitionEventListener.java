package com.greenstone.mes.asset.application.event.listener;

import com.greenstone.mes.asset.application.assembler.AssetEventAssembler;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetRequisitionE;
import com.greenstone.mes.asset.application.event.AssetRequisitionEvent;
import com.greenstone.mes.asset.application.service.AssetHandleLogService;
import com.greenstone.mes.asset.application.service.AssetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AssetRequisitionEventListener implements ApplicationListener<AssetRequisitionEvent> {

    private final AssetService assetService;

    private final AssetEventAssembler assetEventAssembler;

    private final AssetHandleLogService assetHandleLogService;

    public AssetRequisitionEventListener(AssetService assetService, AssetEventAssembler assetEventAssembler,
                                         AssetHandleLogService assetHandleLogService) {
        this.assetService = assetService;
        this.assetEventAssembler = assetEventAssembler;
        this.assetHandleLogService = assetHandleLogService;
    }

    @Override
    public void onApplicationEvent(AssetRequisitionEvent event) {
        AssetRequisitionE eventData = event.getSource();
        assetService.requisitionEvent(eventData);
        assetHandleLogService.requisitionEvent(eventData);
    }

}