package com.greenstone.mes.material.response;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class StockRecordDetailListResp {

    private Long warehouseId;

    private String warehouseName;

    private Integer operation;

    private String number;

    private String unit;

    private String sponsor;

    private String applicant;
    private String applicantNo;

    private Date operationTime;

}
