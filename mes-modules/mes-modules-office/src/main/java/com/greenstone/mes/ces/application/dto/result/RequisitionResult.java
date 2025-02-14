package com.greenstone.mes.ces.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class RequisitionResult {

    private String id;
    private String serialNo;
    private ProcessStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime requisitionDate;
    private String remark;
    private Long requisitionerId;
    private String requisitionerName;
    private String requisitionerNo;
    private List<Item> items;

    @Data
    public static class Item {
        private String id;
        private String serialNo;
        private String itemName;
        private String itemCode;
        private String typeName;
        private String specification;
        private Long requisitionNum;
        private Double unitPrice;
        private String unit;
        private Double totalPrice;
        private String needReturn;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime returnDate;
        private Long returnNum;
    }
}
