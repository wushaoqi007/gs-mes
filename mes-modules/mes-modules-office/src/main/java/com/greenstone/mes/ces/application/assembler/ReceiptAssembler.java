package com.greenstone.mes.ces.application.assembler;

import com.greenstone.mes.ces.application.dto.cmd.ReceiptAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.ReceiptEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseInAddCmd;
import com.greenstone.mes.ces.application.dto.event.ReceiptAddE;
import com.greenstone.mes.ces.application.dto.result.ReceiptResult;
import com.greenstone.mes.ces.domain.entity.Receipt;
import com.greenstone.mes.ces.domain.entity.ReceiptItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-25-10:02
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReceiptAssembler {
    Receipt toReceipt(ReceiptAddCmd addCmd);

    Receipt toReceipt(ReceiptEditCmd editCmd);

    ReceiptResult toReceiptR(Receipt receipt);

    List<ReceiptResult> toReceiptRs(List<Receipt> receipts);

    ReceiptAddE toReceiptAddEvent(Receipt receipt);

    @Mapping(target = "readyNum", source = "itemNum")
    ReceiptAddE.Item toReceiptItemAddEvent(ReceiptItem item);

    @Mapping(target = "inStockNum", source = "itemNum")
    WarehouseInAddCmd.Item toWarehouseInAddCmdItem(ReceiptItem receiptItem);
}
