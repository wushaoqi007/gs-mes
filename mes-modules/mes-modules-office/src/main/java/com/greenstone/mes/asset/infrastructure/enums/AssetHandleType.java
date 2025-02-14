package com.greenstone.mes.asset.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author gu_renkai
 * @date 2023/2/8 14:03
 */

public enum AssetHandleType {
    REQUISITION(1, "领用单"),
    REVERT(2, "退库单"),
    EDIT(3, "编辑"),
    CLEAR(4, "清理"),
    RESTORE(5, "还原"),
    ;
    @EnumValue
    @Getter
    private final int code;

    @Getter
    private final String name;

    AssetHandleType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
