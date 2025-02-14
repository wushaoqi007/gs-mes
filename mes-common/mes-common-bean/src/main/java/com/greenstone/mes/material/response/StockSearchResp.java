package com.greenstone.mes.material.response;

import lombok.Data;

import java.util.List;

@Data
public class StockSearchResp {

    private String materialCode;

    private String materialName;

    private String materialVersion;

    private String projectCode;

    private Long number;

    private String unit;

    private String warehouseName;

    private String warehouseAddress;

}
