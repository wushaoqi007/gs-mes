package com.greenstone.mes.material.request;

import lombok.Data;

@Data
public class StockSearchReq {

    private Long warehouseId;

    private String materialName;

    private String materialCode;

    private String projectCode;

}
