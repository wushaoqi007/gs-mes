package com.greenstone.mes.asset.application.event;

import com.greenstone.mes.asset.application.dto.cqe.event.AssetTypeEditE;
import com.greenstone.mes.common.event.BaseApplicationEvent;

import java.io.Serial;

public class AssetTypeEditEvent extends BaseApplicationEvent<AssetTypeEditE> {

    @Serial
    private static final long serialVersionUID = -1501224687660730987L;

    public AssetTypeEditEvent(AssetTypeEditE source) {
        super(source);
    }

}
