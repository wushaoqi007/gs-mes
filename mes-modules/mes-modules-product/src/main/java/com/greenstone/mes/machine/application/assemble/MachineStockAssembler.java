package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.machine.application.dto.cqe.cmd.*;
import com.greenstone.mes.machine.application.dto.event.MachineStockE;
import com.greenstone.mes.machine.application.dto.event.MachineStockOperationE;
import com.greenstone.mes.machine.application.dto.result.*;
import com.greenstone.mes.material.domain.converter.EnumConverter;
import com.greenstone.mes.material.event.data.StockUpdateEventData;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import org.mapstruct.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = EnumConverter.class,
        imports = {List.class, StrUtil.class, StockAction.class}
)
public interface MachineStockAssembler {

    Logger log = LoggerFactory.getLogger(MachineStockAssembler.class);

    /**
     * *******  MachineStockOperationCommand -> MachineInStockCommand
     */
    MachineInStockCommand toInStockCommand(MachineStockOperationCommand transferCommand);

    MachineInStockCommand.InStockMaterial toInStockCommandMaterial(MachineStockOperationCommand.TransferMaterial material);

    List<MachineInStockCommand.InStockMaterial> toInStockCommandMaterialList(List<MachineStockOperationCommand.TransferMaterial> materialList);

    /**
     * *******  MachineStockOperationCommand -> MachineOutStockCommand
     */
    MachineOutStockCommand toOutStockCommand(MachineStockOperationCommand transferCommand);

    MachineOutStockCommand.OutStockMaterial toOutStockCommandMaterial(MachineStockOperationCommand.TransferMaterial material);

    List<MachineOutStockCommand.OutStockMaterial> toOutStockCommandMaterialList(List<MachineStockOperationCommand.TransferMaterial> materialList);

    /**
     * *******  MachineStockOperationCommand -> MachineTransferStockCommand
     */
    @Mapping(target = "warehouseOut", source = "warehouse")
    @Mapping(target = "warehouseIn", source = "toWarehouse")
    MachineTransferStockCommand toTransferStockCommand(MachineStockOperationCommand transferCommand);

    MachineTransferStockCommand.TransferMaterial toTransferStockCommandMaterial(MachineStockOperationCommand.TransferMaterial material);

    MachineTransferStockCommand.NgData toTransferStockCommandNgData(MachineStockOperationCommand.NgData ngData);

    List<MachineTransferStockCommand.TransferMaterial> toTransferStockCommandMaterialList(List<MachineStockOperationCommand.TransferMaterial> materialList);

    /**
     * *******  MachineTransferStockCommand -> MachineStockOperationCommand
     */
    @InheritInverseConfiguration
    @Mapping(target = "action", expression = "java(StockAction.OUT)")
    MachineStockOperationCommand toOutStockOperationCommand(MachineTransferStockCommand transferStockCommand);

    /**
     * *******  MachineTransferStockCommand -> MachineStockOperationCommand
     */
    @Mapping(target = "warehouse", source = "warehouseIn")
    @Mapping(target = "action", expression = "java(StockAction.IN)")
    MachineStockOperationCommand toInStockOperationCommand(MachineTransferStockCommand transferStockCommand);

    MachineStockOperationCommand.TransferMaterial toStockOperationCommandMaterial(MachineTransferStockCommand.TransferMaterial material);

    List<MachineStockOperationCommand.TransferMaterial> toStockOperationCommandMaterialListFromTransfer(List<MachineTransferStockCommand.TransferMaterial> materialList);

    /**
     * *******  MachineOutStockCommand -> MachineStockOperationCommand
     */
    @Mapping(target = "action", expression = "java(StockAction.IN)")
    MachineStockOperationCommand toInStockOperationCommand(MachineOutStockCommand outStockCommand);

    MachineStockOperationCommand.TransferMaterial toStockOperationCommandMaterial(MachineOutStockCommand.OutStockMaterial material);

    List<MachineStockOperationCommand.TransferMaterial> toStockOperationCommandMaterialListFromOut(List<MachineOutStockCommand.OutStockMaterial> materialList);

    /**
     * *******  MachineInStockCommand -> MachineStockE
     */
    MachineStockE toEventData(MachineInStockCommand inStockCommand);

    MachineStockE.StockDetail toInEventDataMaterial(MachineInStockCommand.InStockMaterial material);

