package com.greenstone.mes.oa.infrastructure.enums;

import lombok.Getter;

/**
 * @author gu_renkai
 * @date 2022/12/1 8:51
 */
@Getter
public enum RemitType {
    LATE_EARLY(0, "迟到早退"),
    CORRECTION(1, "打卡补卡"),
    ;

    private final int code;

    private final String name;


    RemitType(int code, String name) {
        this.code = code;
        this.name = name;
    }


}
