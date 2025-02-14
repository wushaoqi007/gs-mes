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
public class MachineUnknownPartScanQuery {
    private String serialNo;
    private String requirementSerialNo;
    @NotEmpty(message = "项目代码不为空")
    private String projectCode;
    @NotEmpty(message = "零件号不为空")
    private String partCode;
    private String partName;
    @NotEmpty(message = "零件版本不为空")
    private String partVersion;
    private Integer operation;
    private String warehouseCode;
}
