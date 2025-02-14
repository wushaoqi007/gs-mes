package com.greenstone.mes.asset.application.service;

import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetRepairAddCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetRepairEditCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetRepairStatusChangeCmd;
import com.greenstone.mes.asset.application.dto.cqe.query.AssetFuzzyQuery;
import com.greenstone.mes.asset.application.dto.result.AssetRepairR;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-05-31-14:59
 */
public interface AssetRepairService {
    List<AssetRepairR> list(AssetFuzzyQuery query);

    AssetRepairR detail(String id);

    void save(AssetRepairAddCmd addCmd);

    void update(AssetRepairEditCmd editCmd);

    void remove(List<String> serialNos);

    void statusChange(AssetRepairStatusChangeCmd statusChangeCmd);

}
