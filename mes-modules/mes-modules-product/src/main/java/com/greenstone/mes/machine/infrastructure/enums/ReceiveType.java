package com.greenstone.mes.machine.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ReceiveType {
    NORMAL(1, "正常"),
    SURFACE(7, "表处"),
    REWORK(9, "返工"),
    ;
    @EnumValue
    private final int code;

    private final String name;

    ReceiveType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ReceiveType getByCode(int code) {
        return Arrays.stream(ReceiveType.values()).filter(s -> s.getCode() == code).findFirst().orElse(null);
    }
}
