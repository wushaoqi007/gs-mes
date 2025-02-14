package com.greenstone.mes.office.application.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CustomParamSaveCmd {

    @NotBlank(message = "请指定模块编号")
    private String moduleCode;
    @NotBlank(message = "请指定参数名")
    private String paramKey;
    @NotBlank(message = "请指定参数值")
    private String paramValue;

}
