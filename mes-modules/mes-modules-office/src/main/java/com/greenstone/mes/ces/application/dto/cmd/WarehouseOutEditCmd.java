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
public class WarehouseOutEditCmd {

    private boolean commit;
    private String id;
    @NotNull(message = "请选择出库单")
    private String serialNo;
    @NotEmpty(message = "请填写出库仓库")
    private String warehouseCode;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "请选择领用日期")
    private LocalDate outDate;
    @NotNull(message = "请选择经办日期")
    private LocalDateTime handleDate;
    private Long recipientId;
    private String recipientName;
    private String remark;
    @NotEmpty(message = "请添加物品")
    @Valid
    private List<Item> items;

    @Data
    public static class Item {
        @NotEmpty(message = "请填写物品名称")
        private String itemName;
        @NotNull(message = "请填写出库数量")
        private Long outStockNum;
        private String applicationSerialNo;
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
