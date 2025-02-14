package com.greenstone.mes.ces.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class WarehouseOutResult {

    private String id;
    private String serialNo;
    private String warehouseCode;
    private String warehouseName;
    private ProcessStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate outDate;
    private Long recipientId;
    private String recipientName;
    private Long sponsorId;
    private String sponsorName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime handleDate;
    private String remark;
    private List<Item> items;

    @Data
    public static class Item {
        private String id;
        private String serialNo;
        private String applicationSerialNo;
        private String itemCode;
        private String itemName;
        private String specification;
        private String typeName;
        private String unit;
        private Long outStockNum;
        private Double unitPrice;
        private Double totalPrice;
        private String picturePath;
        private String remark;
    }
}
