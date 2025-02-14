package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineStockAllQuery {
    private String serialNo;
    private String requirementSerialNo;
    private String projectCode;
    private String partCode;
    private String partName;
    private String partVersion;
    private Integer operation;
    @NotEmpty(message = "请先选择仓库")
    private String warehouseCode;
}
