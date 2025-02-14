
package com.greenstone.mes.machine.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineCheckTakeDetail {


    private String id;
    private String serialNo;
    private String orderSerialNo;
    private String orderDetailId;
    private String requirementSerialNo;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private String designer;
    private Long takeNumber;
    private String outWarehouseCode;
}
