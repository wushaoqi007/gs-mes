package com.greenstone.mes.external.infrastructure.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationPoint {

    AFTER_APPROVAL_PASSED(1, "审批通过后"),
    AFTER_APPROVAL_REJECTED(2, "审批驳回后"),
    AFTER_COPY(3, "抄送后"),

    ;
    @EnumValue
    @JSONField
    private final int code;

    private final String name;
}
