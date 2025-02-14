package com.greenstone.mes.material.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-09-05-13:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockUpdateR {
    private Long materialId;
    private Long warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private String worksheetCode;
    private Long number;
    private String componentCode;
    private String projectCode;
    private String partCode;
    private String partName;
    private String partVersion;
}
