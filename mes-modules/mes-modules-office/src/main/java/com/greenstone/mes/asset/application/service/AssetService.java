package com.greenstone.mes.asset.application.service;

import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetDeleteCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetImportCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetInsertCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetUpdateCmd;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetRequisitionE;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetRevertE;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetTypeAddE;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetTypeEditE;
import com.greenstone.mes.asset.application.dto.cqe.query.AssetFuzzyQuery;
import com.greenstone.mes.asset.application.dto.result.AssetExportResult;
import com.greenstone.mes.asset.domain.entity.Asset;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/1/31 17:05
 */

public interface AssetService {

    List<Asset> list();

    List<Asset> queryMyAsset(AssetFuzzyQuery query);

    List<Asset> fuzzyQuery(AssetFuzzyQuery query);

    void insert(AssetInsertCmd insertCmd);

    void update(AssetUpdateCmd updateCmd);

    void remove(AssetDeleteCmd deleteCmd);

    void requisitionEvent(AssetRequisitionE eventData);

    void revertEvent(AssetRevertE eventData);

    void typeEditEvent(AssetTypeEditE eventData);

    void typeAddEvent(AssetTypeAddE eventData);

    List<AssetExportResult> exportResults(AssetFuzzyQuery query);

    void importAssets(List<AssetImportCmd> importCmds);
}
