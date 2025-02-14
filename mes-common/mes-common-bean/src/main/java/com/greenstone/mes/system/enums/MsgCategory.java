package com.greenstone.mes.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MsgCategory {
    ADMIN_NOTICE(0, "管理员通知"),
    HUMAN_AFFAIRS(1, "人事"),
    MACHINING(2, "机加工"),
    PRODUCTION(3, "生产"),
    ASSET(4, "资产"),
    PURCHASE(5, "采购"),
    ;
    @EnumValue
    private final int subType;

    private final String name;

}
