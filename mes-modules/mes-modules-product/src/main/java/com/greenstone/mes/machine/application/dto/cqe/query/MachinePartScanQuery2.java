package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author gurenkai
 * @date 2024-07-25
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachinePartScanQuery2 {
    @NotEmpty(message = "零件号不能为空")
    private String partCode;

    @NotEmpty(message = "零件版本不能为空")
    private String partVersion;

    @NotEmpty(message = "申请单号不能为空")
    private String requirementSerialNo;

    @NotEmpty(message = "项目代码不能为空")
    private String projectCode;

    @NotNull(message = "请指定扫码时的操作")
    private Integer partOperation;

    private String surfaceTreatmentSerialNo;


}
