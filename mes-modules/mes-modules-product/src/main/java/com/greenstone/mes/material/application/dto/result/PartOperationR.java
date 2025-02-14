package com.greenstone.mes.material.application.dto.result;

import lombok.*;

/**
 * @author wushaoqi
 * @date 2023-08-16-9:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PartOperationR {
    private String worksheetCode;
    private String projectCode;
    private String componentCode;
    private String partName;
    private String partCode;
    private String partVersion;
    private Long number;

    private Integer operation;

    private Long materialId;
    private Long warehouseId;
    private String warehouseName;
    private String remark;

}
