package com.greenstone.mes.machine.application.dto.cqe.query;

import com.greenstone.mes.machine.infrastructure.enums.CheckResultType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author wushaoqi
 * @date 2023-12-08-13:39
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineCheckPartScanQuery {
    @NotEmpty(message = "质检单号不为空")
    private String checkSerialNo;
    private String orderSerialNo;
    private String requirementSerialNo;
    @NotNull(message = "不支持此操作")
    private Integer operation;
    @NotEmpty(message = "项目代码不为空")
    private String projectCode;
    @NotEmpty(message = "零件号不为空")
    private String partCode;
    private String partName;
    @NotEmpty(message = "零件版本不为空")
    private String partVersion;
    @NotEmpty(message = "请选择仓库")
    private String warehouseCode;
    private CheckResultType checkResultType;
}
