package com.greenstone.mes.form.dto.cmd;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormDataSaveCmd {

    private String id;

    private ProcessStatus status;

    @NotBlank(message = "请指定表单ID")
    private String formId;

    private String dataJson;

}
