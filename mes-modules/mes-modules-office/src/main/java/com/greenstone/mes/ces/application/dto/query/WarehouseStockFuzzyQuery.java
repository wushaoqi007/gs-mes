package com.greenstone.mes.ces.application.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseStockFuzzyQuery {

    private String key;

    private List<String> fields;

    private String warehouseCode;

    private String filter;

    private String typeCode;

}
