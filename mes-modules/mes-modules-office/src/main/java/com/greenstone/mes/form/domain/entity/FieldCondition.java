package com.greenstone.mes.form.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FieldCondition {

    private String field;

    private String label;

    private String option;

    private String type;

    private String value;



}
