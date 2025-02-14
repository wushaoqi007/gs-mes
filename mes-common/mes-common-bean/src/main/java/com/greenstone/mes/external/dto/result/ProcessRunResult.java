package com.greenstone.mes.external.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessRunResult {

    private String formId;

    private String formName;

    private String serviceName;

    private boolean success;

    private boolean complete;

    private boolean approved;

    private String serialNo;

    private String comment;

    private String processDefinitionId;

    private String processInstanceId;

    private String currentTaskId;

    private String nextTaskId;

    private String errMsg;

}
