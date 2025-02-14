package com.greenstone.mes.asset.infrastructure.mapper;

import com.greenstone.mes.asset.application.dto.cqe.query.AssetFuzzyQuery;
import com.greenstone.mes.asset.domain.entity.AssetRepair;
import com.greenstone.mes.asset.infrastructure.persistence.AssetRepairDO;
import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepairMapper extends EasyBaseMapper<AssetRepairDO> {

    List<AssetRepair> selectByFuzzyQuery(AssetFuzzyQuery query);

}
