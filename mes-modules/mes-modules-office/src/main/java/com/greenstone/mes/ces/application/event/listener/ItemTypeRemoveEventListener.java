package com.greenstone.mes.ces.application.event.listener;

import com.greenstone.mes.ces.application.dto.event.ItemTypeRemoveE;
import com.greenstone.mes.ces.application.event.ItemTypeRemoveEvent;
import com.greenstone.mes.ces.application.service.ItemSpecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ItemTypeRemoveEventListener implements ApplicationListener<ItemTypeRemoveEvent> {

    private final ItemSpecService itemSpecService;

    public ItemTypeRemoveEventListener(ItemSpecService itemSpecService) {
        this.itemSpecService = itemSpecService;
    }

    @Override
    public void onApplicationEvent(ItemTypeRemoveEvent event) {
        ItemTypeRemoveE eventData = event.getSource();
        itemSpecService.typeRemoveEvent(eventData);
    }

}