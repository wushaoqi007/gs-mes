package com.greenstone.mes.ces.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2023-11-08-13:08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionItem {
    private String id;
    private String serialNo;
    private String itemName;
    private String itemCode;
    private String typeName;
    private String specification;
    private Long requisitionNum;
    private Double unitPrice;
    private String unit;
    private Double totalPrice;
    private String needReturn;
    private LocalDateTime returnDate;
    private Long returnNum;
    private Long lossNum;
    private String warehouseCode;
}
