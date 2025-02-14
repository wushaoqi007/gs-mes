package com.greenstone.mes.form.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2023/3/3 13:40
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Form {

    private String formId;
    private String formName;
    private Integer formSource;
    private boolean usingProcess;
    private String icon;
    private String processDefinitionId;
    private String formDefinitionId;
    private String defaultJson;
    private String customJson;
    private String systemJson;
    private LocalDateTime createTime;
    private String createBy;
    private LocalDateTime updateTime;
    private String updateBy;

}
