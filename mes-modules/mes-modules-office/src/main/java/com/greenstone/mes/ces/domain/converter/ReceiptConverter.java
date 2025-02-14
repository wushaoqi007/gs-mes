package com.greenstone.mes.ces.domain.converter;

import com.greenstone.mes.ces.domain.entity.Receipt;
import com.greenstone.mes.ces.domain.entity.ReceiptItem;
import com.greenstone.mes.ces.infrastructure.persistence.ReceiptDO;
import com.greenstone.mes.ces.infrastructure.persistence.ReceiptItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-25-9:58
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReceiptConverter {

    Receipt toReceipt(ReceiptDO receiptDO);

    @Mapping(target = "id", source = "receiptDO.id")
    @Mapping(target = "serialNo", source = "receiptDO.serialNo")
    @Mapping(target = "status", source = "receiptDO.status")
    @Mapping(target = "receiveDate", source = "receiptDO.receiveDate")
    @Mapping(target = "remark", source = "receiptDO.remark")
    @Mapping(target = "items", source = "itemDOs")
    @Mapping(target = "receiveBy", source = "receiptDO.receiveBy")
    @Mapping(target = "receiveByName", source = "receiptDO.receiveByName")
    Receipt toReceipt(ReceiptDO receiptDO, List<ReceiptItemDO> itemDOs);

    ReceiptDO toReceiptDO(Receipt receipt);

    ReceiptItemDO toReceiptItemDO(ReceiptItem item);

    List<ReceiptItemDO> toReceiptItemDOs(List<ReceiptItem> items);

    ReceiptItem toReceiptItemS(ReceiptItemDO itemDO);

    List<ReceiptItem> toReceiptItemS(List<ReceiptItemDO> itemDOs);
}
