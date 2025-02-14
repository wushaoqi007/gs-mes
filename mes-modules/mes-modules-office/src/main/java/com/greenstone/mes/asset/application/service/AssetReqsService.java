package com.greenstone.mes.asset.application.service;

import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetReqsCreateCmd;
import com.greenstone.mes.asset.application.dto.result.AssetReqsCreateR;
import com.greenstone.mes.asset.application.dto.result.AssetRequisitionR;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/6 13:40
 */

public interface AssetReqsService {

    List<AssetRequisitionR> list();

    AssetReqsCreateR create(AssetReqsCreateCmd saveCmd);

}
