package com.greenstone.mes.ces.application.event.listener;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseOutAddCmd;
import com.greenstone.mes.ces.application.dto.event.RequisitionAddE;
import com.greenstone.mes.ces.application.event.RequisitionAddEvent;
import com.greenstone.mes.ces.application.service.RequisitionService;
import com.greenstone.mes.ces.application.service.WarehouseOutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RequisitionAddEventListener implements ApplicationListener<RequisitionAddEvent> {

    private final RequisitionService requisitionService;
    private final WarehouseOutService warehouseOutService;

    public RequisitionAddEventListener(RequisitionService requisitionService, WarehouseOutService warehouseOutService) {
        this.requisitionService = requisitionService;
        this.warehouseOutService = warehouseOutService;
    }


    @Override
    public void onApplicationEvent(RequisitionAddEvent event) {
        RequisitionAddE eventData = event.getSource();
        // 新建出库单
        List<WarehouseOutAddCmd> warehouseOutAddCmdList = requisitionService.createWarehouseOut(eventData.getSerialNo());
        if (CollUtil.isNotEmpty(warehouseOutAddCmdList)) {
            for (WarehouseOutAddCmd warehouseOutAddCmd : warehouseOutAddCmdList) {
                warehouseOutService.add(warehouseOutAddCmd);
            }
        }
    }

}