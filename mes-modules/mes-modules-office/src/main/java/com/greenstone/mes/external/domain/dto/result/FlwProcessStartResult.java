package com.greenstone.mes.external.domain.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gu_renkai
 * @date 2023/2/24 15:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlwProcessStartResult {

    private String processInstanceId;

    private String taskId;

}