    List<MachineStockE.StockDetail> toInEventDataMaterialList(List<MachineInStockCommand.InStockMaterial> materialList);

    /**
     * *******  MachineOutStockCommand -> MachineStockE
     */
    MachineStockE toEventData(MachineOutStockCommand outStockCommand);

    MachineStockE.StockDetail toOutEventDataMaterial(MachineOutStockCommand.OutStockMaterial material);

    List<MachineStockE.StockDetail> toOutEventDataMaterialList(List<MachineOutStockCommand.OutStockMaterial> materialList);

    /**
     * *******  MachineStockOperationCommand -> MachineStockOperationE
     */
    MachineStockOperationE toStockOperationEventData(MachineStockOperationCommand transferCommand);

    MachineStockOperationE.StockMaterial toStockOperationEventDataMaterial(MachineStockOperationCommand.TransferMaterial TransferMaterial);

    List<MachineStockOperationE.StockMaterial> toStockOperationEventDataMaterialList(List<MachineStockOperationCommand.TransferMaterial> TransferMaterial);

    MachineStockOperationE.NgData toStockOperationEventNgData(MachineStockOperationCommand.NgData ngData);

    /**
     * *******  MachineOutStockCommand -> MachineStockOperationE
     */
    MachineStockOperationE toStockOperationEventData2(MachineOutStockCommand outStockCommand);

    MachineStockOperationE.StockMaterial toStockOperationEventDataMaterial2(MachineOutStockCommand.OutStockMaterial TransferMaterial);

    List<MachineStockOperationE.StockMaterial> toStockOperationEventDataMaterialList2(List<MachineOutStockCommand.OutStockMaterial> TransferMaterial);

    MachineStockRecordSaveCommand toStockRecordSaveCommand(MachineStockE stockEventData);

    MachineStockRecordSaveCommand.StockDetail toStockRecordSaveCommandDetail(MachineStockE.StockDetail stockDetail);

    List<MachineStockRecordSaveCommand.StockDetail> toStockRecordSaveCommandDetailList(List<MachineStockE.StockDetail> stockDetailList);


    default MachineStockRecordSaveCommand toStockRecordUpdateCommand(StockUpdateEventData stockUpdateEventData) {
        MachineStockRecordSaveCommand stockRecordSaveCommand = MachineStockRecordSaveCommand.builder().action(StockAction.UPDATE)
                .warehouse(stockUpdateEventData.getWarehouse()).sponsor(stockUpdateEventData.getSponsor()).build();
        MachineStockRecordSaveCommand.StockDetail stockDetail = MachineStockRecordSaveCommand.StockDetail.builder()
                .material(stockUpdateEventData.getMaterial()).number(stockUpdateEventData.getNumber())
                .orderSerialNo(stockUpdateEventData.getWorksheetCode())
                .build();
        stockRecordSaveCommand.setMaterialList(Collections.singletonList(stockDetail));
        return stockRecordSaveCommand;
    }

    List<MachineStockExportR> toMachineStockERS(List<MachinePartStockR> listRealStockS);

    MachineStockExportR toMachineStockER(MachinePartStockR listRealStock);

    List<MachineStockWaitReceiveExportR> toMachineStockWaitReceiveERS(List<MachinePartStockR> listRealStocks);

    MachineStockWaitReceiveExportR toMachineStockWaitReceiveER(MachinePartStockR listRealStock);

    List<MachineStockCheckedExportR> toMachineStockCheckedERS(List<MachinePartStockR> listRealStocks);

    MachineStockCheckedExportR toMachineStockCheckedER(MachinePartStockR listRealStock);

    List<MachineStockReceivingExportR> toMachineStockReceivingERS(List<MachinePartStockR> listRealStocks);

    MachineStockReceivingExportR toMachineStockReceivingER(MachinePartStockR listRealStock);

    List<MachineStockTreatingExportR> toMachineStockTreatingERS(List<MachinePartStockR> listRealStocks);

    MachineStockTreatingExportR toMachineStockTreatingER(MachinePartStockR listRealStock);

    List<MachineStockStageExportR> toMachineStockStageERS(List<MachinePartStockR> listRealStock);

    MachineStockStageExportR toMachineStockStageER(MachinePartStockR listRealStock);
}
