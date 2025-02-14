package com.greenstone.mes.machine.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-12-18-11:27
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineStockChangeDetail {
    private String id;
    private String serialNo;
    private String projectCode;
    private Long materialId;
    private Long warehouseId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long stockNumber;
    private Long changeNumber;
    private String warehouseCode;
    private String remark;
}
