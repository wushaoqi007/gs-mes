package com.greenstone.mes.ces.application.dto.cmd;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseInAddCmd {

    private boolean commit;
    private boolean autoCreate;
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
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        @NotEmpty(message = "请填写物品名称")
        private String itemName;
        @NotNull(message = "请填写入库数量")
        private Long inStockNum;
        private String receiptSerialNo;
        private String returnSerialNo;
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
