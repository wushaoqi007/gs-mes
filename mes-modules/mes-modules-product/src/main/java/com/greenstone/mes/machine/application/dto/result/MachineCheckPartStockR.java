package com.greenstone.mes.machine.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-11-29-15:26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineCheckPartStockR {
    private String checkDetailId;
    private String checkSerialNo;
    private String orderSerialNo;
    private String orderDetailId;
    private String requirementSerialNo;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long processNumber;
    private Long takeNumber;
    private Long stockNumber;
    private String warehouseCode;
    private String warehouseName;
    private Long warehouseId;
    private String designer;
    private String provider;
}
