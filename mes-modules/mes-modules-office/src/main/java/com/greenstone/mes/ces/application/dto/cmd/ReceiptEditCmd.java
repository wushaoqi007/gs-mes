package com.greenstone.mes.ces.application.dto.cmd;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class ReceiptEditCmd {

    private boolean commit;
    private String id;
    @NotNull(message = "请选择收货单")
    private String serialNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "请选择收货日期")
    private LocalDate receiveDate;
    private String remark;
    @NotEmpty(message = "请添加收货的物品")
    @Valid
    private List<Item> items;

    @Data
    public static class Item {
        @NotEmpty(message = "请填写物品名称")
        private String itemName;
        @NotNull(message = "请填写到货数量")
        private Long itemNum;
        @NotEmpty(message = "请填写物品编码")
        private String itemCode;
        private Long receivedNum;
        private String purchaseLink;
        private String specification;
        private String picturePath;
        private String unit;
        private Double unitPrice;
        private Double totalPrice;
        private String provider;
        private LocalDate invoiceDate;
        private String invoiceCode;
        private String remark;
        private String warehouseCode;
        private String orderSerialNo;
        private String orderItemId;
        private Long orderNum;
    }

}
