package com.greenstone.mes.asset.application.event;

import com.greenstone.mes.asset.application.dto.cqe.event.AssetRequisitionE;
import com.greenstone.mes.common.event.BaseApplicationEvent;

import java.io.Serial;

public class AssetRequisitionEvent extends BaseApplicationEvent<AssetRequisitionE> {

    @Serial
    private static final long serialVersionUID = -1501224687660730987L;

    public AssetRequisitionEvent(AssetRequisitionE source) {
        super(source);
    }

}
