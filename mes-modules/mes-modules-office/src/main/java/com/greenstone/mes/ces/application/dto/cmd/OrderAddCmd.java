package com.greenstone.mes.ces.application.dto.cmd;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderAddCmd {

    private boolean commit;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "请选择预计到货日期")
    private LocalDate expectReceiveDate;
    @NotNull(message = "请选择订购时间")
    private LocalDateTime purchaseDate;
    private String remark;
    @NotEmpty(message = "请添加订购的物品")
    @Valid
    private List<Item> items;

    @Data
    public static class Item {
        @NotEmpty(message = "请填写物品名称")
        private String itemName;
        @NotNull(message = "请填写采购数量")
        private Long itemNum;
        @NotEmpty(message = "请填写物品编码")
        private String itemCode;
        private String purchaseLink;
        private String specification;
        private String picturePath;
        private String unit;
        private Double unitPrice;
        private Double totalPrice;
        private String provider;
        private String applicationSerialNo;
        private String applicationItemId;
        private Long applicationNum;
    }

}
