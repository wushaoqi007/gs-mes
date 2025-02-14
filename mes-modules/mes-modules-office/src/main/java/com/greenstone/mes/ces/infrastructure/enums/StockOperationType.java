package com.greenstone.mes.ces.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author wushaoqi
 * @date 2023-06-05-13:36
 */
public enum StockOperationType {
    IN(0, "入库"),
    OUT(1, "出库"),
    ;
    @EnumValue
    @Getter
    private final int code;

    @Getter
    private final String name;

    StockOperationType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
