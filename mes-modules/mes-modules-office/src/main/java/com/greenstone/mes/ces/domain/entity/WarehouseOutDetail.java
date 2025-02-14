package com.greenstone.mes.ces.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 出库单明细-物品列表
 *
 * @author wushaoqi
 * @date 2023-06-5-9:47
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseOutDetail {

    private String id;
    private String serialNo;
    private String applicationSerialNo;
    private String requisitionSerialNo;
    private String clearSerialNo;
    private String itemCode;
    private String itemName;
    private String specification;
    private String typeName;
    private String unit;
    private Long outStockNum;
    private Double unitPrice;
    private Double totalPrice;
    private String picturePath;
    private String remark;
}
