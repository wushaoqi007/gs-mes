package com.greenstone.mes.form.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormBriefResult {

    private String formId;
    private String formName;
    private String icon;
    private String fieldsJson;
    private String defaultJson;
    private String customJson;
    private boolean usingProcess;

}
