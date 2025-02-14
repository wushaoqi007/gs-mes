package com.greenstone.mes.machine.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineTransferDetail {
    private String id;
    private String serialNo;
    private Long materialId;
    private String outWarehouseCode;
    private String inWarehouseCode;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long number;
    private String remark;
}
