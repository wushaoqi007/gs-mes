package com.greenstone.mes.external.domain.entity;

import com.greenstone.mes.external.infrastructure.enums.FlowNodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gu_renkai
 * @date 2023/3/2 11:36
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeDefinition {

    private String nodeId;
    private FlowNodeType nodeType;
    private String paramsJson;
    private String processDefinitionId;
    private String formId;

}
