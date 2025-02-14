package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author wushaoqi
 * @date 2023-12-08-13:39
 */
@Data
public class MachineSurfaceTreatmentPartScanQuery {
    private String orderSerialNo;
    private String requirementSerialNo;
    @NotEmpty(message = "项目代码不为空")
    private String projectCode;
    @NotEmpty(message = "零件号不为空")
    private String partCode;
    private String partName;
    @NotEmpty(message = "零件版本不为空")
    private String partVersion;
    @NotEmpty(message = "仓库编码不为空")
    private String warehouseCode;
}
