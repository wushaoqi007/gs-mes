package com.greenstone.mes.external.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/24 15:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskRunResult {

    private boolean success;

    private boolean approved;

    private String processDefinitionId;

    private String processInstanceId;

    private String taskId;

    private String taskDefinitionKey;

    private String nextTaskId;

    private String nextTaskDefinitionKey;

    private String errMsg;

}
