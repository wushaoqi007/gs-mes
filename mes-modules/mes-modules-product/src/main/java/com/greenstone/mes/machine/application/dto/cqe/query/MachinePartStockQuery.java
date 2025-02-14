package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-12-08-13:39
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachinePartStockQuery {
    private String projectCode;
    private String partCode;
    private String partName;
    private String warehouseName;
    private String warehouseCode;
    private Long warehouseId;
    private Long materialId;
    private Integer stage;
}
