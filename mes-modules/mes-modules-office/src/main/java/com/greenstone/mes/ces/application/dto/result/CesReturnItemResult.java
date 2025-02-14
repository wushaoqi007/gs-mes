package com.greenstone.mes.ces.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class CesReturnItemResult {

    private String id;
    private String returnSerialNo;
    private String requisitionSerialNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime requisitionDate;
    private Long requisitionerId;
    private String requisitionerName;
    private String requisitionerNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime returnDate;
    private Long returnById;
    private String returnByName;
    private String returnByNo;
    private String itemName;
    private String itemCode;
    private String typeName;
    private String specification;
    private Long requisitionNum;
    private Long returnNum;
    private Long lossNum;
    private String warehouseCode;
}
