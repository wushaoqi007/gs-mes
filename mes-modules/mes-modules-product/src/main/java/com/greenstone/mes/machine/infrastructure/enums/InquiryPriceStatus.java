package com.greenstone.mes.machine.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum InquiryPriceStatus {
    UNQUOTED_PRICE(0, "未询价"),
    QUOTED_PRICE(1, "已询价"),
    ORDERED(2, "已下单"),
    ;
    @EnumValue
    private final int code;

    private final String name;

    InquiryPriceStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
