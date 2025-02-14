package com.greenstone.mes.external.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessRevokeResult {

    private String formId;

    private boolean success;

    private boolean complete;

    private String serialNo;

    private String processDefinitionId;

    private String processInstanceId;

    private String errMsg;

}
