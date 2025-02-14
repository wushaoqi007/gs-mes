package com.greenstone.mes.external.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CopyHandleStatus {
    NEW(1, "新抄送"),
    HANDLED(2, "已处理"),
    UPDATED(3, "有更新"),
    IGNORED(4, "已忽略"),
    ;

    @EnumValue
    private final int status;

    private final String name;

}
