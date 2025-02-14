package com.greenstone.mes.oa.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 宿舍成员状态
 */
@RequiredArgsConstructor
@Getter
public enum DormCityType {
    ALL(1, "所有"),
    WUXI(2, "无锡宿舍"),
    OTHER(3, "驻外宿舍"),
    ;
    @EnumValue
    private final int code;

    private final String name;

}
