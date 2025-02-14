package com.greenstone.mes.external.infrastructure.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationTarget {

    APPROVAL_BY(1, "发起人"),
    CURR_HANDLER(2, "本步骤办理人"),
    NEXT_HANDLER(3, "下一步办理人"),
    ASSIGNEES(4, "指定人员"),

    ;
    @EnumValue
    @JSONField
    private final int code;

    private final String name;
}
