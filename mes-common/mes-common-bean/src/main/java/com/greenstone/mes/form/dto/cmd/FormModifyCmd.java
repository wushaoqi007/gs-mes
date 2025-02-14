package com.greenstone.mes.form.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormModifyCmd {

    private String formId;

    private String formName;

    private String icon;

    private Boolean usingProcess;

    private String customJson;

}
