package com.greenstone.mes.asset.application.assembler;

import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetRepairAddCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetRepairEditCmd;
import com.greenstone.mes.asset.application.dto.result.AssetRepairR;
import com.greenstone.mes.asset.domain.converter.EnumConverter;
import com.greenstone.mes.asset.domain.entity.Asset;
import com.greenstone.mes.asset.domain.entity.AssetRepair;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {EnumConverter.class}
)
public interface AssetRepairAssembler {

    AssetRepair toAssetRepair(AssetRepairAddCmd insertCmd);

    AssetRepair toAssetRepair(AssetRepairEditCmd updateCmd);

    AssetRepairR toAssetRepairR(AssetRepair assetRepair);

    List<AssetRepairR> toAssetRepairRList(List<AssetRepair> assetRepairs);

    List<Asset> toRepairAssetListFromAdd(List<AssetRepairAddCmd.Asset> assets);

    List<Asset> toRepairAssetListFromEdit(List<AssetRepairEditCmd.Asset> assets);

    Asset toRepairAssetFromAdd(AssetRepairAddCmd.Asset asset);

    Asset toRepairAssetFromEdit(AssetRepairEditCmd.Asset asset);


}
