package com.greenstone.mes.asset.application.service;

import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetRevertCreateCmd;
import com.greenstone.mes.asset.application.dto.result.AssetRevertCreateR;
import com.greenstone.mes.asset.application.dto.result.AssetRevertR;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/9 10:32
 */

public interface AssetRevertService {

    List<AssetRevertR> list();

    AssetRevertCreateR create(AssetRevertCreateCmd createCmd);

}
