package com.greenstone.mes.asset.application.event;

import com.greenstone.mes.asset.application.dto.cqe.event.AssetEditE;
import com.greenstone.mes.common.event.BaseApplicationEvent;

import java.io.Serial;

public class AssetEditEvent extends BaseApplicationEvent<AssetEditE> {

    @Serial
    private static final long serialVersionUID = -1501224687660730987L;

    public AssetEditEvent(AssetEditE source) {
        super(source);
    }

}
