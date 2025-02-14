package com.greenstone.mes.asset.domain.converter;

import com.greenstone.mes.asset.domain.entity.AssetRepair;
import com.greenstone.mes.asset.domain.entity.AssetRepairDetail;
import com.greenstone.mes.asset.infrastructure.persistence.AssetRepairDO;
import com.greenstone.mes.asset.infrastructure.persistence.AssetRepairDetailDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AssetRepairConverter {

    AssetRepairDO entity2Do(AssetRepair assetRepair);

    List<AssetRepairDO> entities2Dos(List<AssetRepair> assetRepairs);

    AssetRepair do2Entity(AssetRepairDO assetRepairDO);

    List<AssetRepair> dos2Entities(List<AssetRepairDO> assetRepairDOS);

    // repairDetail
    AssetRepairDetailDO detailEntity2Do(AssetRepairDetail assetRepairDetail);

    List<AssetRepairDetailDO> detailEntities2Dos(List<AssetRepairDetail> assetRepairDetails);

    AssetRepairDetail detailDo2Entity(AssetRepairDetailDO assetRepairDetailDO);

    List<AssetRepairDetail> detailDos2Entities(List<AssetRepairDetailDO> assetRepairDetailDOS);


}
