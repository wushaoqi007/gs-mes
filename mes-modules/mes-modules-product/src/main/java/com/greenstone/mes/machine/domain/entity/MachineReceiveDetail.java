package com.greenstone.mes.machine.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-12-08-9:27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineReceiveDetail {
    private String id;
    private String serialNo;
    private Integer operation;
    private String orderSerialNo;
    private String orderDetailId;
    private String requirementSerialNo;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long expectedNumber;
    private Long actualNumber;
    private String warehouseCode;

    private String provider;
}
