package com.greenstone.mes.asset.domain.converter;

import com.greenstone.mes.asset.application.dto.cqe.query.AssetTypeTreeListQuery;
import com.greenstone.mes.asset.domain.entity.*;
import com.greenstone.mes.asset.infrastructure.persistence.*;
import org.mapstruct.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/23 11:08
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AssetConverter {

    // AssetCate
    AssetTypeDO toAssetTypeDO(AssetType assetType);

    AssetTypeDO toAssetTypeDO(AssetTypeTreeListQuery query);

    List<AssetType> toAssetTypeList(List<AssetTypeDO> doList);

    AssetType toAssetType(AssetTypeDO assetTypeDO);

    // AssetSpecification
    AssetSpec toAssetSpecification(AssetSpecDO assetSpecDO);

    List<AssetSpec> toAssetSpecList(List<AssetSpecDO> assetSpecDO);

    AssetSpecDO toSpecInsertDO(AssetSpec specification);

    @Mapping(target = "typeCode", ignore = true)
    AssetSpecDO toSpecUpdateDO(AssetSpec specification);

    // Asset
    AssetDO toAssetDO(Asset asset);

    @InheritInverseConfiguration
    Asset toAsset(AssetDO assetDO);

    List<Asset> toAssetList(List<AssetDO> assetDOList);

    // AssetReqsDO
    AssetReqsDO toAssetReqsDO(AssetRequisition requisition);

    AssetRequisition toAssetRequisition(AssetReqsDO requisition);

    List<AssetRequisition> toAssetRequisitionList(List<AssetReqsDO> reqsDOList);

    // AssetRevertDO
    AssetRevertDO toAssetRevertDO(AssetRevert revert);

    AssetRevert toAssetRevert(AssetRevertDO revertDO);

    List<AssetRevert> toAssetRevertList(List<AssetRevertDO> revertDOList);

    // AssetHandleLogDO
    AssetHandleLogDO toAssetHandleLogDO(AssetHandleLog assetHandleLog);

    List<AssetHandleLogDO> toAssetHandleLogDOList(List<AssetHandleLog> assetHandleLogList);

    AssetHandleLog toAssetHandleLog(AssetHandleLogDO assetHandleLogDO);

    List<AssetHandleLog> toAssetHandleLogs(List<AssetHandleLogDO> assetHandleLogDOList);

    // AssetClear
    AssetClearDO toAssetClearDO(AssetClear assetClear);

    AssetClear toAssetClear(AssetClearDO assetClearDO);

}
