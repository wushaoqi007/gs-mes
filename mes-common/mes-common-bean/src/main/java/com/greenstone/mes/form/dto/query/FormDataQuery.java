package com.greenstone.mes.form.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormDataQuery {

    @NotBlank(message = "缺少表单信息")
    private String formId;

    private String conditions;

    private String fuzzyFields;

    private String fuzzyKeyword;

    private String orderBy;

}
