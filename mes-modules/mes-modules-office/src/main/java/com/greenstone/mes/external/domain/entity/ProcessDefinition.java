package com.greenstone.mes.external.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gu_renkai
 * @date 2023/3/1 13:07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessDefinition {

    private String id;
    private String formId;
    private String formName;
    private String processDefinitionId;
    private String processDefinitionKey;
    private String processDefinitionName;
    private Integer version;
    private String jsonContent;
    private String xmlContent;

}
