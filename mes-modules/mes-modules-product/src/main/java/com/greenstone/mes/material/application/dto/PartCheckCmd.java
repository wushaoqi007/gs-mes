package com.greenstone.mes.material.application.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class PartCheckCmd {

    @NotEmpty(message = "加工单号不为空")
    private String worksheetCode;
    private Long number;
    @NotEmpty(message = "组件号不为空")
    private String componentCode;
    @NotEmpty(message = "项目号不为空")
    private String projectCode;
    @NotEmpty(message = "零件号不为空")
    private String partCode;
    private String partName;
    @NotEmpty(message = "零件版本不为空")
    private String partVersion;
}
