package com.greenstone.mes.asset.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author gu_renkai
 * @date 2023/2/8 14:03
 */

public enum AssetBillType {
    NONE(0, "无"),
    REQUISITION(1, "领用"),
    REVERT(2, "退库"),
    ;
    @EnumValue
    @Getter
    private final int code;

    @Getter
    private final String name;

    AssetBillType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
