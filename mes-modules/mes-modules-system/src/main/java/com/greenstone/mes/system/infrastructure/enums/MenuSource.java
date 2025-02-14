package com.greenstone.mes.system.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gu_renkai
 * @date 2023/3/3 10:02
 */
@Getter
@AllArgsConstructor
public enum MenuSource {
    SYSTEM(1, "系统"),
    CUSTOM(2, "自定义"),
    STORE(3, "应用商店"),
    ;
    @EnumValue
    private final int source;

    private final String name;

}
