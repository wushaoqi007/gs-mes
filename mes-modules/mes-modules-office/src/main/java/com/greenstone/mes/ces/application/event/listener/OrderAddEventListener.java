package com.greenstone.mes.ces.application.event.listener;

import com.greenstone.mes.ces.application.dto.event.OrderAddE;
import com.greenstone.mes.ces.application.event.OrderAddEvent;
import com.greenstone.mes.ces.application.service.CesApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderAddEventListener implements ApplicationListener<OrderAddEvent> {

    private final CesApplicationService applicationService;

    public OrderAddEventListener(CesApplicationService applicationService) {
        this.applicationService = applicationService;
    }


    @Override
    public void onApplicationEvent(OrderAddEvent event) {
        OrderAddE eventData = event.getSource();
        applicationService.orderAddEvent(eventData);
    }

}