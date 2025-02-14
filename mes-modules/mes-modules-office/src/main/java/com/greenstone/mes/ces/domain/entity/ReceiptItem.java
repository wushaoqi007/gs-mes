package com.greenstone.mes.ces.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 收货单物品
 *
 * @author wushaoqi
 * @date 2023-05-25-9:47
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptItem {

    private String id;
    private String serialNo;
    private String orderSerialNo;
    private String orderItemId;
    private Long orderNum;
    private String applicationItemId;
    private String itemName;
    private Long itemNum;
    private String itemCode;
    private String typeName;
    private Long receivedNum;
    private Double unitPrice;
    private String purchaseLink;
    private String specification;
    private String picturePath;
    private String unit;
    private Double totalPrice;
    private String provider;
    private LocalDate invoiceDate;
    private String invoiceCode;
    private String remark;
    private String warehouseCode;
    private String warehouseName;
}
