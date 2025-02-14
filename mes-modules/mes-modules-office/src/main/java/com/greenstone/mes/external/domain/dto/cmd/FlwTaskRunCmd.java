package com.greenstone.mes.external.domain.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author gu_renkai
 * @date 2023/2/24 15:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlwTaskRunCmd {

    private String processInstanceId;

    private String taskId;

    private String comment;

    private Map<String, Object> variables;

}
