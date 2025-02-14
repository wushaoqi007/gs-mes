package com.greenstone.mes.material.request;

import lombok.Data;

@Data
public class StockTotalListReq {

    private String materialName;

    private String materialCode;

    private Integer materialType;

    private String projectCode;

}
