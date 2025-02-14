package com.greenstone.mes.form.dto.result;

import com.greenstone.mes.form.domain.BaseFormDataEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseFormDataDataResult extends BaseFormDataEntity {

    private String processDefinitionId;
    private boolean deleted;
    private String submitByName;
    private LocalDateTime updateTime;

}
