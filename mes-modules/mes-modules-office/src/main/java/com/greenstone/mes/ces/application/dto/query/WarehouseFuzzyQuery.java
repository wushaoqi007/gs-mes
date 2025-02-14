package com.greenstone.mes.ces.application.dto.query;

import lombok.Data;

import java.util.List;

@Data
public class WarehouseFuzzyQuery {

    private String key;

    private List<String> fields;

    private String parentWarehouseCode;

}
