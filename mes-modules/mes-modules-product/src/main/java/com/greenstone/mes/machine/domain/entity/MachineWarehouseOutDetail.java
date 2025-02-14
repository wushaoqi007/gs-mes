package com.greenstone.mes.machine.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-12-28-14:02
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineWarehouseOutDetail {
    private String id;
    private String serialNo;
    private String orderSerialNo;
    private String orderDetailId;
    private String requirementSerialNo;
    private String projectCode;
    private Long materialId;
    private Long warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long outStockNumber;
    private Integer type;
    private Integer classification;
}
