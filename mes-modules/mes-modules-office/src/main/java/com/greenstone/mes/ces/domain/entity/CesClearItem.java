package com.greenstone.mes.ces.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-11-08-13:08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CesClearItem {
    private String id;
    private String serialNo;
    private String itemName;
    private String itemCode;
    private String typeName;
    private String specification;
    private Long clearNum;
    private String warehouseCode;
}
