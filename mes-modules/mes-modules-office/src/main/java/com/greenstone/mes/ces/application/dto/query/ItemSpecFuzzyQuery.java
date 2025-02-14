package com.greenstone.mes.ces.application.dto.query;

import lombok.Data;

import java.util.List;

@Data
public class ItemSpecFuzzyQuery {

    private String key;

    private List<String> fields;

}
