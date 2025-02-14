package com.greenstone.mes.material.response;

import lombok.Data;

@Data
public class StockRecordSearchResp {

    private Long recordId;

    private String sponsor;

    private String operator;

    private String warehouseName;

    private Integer operation;

    private String remark;

    private String operationTime;

}
