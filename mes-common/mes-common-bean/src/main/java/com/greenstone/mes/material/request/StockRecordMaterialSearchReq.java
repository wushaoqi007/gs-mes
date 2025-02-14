package com.greenstone.mes.material.request;

import lombok.Data;

import java.util.Date;

@Data
public class StockRecordMaterialSearchReq {

    private String materialId;

    private String materialName;

    private String projectCode;

    private String warehouseId;

    private String warehouseCode;

    private String partCode;

    private String sponsor;

    private Date operationTimeFrom;

    private Date operationTimeTo;

    private Boolean simple;

}
