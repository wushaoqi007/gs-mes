package com.greenstone.mes.system.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author wushaoqi
 * @date 2024-03-11-16:18
 */
@Data
public class ParamDataAddCmd {
    private String id;
    @NotEmpty(message = "参数类型不为空")
    private String paramType;
    private String parentId;
    @NotEmpty(message = "参数值不为空")
    private String paramValue1;
    private String paramValue2;
    private String paramValue3;
    private String remark;
}
