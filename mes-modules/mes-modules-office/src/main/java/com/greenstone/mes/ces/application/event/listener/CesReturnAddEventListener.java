package com.greenstone.mes.ces.application.event.listener;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseInAddCmd;
import com.greenstone.mes.ces.application.dto.event.CesReturnAddE;
import com.greenstone.mes.ces.application.event.CesReturnAddEvent;
import com.greenstone.mes.ces.application.service.CesReturnService;
import com.greenstone.mes.ces.application.service.RequisitionService;
import com.greenstone.mes.ces.application.service.WarehouseInService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CesReturnAddEventListener implements ApplicationListener<CesReturnAddEvent> {

    private final CesReturnService returnService;
    private final RequisitionService requisitionService;
    private final WarehouseInService warehouseInService;

    public CesReturnAddEventListener(CesReturnService returnService, RequisitionService requisitionService, WarehouseInService warehouseInService) {
        this.returnService = returnService;
        this.requisitionService = requisitionService;
        this.warehouseInService = warehouseInService;
    }


    @Override
    public void onApplicationEvent(CesReturnAddEvent event) {
        CesReturnAddE eventData = event.getSource();
        // 修改领用单数量
        requisitionService.returnAddEvent(eventData);
        // 新建入库单
        List<WarehouseInAddCmd> warehouseInAddCmdList = returnService.createWarehouseIn(eventData.getSerialNo());
        if (CollUtil.isNotEmpty(warehouseInAddCmdList)) {
            for (WarehouseInAddCmd warehouseInAddCmd : warehouseInAddCmdList) {
                warehouseInService.add(warehouseInAddCmd);
            }
        }
    }

}