package com.greenstone.mes.external.domain.dto.cmd;

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
public class FlwDeployCmd {

    private String deploymentKey;

    private String resourceName;

    private String processText;

    private String processKey;

    private String processName;


}
