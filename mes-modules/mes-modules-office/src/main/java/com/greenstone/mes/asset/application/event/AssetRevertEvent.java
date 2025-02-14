package com.greenstone.mes.asset.application.event;

import com.greenstone.mes.asset.application.dto.cqe.event.AssetRevertE;
import com.greenstone.mes.common.event.BaseApplicationEvent;

import java.io.Serial;

public class AssetRevertEvent extends BaseApplicationEvent<AssetRevertE> {

    @Serial
    private static final long serialVersionUID = -1501224687660730987L;

    public AssetRevertEvent(AssetRevertE source) {
        super(source);
    }

}
