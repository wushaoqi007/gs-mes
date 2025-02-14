package com.greenstone.mes.ces.application.dto.cmd;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CesClearAddCmd {

    private boolean commit;
    @NotNull(message = "请选择清理日期")
    private LocalDateTime clearDate;
    private String remark;
    @NotEmpty(message = "请添加清理物品")
    @Valid
    private List<Item> items;

    @Data
    public static class Item {
        private String itemName;
        @NotEmpty(message = "请填写物品编码")
        private String itemCode;
        private String typeName;
        private String specification;
        @NotNull(message = "请填写清理数量")
        private Long clearNum;
        @NotEmpty(message = "请填写仓库编码")
        private String warehouseCode;
    }

}
