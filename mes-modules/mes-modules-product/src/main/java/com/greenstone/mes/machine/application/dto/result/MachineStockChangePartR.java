package com.greenstone.mes.machine.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineStockChangePartR {
    private String id;
    private String serialNo;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long stockNumber;
    private String warehouseCode;
}
