package com.greenstone.mes.ces.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class CesClearResult {

    private String id;
    private String serialNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime clearDate;
    private String remark;
    private Long clearById;
    private String clearByName;
    private String clearByNo;
    private List<Item> items;

    @Data
    public static class Item {
        private String id;
        private String serialNo;
        private String itemName;
        private String itemCode;
        private String typeName;
        private String specification;
        private Long clearNum;
        private String warehouseCode;
    }
}
