package com.greenstone.mes.ces.application.dto.event;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-30-15:16
 */
@Data
public class ReceiptAddE {

    private String serialNo;
    @NotEmpty(message = "请添加收货的物品")
    private List<Item> items;

    @Data
    public static class Item {
        private Long readyNum;
        private String applicationItemId;
        private String orderItemId;
    }
}
