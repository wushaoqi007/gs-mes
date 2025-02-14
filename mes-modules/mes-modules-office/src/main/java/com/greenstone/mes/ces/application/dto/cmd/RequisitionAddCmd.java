package com.greenstone.mes.ces.application.dto.cmd;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequisitionAddCmd {

    private boolean commit;
    @NotNull(message = "请选择领用日期")
    private LocalDateTime requisitionDate;
    private String remark;
    @NotEmpty(message = "请添加领用物品")
    @Valid
    private List<Item> items;

    @Data
    public static class Item {
        @NotEmpty(message = "请填写物品名称")
        private String itemName;
        @NotEmpty(message = "请填写物品编码")
        private String itemCode;
        private String typeName;
        private String specification;
        @NotNull(message = "请填写领用数量")
        private Long requisitionNum;
        private Double unitPrice;
        private String unit;
        private Double totalPrice;
        @NotEmpty(message = "请填写仓库编码")
        private String warehouseCode;
    }

}
