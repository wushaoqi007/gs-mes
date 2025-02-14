package com.greenstone.mes.machine.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum RequirementPartType {
    NEW(1, "新制"),
    CANCEL(2, "取消"),
    UPDATE(3, "修改"),
    ;
    @EnumValue
    private final int code;

    private final String name;

    RequirementPartType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RequirementPartType getByCode(int code) {
        return Arrays.stream(RequirementPartType.values()).filter(s -> s.getCode() == code).findFirst().orElse(null);
    }

    public static RequirementPartType getByName(String name) {
        Optional<RequirementPartType> find = Arrays.stream(RequirementPartType.values()).filter(s -> s.getName().equals(name)).findFirst();
        if (find.isEmpty()) {
            throw new ServiceException("请填写正确的零件类型中的一种：新制、取消、修改");
        }
        return find.get();
    }
}
