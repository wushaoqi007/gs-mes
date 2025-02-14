package com.greenstone.mes.external.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CopyAddCmd {

    private String processDefinitionId;

    private String processInstanceId;

    private String nodeId;

}
