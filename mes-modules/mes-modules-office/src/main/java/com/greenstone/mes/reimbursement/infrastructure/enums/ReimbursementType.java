package com.greenstone.mes.reimbursement.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ReimbursementType {
    TRAVEL(1, "出差"),
    ;
    @EnumValue
    private final int code;

    private final String name;

    ReimbursementType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
