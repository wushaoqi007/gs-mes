package com.greenstone.mes.ces.application.event.listener;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseInAddCmd;
import com.greenstone.mes.ces.application.dto.event.ReceiptAddE;
import com.greenstone.mes.ces.application.event.ReceiptAddEvent;
import com.greenstone.mes.ces.application.service.CesApplicationService;
import com.greenstone.mes.ces.application.service.OrderService;
import com.greenstone.mes.ces.application.service.ReceiptService;
import com.greenstone.mes.ces.application.service.WarehouseInService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ReceiptAddEventListener implements ApplicationListener<ReceiptAddEvent> {

    private final CesApplicationService applicationService;
    private final ReceiptService receiptService;
    private final OrderService orderService;
    private final WarehouseInService warehouseInService;

    public ReceiptAddEventListener(CesApplicationService applicationService, ReceiptService receiptService,
                                   OrderService orderService, WarehouseInService warehouseInService) {
        this.applicationService = applicationService;
        this.receiptService = receiptService;
        this.orderService = orderService;
        this.warehouseInService = warehouseInService;
    }


    @Override
    public void onApplicationEvent(ReceiptAddEvent event) {
        ReceiptAddE eventData = event.getSource();
        // 修改申请单数量
        applicationService.receiptAddEvent(eventData);
        // 修改订单数量
        orderService.receiptAddEvent(eventData);
        // 新建入库单
        List<WarehouseInAddCmd> warehouseInAddCmdList = receiptService.createWarehouseIn(eventData.getSerialNo());
        if (CollUtil.isNotEmpty(warehouseInAddCmdList)) {
            for (WarehouseInAddCmd warehouseInAddCmd : warehouseInAddCmdList) {
                warehouseInService.add(warehouseInAddCmd);
            }
        }
    }

}