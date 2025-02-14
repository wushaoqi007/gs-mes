package com.greenstone.mes.system.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表单定义查询结果
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormDefinitionVo {

    private String formId;

    private String formName;

    private String icon;

    private String fieldsJson;

    private String defaultJson;

    private String customJson;

    private Boolean usingProcess;

}
