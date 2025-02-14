package com.greenstone.mes.questionnaire.application.dto.cqe;

import lombok.Data;

import java.util.List;

@Data
public class FuzzyQuery {

    private String key;

    private List<String> fields;

}
