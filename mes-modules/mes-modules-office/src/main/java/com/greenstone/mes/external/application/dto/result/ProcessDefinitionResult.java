package com.greenstone.mes.external.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gu_renkai
 * @date 2023/3/3 13:53
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessDefinitionResult {

    private String id;
    private String billType;
    private String billTypeName;
    private String processDefinitionId;
    private String processDefinitionName;
    private String version;
    private String jsonContent;

}
