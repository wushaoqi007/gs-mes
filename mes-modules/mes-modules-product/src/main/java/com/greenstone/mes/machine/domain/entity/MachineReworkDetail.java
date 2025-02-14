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
public class MachineReworkDetail {

    private String id;
    private String serialNo;
    private String orderSerialNo;
    private String orderDetailId;
    private String checkSerialNo;
    private String checkDetailId;
    private String requirementSerialNo;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long processNumber;
    private Long reworkNumber;
    private String warehouseCode;
    private String provider;
}
