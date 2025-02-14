package com.greenstone.mes.asset.application.service;

import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetSpecDeleteCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetSpecInsertCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetSpecUpdateCmd;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetTypeAddE;
import com.greenstone.mes.asset.application.dto.cqe.query.AssetSpecQuery;
import com.greenstone.mes.asset.application.dto.result.AssetSpecListR;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/3 15:25
 */

public interface AssetSpecService {

    List<AssetSpecListR> list(AssetSpecQuery query);

    void insert(AssetSpecInsertCmd insertCmd);

    void update(AssetSpecUpdateCmd updateCmd);

    void remove(AssetSpecDeleteCmd deleteCmd);

    void typeAddEvent(AssetTypeAddE addE);


}
