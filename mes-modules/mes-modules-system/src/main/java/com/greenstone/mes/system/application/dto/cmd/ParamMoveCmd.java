package com.greenstone.mes.system.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ParamMoveCmd {

    @NotBlank(message = "缺少参数类型信息")
    private String paramType;

    @NotBlank(message = "缺少参数层级信息")
    private String parentId;

    @NotBlank(message = "缺少参数数据信息")
    private String dataId;

    @NotBlank(message = "缺少排序信息")
    private Integer orderNum;

}
