package com.greenstone.mes.material.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockRecordMaterialSearchResp {

    private String materialName;

    private String materialCode;

    private String materialVersion;

    private Integer materialType;

    private String unit;

    private Long number;

    private String projectCode;

    private String warehouseName;

    private String warehouseCode;

    private Integer operation;

    private Integer stage;

    private Integer action;

    private Integer behavior;

    private String sponsor;

    private String applicant;

    private String applicantNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;

    private String remark;

}
