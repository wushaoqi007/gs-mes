package com.greenstone.mes.office.application.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CustomParamListQuery {

    @NotBlank(message = "请指定模块编号")
    private String moduleCode;

    private String paramKey;

}
