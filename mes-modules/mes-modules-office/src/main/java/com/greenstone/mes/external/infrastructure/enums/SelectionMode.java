package com.greenstone.mes.external.infrastructure.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SelectionMode {
    SINGLE(1, "单选"),
    MULTI(2, "多选"),
    ;
    @JSONField
    @EnumValue
    private final int model;

    private final String name;

}
