package com.greenstone.mes.ces.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 入库单明细-物品列表
 *
 * @author wushaoqi
 * @date 2023-06-2-9:47
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseInDetail {

    private String id;
    private String serialNo;
    private String receiptSerialNo;
    private String returnSerialNo;
    private String itemCode;
    private String itemName;
    private String specification;
    private String typeName;
    private String unit;
    private Long inStockNum;
    private Double unitPrice;
    private Double totalPrice;
    private String picturePath;
    private String remark;
}
