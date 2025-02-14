package com.greenstone.mes.ces.application.dto.query;

import lombok.Data;

import java.util.List;

@Data
public class CesClearFuzzyQuery {

    private String key;

    private List<String> fields;

}
