package com.greenstone.mes.form.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gu_renkai
 * @date 2023/3/3 13:40
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormDefinition {

    private String formId;
    private String formName;
    private String menuId;
    private String moduleId;
    private String defaultJson;
    private String customJson;
    private String serialNoPrefix;
    private boolean usingProcess;
    private String processDefinitionId;

}
