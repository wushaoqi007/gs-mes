package com.greenstone.mes.machine.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CheckResultType {
    QUALIFIED(1, "合格"),
    REWORK(2, "返工"),
    TREAT_SURFACE(3, "表处"),
    ;
    @EnumValue
    private final int code;

    private final String name;

    CheckResultType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static CheckResultType getByCode(int code) {
        return Arrays.stream(CheckResultType.values()).filter(s -> s.getCode() == code).findFirst().orElse(null);
    }
}
