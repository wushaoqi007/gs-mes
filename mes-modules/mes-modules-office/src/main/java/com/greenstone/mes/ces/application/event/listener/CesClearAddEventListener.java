package com.greenstone.mes.ces.application.event.listener;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseOutAddCmd;
import com.greenstone.mes.ces.application.dto.event.CesClearAddE;
import com.greenstone.mes.ces.application.event.CesClearAddEvent;
import com.greenstone.mes.ces.application.service.CesClearService;
import com.greenstone.mes.ces.application.service.WarehouseOutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CesClearAddEventListener implements ApplicationListener<CesClearAddEvent> {

    private final CesClearService cesClearService;
    private final WarehouseOutService warehouseOutService;

    public CesClearAddEventListener(CesClearService cesClearService, WarehouseOutService warehouseOutService) {
        this.cesClearService = cesClearService;
        this.warehouseOutService = warehouseOutService;
    }


    @Override
    public void onApplicationEvent(CesClearAddEvent event) {
        CesClearAddE eventData = event.getSource();
        // 新建出库单
        List<WarehouseOutAddCmd> warehouseOutAddCmdList = cesClearService.createWarehouseOut(eventData.getSerialNo());
        if (CollUtil.isNotEmpty(warehouseOutAddCmdList)) {
            for (WarehouseOutAddCmd warehouseOutAddCmd : warehouseOutAddCmdList) {
                warehouseOutService.add(warehouseOutAddCmd);
            }
        }
    }

}