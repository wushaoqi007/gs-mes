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
public class WarehouseInEditCmd {

    private boolean commit;
    private String id;
    @NotNull(message = "请选择入库单")
    private String serialNo;
    @NotEmpty(message = "请填写入库仓库")
    private String warehouseCode;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "请选择入库日期")
    private LocalDate inDate;
    @NotNull(message = "请选择经办日期")
    private LocalDateTime handleDate;
    private String remark;
    @NotEmpty(message = "请添加物品")
    @Valid
    private List<Item> items;

    @Data
    public static class Item {
        @NotEmpty(message = "请填写物品名称")
        private String itemName;
        @NotNull(message = "请填写入库数量")
        private Long inStockNum;
        private String receiptSerialNo;
        @NotEmpty(message = "请填写物品编码")
        private String itemCode;
        private String specification;
        private String typeName;
        private String unit;
        private Double unitPrice;
        private Double totalPrice;
        private String picturePath;
        private String remark;
    }

}
