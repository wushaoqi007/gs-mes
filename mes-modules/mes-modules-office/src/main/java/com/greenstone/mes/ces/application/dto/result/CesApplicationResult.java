package com.greenstone.mes.ces.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/21 15:11
 */

@Data
public class CesApplicationResult {

    private String id;
    private String serialNo;
    private ProcessStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectReceiveDate;
    private String remark;
    private Long appliedBy;
    private String appliedByName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appliedTime;
    private List<Item> items;

    @Data
    public static class Item {
        private String id;
        private String serialNo;
        private String itemName;
        private Long itemNum;
        private String typeName;
        private String purchaseLink;
        private String specification;
        private String picturePath;
        private Double unitPrice;
        private Double estimatedCost;
        private String itemCode;
        private String unit;
        private Long purchasedNum;
        private Long providedNum;
    }
}
