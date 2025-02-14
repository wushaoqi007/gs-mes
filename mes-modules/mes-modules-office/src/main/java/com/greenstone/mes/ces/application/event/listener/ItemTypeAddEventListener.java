package com.greenstone.mes.ces.application.event.listener;

import com.greenstone.mes.ces.application.dto.event.ItemTypeAddE;
import com.greenstone.mes.ces.application.event.ItemTypeAddEvent;
import com.greenstone.mes.ces.application.service.ItemSpecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ItemTypeAddEventListener implements ApplicationListener<ItemTypeAddEvent> {

    private final ItemSpecService itemSpecService;

    public ItemTypeAddEventListener(ItemSpecService itemSpecService) {
        this.itemSpecService = itemSpecService;
    }

    @Override
    public void onApplicationEvent(ItemTypeAddEvent event) {
        ItemTypeAddE eventData = event.getSource();
        itemSpecService.typeAddEvent(eventData);
    }

}