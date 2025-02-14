package com.greenstone.mes.reimbursement.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ExpenseType {
    TRAIN(1, "火车票"),
    TAXI(2, "的士票"),
    AIR(3, "飞机票"),
    STEAMER(4, "船票"),
    ;
    @EnumValue
    private final int code;

    private final String name;

    ExpenseType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
