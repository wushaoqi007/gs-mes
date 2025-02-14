package com.greenstone.mes.oa.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 宿舍成员状态
 */
@RequiredArgsConstructor
@Getter
public enum DormMemberStatus {
    LIVE_IN(1, "在住"),
    LEAVE(2, "暂离"),
    ;
    @EnumValue
    private final int status;

    private final String name;

}
