package com.greenstone.mes.asset.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

public enum AssetRepairStatus {

    REPAIRING(0, "维修中"),
    FINISH(1, "已完成"),
    ;

    @EnumValue
    @Getter
    private final int status;

    @Getter
    private final String name;

    AssetRepairStatus(int status, String name) {
        this.status = status;
        this.name = name;
    }

}
