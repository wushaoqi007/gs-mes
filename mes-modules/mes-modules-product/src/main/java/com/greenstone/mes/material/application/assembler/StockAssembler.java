package com.greenstone.mes.material.application.assembler;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.application.dto.*;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.converter.EnumConverter;
import com.greenstone.mes.material.event.data.StockEventData;
import com.greenstone.mes.material.event.data.StockOperationEventData;
import com.greenstone.mes.material.event.data.StockUpdateEventData;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import com.greenstone.mes.material.response.StockListResp;
import org.mapstruct.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = EnumConverter.class,
        imports = {List.class, StrUtil.class, StockAction.class}
)
public interface StockAssembler {

    Logger log = LoggerFactory.getLogger(StockAssembler.class);

    @Mapping(target = "material", source = "baseMaterial")
    @Mapping(target = "number", source = "stockListResp.number")
    @Mapping(target = "worksheetCode", source = "stockListResp.worksheetCode")
    @Mapping(target = "componentCode", source = "stockListResp.componentCode")
    OutStockCommand.OutStockMaterial toMaterialStockDetail(StockListResp stockListResp, BaseMaterial baseMaterial);

    /**
     * *******  StockOperationCommand -> InStockCommand
     */
    InStockCommand toInStockCommand(StockOperationCommand transferCommand);

    InStockCommand.InStockMaterial toInStockCommandMaterial(StockOperationCommand.TransferMaterial material);

    List<InStockCommand.InStockMaterial> toInStockCommandMaterialList(List<StockOperationCommand.TransferMaterial> materialList);

    /**
     * *******  StockOperationCommand -> OutStockCommand
     */
    OutStockCommand toOutStockCommand(StockOperationCommand transferCommand);

    OutStockCommand.OutStockMaterial toOutStockCommandMaterial(StockOperationCommand.TransferMaterial material);

    List<OutStockCommand.OutStockMaterial> toOutStockCommandMaterialList(List<StockOperationCommand.TransferMaterial> materialList);

    /**
     * *******  StockOperationCommand -> TransferStockCommand
     */
    @Mapping(target = "warehouseOut", source = "warehouse")
    @Mapping(target = "warehouseIn", source = "toWarehouse")
    TransferStockCommand toTransferStockCommand(StockOperationCommand transferCommand);

    TransferStockCommand.TransferMaterial toTransferStockCommandMaterial(StockOperationCommand.TransferMaterial material);

    TransferStockCommand.NgData toTransferStockCommandNgData(StockOperationCommand.NgData ngData);

    List<TransferStockCommand.TransferMaterial> toTransferStockCommandMaterialList(List<StockOperationCommand.TransferMaterial> materialList);

    /**
     * *******  TransferStockCommand -> StockOperationCommand
     */
    @InheritInverseConfiguration
    @Mapping(target = "action", expression = "java(StockAction.OUT)")
    StockOperationCommand toOutStockOperationCommand(TransferStockCommand transferStockCommand);

    /**
     * *******  TransferStockCommand -> StockOperationCommand
     */
    @Mapping(target = "warehouse", source = "warehouseIn")
    @Mapping(target = "action", expression = "java(StockAction.IN)")
    StockOperationCommand toInStockOperationCommand(TransferStockCommand transferStockCommand);

    StockOperationCommand.TransferMaterial toStockOperationCommandMaterial(TransferStockCommand.TransferMaterial material);

    List<StockOperationCommand.TransferMaterial> toStockOperationCommandMaterialListFromTransfer(List<TransferStockCommand.TransferMaterial> materialList);

    /**
     * *******  OutStockCommand -> StockOperationCommand
     */
    @Mapping(target = "action", expression = "java(StockAction.IN)")
    StockOperationCommand toInStockOperationCommand(OutStockCommand outStockCommand);

    StockOperationCommand.TransferMaterial toStockOperationCommandMaterial(OutStockCommand.OutStockMaterial material);

    List<StockOperationCommand.TransferMaterial> toStockOperationCommandMaterialListFromOut(List<OutStockCommand.OutStockMaterial> materialList);

    /**
     * *******  InStockCommand -> StockEventData
     */
    StockEventData toEventData(InStockCommand inStockCommand);

    StockEventData.StockDetail toInEventDataMaterial(InStockCommand.InStockMaterial material);

    List<StockEventData.StockDetail> toInEventDataMaterialList(List<InStockCommand.InStockMaterial> materialList);

    /**
     * *******  OutStockCommand -> StockEventData
     */
    StockEventData toEventData(OutStockCommand outStockCommand);

    StockEventData.StockDetail toOutEventDataMaterial(OutStockCommand.OutStockMaterial material);

    List<StockEventData.StockDetail> toOutEventDataMaterialList(List<OutStockCommand.OutStockMaterial> materialList);

    /**
     * *******  StockOperationCommand -> StockOperationEventData
     */
    StockOperationEventData toStockOperationEventData(StockOperationCommand transferCommand);

    StockOperationEventData.StockMaterial toStockOperationEventDataMaterial(StockOperationCommand.TransferMaterial TransferMaterial);

    List<StockOperationEventData.StockMaterial> toStockOperationEventDataMaterialList(List<StockOperationCommand.TransferMaterial> TransferMaterial);

    StockOperationEventData.NgData toStockOperationEventNgData(StockOperationCommand.NgData ngData);

    /**
     * *******  OutStockCommand -> StockOperationEventData
     */
    StockOperationEventData toStockOperationEventData2(OutStockCommand outStockCommand);

    StockOperationEventData.StockMaterial toStockOperationEventDataMaterial2(OutStockCommand.OutStockMaterial TransferMaterial);

    List<StockOperationEventData.StockMaterial> toStockOperationEventDataMaterialList2(List<OutStockCommand.OutStockMaterial> TransferMaterial);

    StockRecordSaveCommand toStockRecordSaveCommand(StockEventData stockEventData);

    StockRecordSaveCommand.StockDetail toStockRecordSaveCommandDetail(StockEventData.StockDetail stockDetail);

    List<StockRecordSaveCommand.StockDetail> toStockRecordSaveCommandDetailList(List<StockEventData.StockDetail> stockDetailList);


    default StockRecordSaveCommand toStockRecordUpdateCommand(StockUpdateEventData stockUpdateEventData) {
        StockRecordSaveCommand stockRecordSaveCommand = StockRecordSaveCommand.builder().action(StockAction.UPDATE)
                .warehouse(stockUpdateEventData.getWarehouse()).sponsor(stockUpdateEventData.getSponsor()).build();
        StockRecordSaveCommand.StockDetail stockDetail = StockRecordSaveCommand.StockDetail.builder()
                .material(stockUpdateEventData.getMaterial()).number(stockUpdateEventData.getNumber())
                .componentCode(stockUpdateEventData.getComponentCode()).worksheetCode(stockUpdateEventData.getWorksheetCode())
                .build();
        stockRecordSaveCommand.setMaterialList(Collections.singletonList(stockDetail));
        return stockRecordSaveCommand;
    }
}
