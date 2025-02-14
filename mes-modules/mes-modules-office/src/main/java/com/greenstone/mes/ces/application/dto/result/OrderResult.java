package com.greenstone.mes.ces.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class OrderResult {

    private String id;
    private String serialNo;
    private ProcessStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectReceiveDate;
    private String remark;
    private Long purchaserId;
    private String purchaserName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime purchaseDate;
    private List<Item> items;

    @Data
    public static class Item {
        private String id;

        private String serialNo;

        private String applicationSerialNo;

        private String applicationItemId;

        private Long applicationNum;

        private String itemName;

        private Long itemNum;

        private String itemCode;

        private Long receivedNum;

        private Double unitPrice;

        private String purchaseLink;

        private String specification;

        private String picturePath;

        private String unit;

        private Double totalPrice;

        private String provider;

    }
}
