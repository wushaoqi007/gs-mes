package com.greenstone.mes.asset.application.service;

import com.greenstone.mes.asset.application.dto.cqe.event.AssetClearE;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetEditE;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetRequisitionE;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetRevertE;
import com.greenstone.mes.asset.application.dto.cqe.query.AssetHandleLogQuery;
import com.greenstone.mes.asset.application.dto.result.AssetHandleLogR;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/8 14:19
 */

public interface AssetHandleLogService {

    List<AssetHandleLogR> list(AssetHandleLogQuery query);

    void requisitionEvent(AssetRequisitionE event);

    void revertEvent(AssetRevertE event);

    void editEvent(AssetEditE event);

    void clearEvent(AssetClearE event);

}
