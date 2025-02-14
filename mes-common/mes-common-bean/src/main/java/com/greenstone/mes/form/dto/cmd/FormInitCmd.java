package com.greenstone.mes.form.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormInitCmd {

    @NotBlank(message = "表单名称不能为空")
    private String formName;

    @NotBlank(message = "请选择所属菜单")
    private String parentMenuId;

    @NotBlank(message = "请选择图标")
    private String icon;

    private boolean usingProcess;

}
