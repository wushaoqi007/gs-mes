package com.greenstone.mes.asset.application.assembler;

import com.greenstone.mes.asset.application.dto.cqe.event.AssetRequisitionE;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetRevertE;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetTypeAddE;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetTypeEditE;
import com.greenstone.mes.asset.application.dto.result.AssetExportResult;
import com.greenstone.mes.asset.domain.converter.EnumConverter;
import com.greenstone.mes.asset.domain.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
public interface AssetEventAssembler {

    // AssetRequisitionEvent
    @Mapping(target = "billId", source = "id")
    AssetRequisitionE toAssetRequisitionEventData(AssetRequisition requisition);

    default List<Asset> toAssets(AssetRequisitionE eventData) {
        List<Asset> assets = new ArrayList<>();
        for (AssetReqsDetail asset : eventData.getAssets()) {
            assets.add(Asset.builder().receivedId(eventData.getReceivedId())
                    .receivedBy(eventData.getReceivedBy())
                    .billSn(eventData.getSerialNo())
                    .barCode(asset.getBarCode())
                    .receivedTime(eventData.getReceivedTime())
                    .build());
        }
        return assets;
    }

    // AssetRevertEventData
    AssetRevertE toAssetRevertEventData(AssetRevert revert);

    // AssetTypeEditEventData
    AssetTypeEditE toAssetTypeEditEventData(AssetType assetType);

    @Mapping(target = "typeId", source = "id")
    @Mapping(target = "typeHierarchy", source = "nameHierarchy")
    Asset toAssets(AssetTypeEditE eventData);

    // AssetTypeAddEventData
    AssetTypeAddE toAssetTypeAddEventData(AssetType assetType);

    @Mapping(target = "typeId", source = "id")
    @Mapping(target = "typeHierarchy", source = "nameHierarchy")
    Asset toAssets(AssetTypeAddE eventData);

    // AssetExportResult
    AssetExportResult toAssetExportResult(Asset asset);

    List<AssetExportResult> toAssetExportResults(List<Asset> assets);

}
