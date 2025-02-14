package com.greenstone.mes.machine.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineStockPartR {
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private Long stockNumber;
}
