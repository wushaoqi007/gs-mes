package com.greenstone.mes.ces.application.dto.cmd;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CesReturnAddCmd {

    @NotNull(message = "请选择归还日期")
    private LocalDateTime returnDate;
    private String remark;
    @NotEmpty(message = "请添加归还物品")
    @Valid
    private List<Item> items;

    @Data
    public static class Item {
        @NotEmpty(message = "请填写关联领用物品")
        private String requisitionItemId;
        @NotEmpty(message = "请填写关联领用单")
        private String requisitionSerialNo;
        @NotEmpty(message = "请填写物品名称")
        private String itemName;
        @NotEmpty(message = "请填写物品编码")
        private String itemCode;
        private String typeName;
        private String specification;
        @NotNull(message = "请填写归还数量")
        private Long returnNum;
        private Long lossNum;
        @NotEmpty(message = "请填写仓库编码")
        private String warehouseCode;
    }

}
