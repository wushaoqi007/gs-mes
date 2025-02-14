package com.greenstone.mes.material.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-08-29-13:55
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartReceive {
    private Long id;

    private Long recordId;

    private Long materialId;

    private String worksheetCode;

    private String projectCode;

    private String componentCode;

    private String partCode;

    private String partVersion;

    private String partName;

    private Long number;

    private Boolean handle;

    private Long warehouseId;
}
