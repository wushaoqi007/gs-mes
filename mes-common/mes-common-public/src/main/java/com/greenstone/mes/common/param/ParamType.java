package com.greenstone.mes.common.param;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ParamType {
    STATIC(1),
    ;
    @EnumValue
    private final int type;

}
