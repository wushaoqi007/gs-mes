package com.greenstone.mes.material.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.material.event.data.ConfirmEventData;

public class PartOrderConfirmEvent extends BaseApplicationEvent<ConfirmEventData> {

    public PartOrderConfirmEvent(ConfirmEventData source) {
        super(source);
    }

}
