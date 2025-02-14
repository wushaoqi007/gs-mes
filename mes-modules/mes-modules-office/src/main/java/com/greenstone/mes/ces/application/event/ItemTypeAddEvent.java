package com.greenstone.mes.ces.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.ces.application.dto.event.ItemTypeAddE;

import java.io.Serial;

public class ItemTypeAddEvent extends BaseApplicationEvent<ItemTypeAddE> {


    @Serial
    private static final long serialVersionUID = -7720076096454304277L;

    public ItemTypeAddEvent(ItemTypeAddE source) {
        super(source);
    }

}
