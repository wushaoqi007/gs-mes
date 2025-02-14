package com.greenstone.mes.form.domain.entity;

import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.form.domain.BaseFormDataEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CustomFormDataEntity extends BaseFormDataEntity {

    private String id;
    private String serialNo;
    private ProcessStatus status;
    private String formId;
    private String dataJson;
    private String processInstanceId;
    private String processDefinitionId;
    private Boolean deleted;
    private Long createById;
    private String createBy;
    private LocalDateTime createTime;
    private Long submitById;
    private String submitBy;
    private LocalDateTime submitTime;
    private Long updateById;
    private String updateBy;
    private LocalDateTime updateTime;

}
