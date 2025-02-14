package com.greenstone.mes.ces.application.dto.event;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-30-10:16
 */
@Data
public class OrderAddE {

    @NotEmpty(message = "请添加订购的物品")
    private List<Item> items;

    @Data
    public static class Item {
        private Long purchasedNum;
        private String applicationItemId;
    }
}
