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
public class FormDeleteCmd {

    @NotBlank(message = "请指定需要删除的表单")
    private String formId;

}
