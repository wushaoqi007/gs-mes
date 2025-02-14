package com.greenstone.mes.oa.infrastructure.enums;

import lombok.Getter;

/**
 * @author gu_renkai
 * @date 2022/11/28 13:07
 */
@Getter
public enum AttendanceExceptionType {
    LATE(1, "迟到"),
    EARLY(2, "早退"),
    LATE_AND_EARLY(3, "迟到并早退"),
    LACK(4, "缺卡"),
    ABSENT(8, "缺勤"),
    ;

    private final int type;

    private final String name;

    AttendanceExceptionType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static AttendanceExceptionType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (AttendanceExceptionType value : AttendanceExceptionType.values()) {
            if (value.getType() == code) {
                return value;
            }
        }
        return null;
    }

}
