package com.greenstone.mes.asset.application.service;

import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetClearCreateCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetClearRestoreCmd;
import com.greenstone.mes.asset.domain.entity.AssetClear;

import java.util.List;

public interface AssetClearService {

    List<AssetClear> clears();

    void save(AssetClearCreateCmd createCmd);

    void restore(AssetClearRestoreCmd restoreCmd);
}
