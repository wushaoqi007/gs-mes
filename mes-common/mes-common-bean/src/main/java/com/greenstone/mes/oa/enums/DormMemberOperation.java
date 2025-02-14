package com.greenstone.mes.oa.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 宿舍成员操作
 */
@RequiredArgsConstructor
@Getter
public enum DormMemberOperation {
    CHECK_IN(1, "入住"),
    CHECK_OUT(2, "退房"),
    LEAVE(3, "暂离"),
    BACK(4, "返宿"),
    ;
    @EnumValue
    private final int operation;

    private final String name;

}
