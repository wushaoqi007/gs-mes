package com.greenstone.mes.ces.application.dto.query;

import lombok.Data;

import java.util.List;

@Data
public class CesReturnFuzzyQuery {

    private String key;

    private List<String> fields;

}
