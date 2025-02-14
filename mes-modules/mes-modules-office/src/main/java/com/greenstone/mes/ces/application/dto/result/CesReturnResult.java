package com.greenstone.mes.ces.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class CesReturnResult {

    private String id;
    private String serialNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime returnDate;
    private String remark;
    private Long returnById;
    private String returnByName;
    private String returnByNo;
    private List<Item> items;

    @Data
    public static class Item {
        private String id;
        private String serialNo;
        private String requisitionItemId;
        private String requisitionSerialNo;
        private String itemName;
        private String itemCode;
        private String typeName;
        private String specification;
        private Long returnNum;
        private Long lossNum;
        private String warehouseCode;
    }
}
