package com.greenstone.mes.machine.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum MachineType {
    CHECK_TAKE(1, "质检取件"),
    CHECKED_TAKE(2, "合格品取件"),
    ;
    @EnumValue
    private final int type;

    private final String name;

    MachineType(int type, String name) {
        this.type = type;
        this.name = name;
    }
}
