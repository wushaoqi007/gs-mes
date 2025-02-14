package com.greenstone.mes.external.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskRunCmd {

    private String processInstanceId;

    private String taskId;

    private String serialNo;

    private String comment;

    private Map<String, Object> variables;

}
