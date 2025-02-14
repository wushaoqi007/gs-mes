package com.greenstone.mes.material.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author gu_renkai
 * @date 2022/12/19 16:04
 */
@Getter
public enum CheckResult {
    NG(0, "NG"),
    OK(1, "OK"),
    ;
    @EnumValue
    private final int id;

    private final String name;

    CheckResult(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CheckResult getById(int id) {
        return Arrays.stream(CheckResult.values()).filter(s -> s.getId() == id).findFirst().orElse(null);
    }
}
