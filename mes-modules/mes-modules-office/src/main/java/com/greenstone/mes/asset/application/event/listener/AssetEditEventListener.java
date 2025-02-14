package com.greenstone.mes.asset.application.event.listener;

import com.greenstone.mes.asset.application.dto.cqe.event.AssetEditE;
import com.greenstone.mes.asset.application.event.AssetEditEvent;
import com.greenstone.mes.asset.application.service.AssetHandleLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class AssetEditEventListener implements ApplicationListener<AssetEditEvent> {

    private final AssetHandleLogService assetHandleLogService;

    @Override
    public void onApplicationEvent(AssetEditEvent event) {
        AssetEditE assetEditE = event.getSource();
        assetHandleLogService.editEvent(assetEditE);
    }

}