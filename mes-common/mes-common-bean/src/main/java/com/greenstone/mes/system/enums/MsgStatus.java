package com.greenstone.mes.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MsgStatus {
    NEW(0, "新消息"),
    READ(1, "已读"),
    ;
    @EnumValue
    private final int id;

    private final String name;

}
