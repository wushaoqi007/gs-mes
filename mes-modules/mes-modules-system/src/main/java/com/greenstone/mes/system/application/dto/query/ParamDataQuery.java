package com.greenstone.mes.system.application.dto.query;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author wushaoqi
 * @date 2024-03-11-15:41
 */
@Data
public class ParamDataQuery {
    @NotEmpty(message = "参数类型不为空")
    private String paramType;
    private String paramValue;
}
