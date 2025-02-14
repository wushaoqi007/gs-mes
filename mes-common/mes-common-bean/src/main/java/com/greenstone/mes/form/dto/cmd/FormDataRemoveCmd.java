package com.greenstone.mes.form.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormDataRemoveCmd {

    @NotBlank(message = "请指定表单ID")
    private String formId;

    @NotEmpty(message = "至少选择一条记录")
    private List<String> ids;

}
