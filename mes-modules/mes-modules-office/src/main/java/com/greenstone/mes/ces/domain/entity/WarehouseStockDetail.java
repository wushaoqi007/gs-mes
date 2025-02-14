package com.greenstone.mes.ces.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-06-05-13:19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseStockDetail {
    private Long id;
    private String warehouseCode;
    private String itemCode;
    private Long number;
    private String typeCode;
    private String typeName;
    private String itemName;
    private String specification;
    private Long maxSecureStock;
    private Long minSecureStock;
    private String remark;
}
