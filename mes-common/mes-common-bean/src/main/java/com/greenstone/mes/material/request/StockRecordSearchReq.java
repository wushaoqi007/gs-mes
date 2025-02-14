package com.greenstone.mes.material.request;

import lombok.Data;

import java.util.Date;

@Data
public class StockRecordSearchReq {

    private String sponsor;

    private Long warehouseId;

    private Date operationTimeFrom;

    private Date operationTimeTo;

}
