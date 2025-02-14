package com.greenstone.mes.system.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberType {
    USER("user", "用户"),
    ROLE("role", "角色"),
    ;
    @EnumValue
    private String type;

    private String name;

}
