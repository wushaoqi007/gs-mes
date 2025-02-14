package com.greenstone.mes.system.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * @author wushaoqi
 * @date 2024-03-11-16:18
 */
@Data
public class ParamAddCmd {
    private String id;
    @NotEmpty(message = "参数类型不为空")
    private String paramType;
    @NotEmpty(message = "参数名称不为空")
    private String paramName;
    private Boolean multilevel;
    private Integer levels;
    @Max(value = 1, message = "请使用正确的状态，0：正常；1：停用")
    @Min(value = 0, message = "请使用正确的状态，0：正常；1：停用")
    private String status;
    private String remark;
}
