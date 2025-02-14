package com.greenstone.mes.ces.application.assembler;

import com.greenstone.mes.ces.application.dto.cmd.RequisitionAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.RequisitionEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseOutAddCmd;
import com.greenstone.mes.ces.application.dto.result.RequisitionResult;
import com.greenstone.mes.ces.domain.entity.Requisition;
import com.greenstone.mes.ces.domain.entity.RequisitionItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RequisitionAssembler {
    Requisition toRequisition(RequisitionAddCmd addCmd);

    Requisition toRequisition(RequisitionEditCmd editCmd);

    RequisitionResult toRequisitionR(Requisition requisition);

    List<RequisitionResult> toRequisitionRs(List<Requisition> requisitions);

    @Mapping(target = "outStockNum", source = "requisitionNum")
    @Mapping(target = "requisitionSerialNo", source = "serialNo")
    WarehouseOutAddCmd.Item toWarehouseOutAddCmdItem(RequisitionItem requisitionItem);

//    RequisitionAddE toRequisitionAddEvent(Requisition requisition);
//
//    @Mapping(target = "purchasedNum", source = "itemNum")
//    RequisitionAddE.Item toRequisitionItemAddEvent(RequisitionItem item);
//
//    @Mapping(target = "items", source = "receiptAddEs")
//    Requisition toRequisitionFromReceiptE(ReceiptAddE eventData, List<ReceiptAddE.Item> receiptAddEs);
//
//    @Mapping(target = "id", source = "requisitionItemId")
//    @Mapping(target = "receivedNum", source = "readyNum")
//    RequisitionItem toRequisitionItemFromReceiptE(ReceiptAddE.Item receiptAddItemE);
}
