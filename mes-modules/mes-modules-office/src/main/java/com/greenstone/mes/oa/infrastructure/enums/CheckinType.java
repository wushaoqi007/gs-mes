package com.greenstone.mes.oa.infrastructure.enums;

import lombok.Getter;

/**
 * @author gu_renkai
 * @date 2022/11/28 13:07
 */
@Getter
public enum CheckinType {
    WORK(0, "上班打卡"),
    OFF_WORK(1, "下班打卡"),
    OUT_WORK(2, "外出打卡"),
    ;

    private final int type;

    private final String name;

    CheckinType(int type, String name) {
        this.type = type;
        this.name = name;
    }

}
