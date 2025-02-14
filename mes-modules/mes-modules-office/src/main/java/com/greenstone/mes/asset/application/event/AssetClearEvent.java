package com.greenstone.mes.asset.application.event;

import com.greenstone.mes.asset.application.dto.cqe.event.AssetClearE;
import com.greenstone.mes.common.event.BaseApplicationEvent;

import java.io.Serial;

public class AssetClearEvent extends BaseApplicationEvent<AssetClearE> {

    @Serial
    private static final long serialVersionUID = -1501224687660730987L;

    public AssetClearEvent(AssetClearE source) {
        super(source);
    }

}
