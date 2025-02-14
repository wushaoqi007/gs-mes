package com.greenstone.mes.asset.application.service;

import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetTypeSaveCmd;
import com.greenstone.mes.asset.application.dto.cqe.query.AssetTypeQuery;
import com.greenstone.mes.asset.application.dto.result.AssetTypeListR;
import com.greenstone.mes.asset.application.dto.result.AssetTypeR;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/1/31 15:37
 */

public interface AssetTypeService {

    List<AssetTypeListR> list();

    AssetTypeR find(AssetTypeQuery query);

    void save(AssetTypeSaveCmd saveCmd);

    void remove(String typeCode);

}
