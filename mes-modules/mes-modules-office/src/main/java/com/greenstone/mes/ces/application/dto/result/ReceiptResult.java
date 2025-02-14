package com.greenstone.mes.ces.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;


@Data
public class ReceiptResult {

    private String id;
    private String serialNo;
    private ProcessStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate receiveDate;
    private String remark;
    private Long receiveBy;
    private String receiveByName;
    private List<Item> items;

    @Data
    public static class Item {
        private String id;
        private String serialNo;
        private String orderSerialNo;
        private String orderItemId;
        private Long orderNum;
        private String itemName;
        private Long itemNum;
        private String itemCode;
        private String typeName;
        private Long receivedNum;
        private Double unitPrice;
        private String purchaseLink;
        private String specification;
        private String picturePath;
        private String unit;
        private Double totalPrice;
        private String provider;
        private LocalDate invoiceDate;
        private String invoiceCode;
        private String remark;
        private String warehouseCode;
        private String warehouseName;
    }
}
