package com.greenstone.mes.external.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author gu_renkai
 * @date 2023/2/24 15:43
 */
@Data
public class ProcessSaveCmd {

    @NotBlank(message = "缺失表单信息")
    private String formId;

    private String formName;

    @NotBlank(message = "缺少流程编号")
    private String processCode;

    private String processName;

    @NotBlank(message = "缺少流程内容")
    private String jsonContent;

}
