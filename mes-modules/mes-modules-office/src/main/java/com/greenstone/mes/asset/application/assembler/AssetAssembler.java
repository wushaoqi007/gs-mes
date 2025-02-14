package com.greenstone.mes.asset.application.assembler;

import com.greenstone.mes.asset.application.dto.cqe.cmd.*;
import com.greenstone.mes.asset.application.dto.result.*;
import com.greenstone.mes.asset.domain.converter.EnumConverter;
import com.greenstone.mes.asset.domain.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/1/31 16:10
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {EnumConverter.class}
)
public interface AssetAssembler {

    // AssetCateListResp
    AssetTypeListR toAssetCateListResp(AssetType assetType);

    List<AssetTypeListR> toAssetCateListRespList(List<AssetType> assetTypeList);

    // AssetCateResp
    AssetTypeR toAssetCateResp(AssetType assetType);

    // AssetCateSaveCmd
    AssetType toAssetCate(AssetTypeSaveCmd saveCmd);

    // AssetSaveCmd
    @Mapping(target = "receivedId", source = "userId")
    Asset toAsset(AssetInsertCmd saveCmd);

    @Mapping(target = "receivedId", source = "userId")
    Asset toAsset(AssetUpdateCmd updateCmd);

    // AssetSpecListResp
    AssetSpec toAssetSpec(AssetSpecInsertCmd insertCmd);

    AssetSpec toAssetSpec(AssetSpecUpdateCmd updateCmd);

    AssetSpecListR toAssetSpecListResp(AssetSpec assetSpec);

    List<AssetSpecListR> toAssetSpecListRespList(List<AssetSpec> assetSpecs);

    // AssetRequisition
    AssetRequisition toAssetRequisition(AssetReqsCreateCmd saveCmd);

    Asset toReqsAsset(AssetReqsCreateCmd.Asset asset);

    List<Asset> toReqsAssetList(List<AssetReqsCreateCmd.Asset> assets);

    // AssetRevertCreateCmd
    AssetRevert toAssetRevert(AssetRevertCreateCmd createCmd);

    Asset toRevertAsset(AssetRevertCreateCmd.Asset asset);

    List<Asset> toRevertAssetList(List<AssetRevertCreateCmd.Asset> assets);

    // AssetR
    AssetR toAssetR(Asset asset);

    List<AssetR> toAssetRs(List<Asset> assets);

    // AssetRevertListR
    AssetRevertR toAssetRevertR(AssetRevert revert);

    List<AssetRevertR> toAssetRevertRs(List<AssetRevert> reverts);

    // AssetRequisitionR
    AssetRequisitionR toAssetRequisitionR(AssetRequisition requisition);

    List<AssetRequisitionR> toAssetRequisitionRs(List<AssetRequisition> requisitions);


    // AssetHandleLogR
    AssetHandleLogR toAssetHandleLogR(AssetHandleLog assetHandleLog);

    List<AssetHandleLogR> toAssetHandleLogRs(List<AssetHandleLog> assetHandleLogs);

}
