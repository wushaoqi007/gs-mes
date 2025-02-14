package com.greenstone.mes.external.infrastructure.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MultiApproveMode {
    OR(1, "或签"),
    ALL(2, "会签"),
    SUCCESSIVELY(3, "依次审批"),
    ;
    @JSONField(defaultValue = "")
    @EnumValue
    private final int mode;

    private final String name;

}
