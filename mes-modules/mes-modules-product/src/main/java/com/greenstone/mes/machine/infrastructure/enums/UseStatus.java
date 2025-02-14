package com.greenstone.mes.machine.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum UseStatus {
    NONE(0, "未领取"),
    ALL(1, "已领取"),
    SOME(2, "部分领取"),
    ;
    @EnumValue
    private final int code;

    private final String name;

    UseStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
