package com.greenstone.mes.ces.application.dto.event;

import com.greenstone.mes.ces.infrastructure.enums.StockOperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-06-05-13:41
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseStockE {

    @NotNull(message = "请填写操作类型")
    private StockOperationType operation;
    @NotEmpty(message = "请填写仓库")
    private String warehouseCode;
    @NotEmpty(message = "请添加物品")
    private List<Item> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private String itemCode;
        private String itemName;
        @NotEmpty(message = "请填写物品数量")
        private Long number;
    }
}
