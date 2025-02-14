package com.greenstone.mes.ces.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum WarehouseStatus {
    NORMAL(0, "正常"),
    DISABLE(1, "停用"),
    ;
    @EnumValue
    private final int status;

    private final String name;

    WarehouseStatus(int status, String name) {
        this.status = status;
        this.name = name;
    }
}
