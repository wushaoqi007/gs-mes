package com.greenstone.mes.form.dto.result;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormCommitResult {

    private String formId;
    private String id;
    private String serialNo;
    private String processInstanceId;
    private String formDefinitionId;
    private ProcessStatus status;

}
