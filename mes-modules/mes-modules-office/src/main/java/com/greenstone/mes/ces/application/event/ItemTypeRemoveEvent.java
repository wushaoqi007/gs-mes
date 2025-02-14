package com.greenstone.mes.ces.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.ces.application.dto.event.ItemTypeRemoveE;

import java.io.Serial;

public class ItemTypeRemoveEvent extends BaseApplicationEvent<ItemTypeRemoveE> {


    @Serial
    private static final long serialVersionUID = 3411200455090463636L;

    public ItemTypeRemoveEvent(ItemTypeRemoveE source) {
        super(source);
    }

}
