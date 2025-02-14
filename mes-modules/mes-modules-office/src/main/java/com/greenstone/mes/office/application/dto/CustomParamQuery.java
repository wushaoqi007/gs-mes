package com.greenstone.mes.office.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomParamQuery {

    @NotBlank(message = "请指定模块编号")
    private String moduleCode;
    @NotBlank(message = "请指定参数名")
    private String paramKey;

}
