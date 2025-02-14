package com.greenstone.mes.material.infrastructure.enums;

import lombok.Getter;

/**
 * @author gu_renkai
 * @date 2022/10/31 10:04
 */
@Getter
public enum ProcessOrderStatus {
    TO_CONFIRM(1, "待确认"),
    TO_RECEIVE(2, "待收件"),
    RECEIVING(3, "收件中"),
    RECEIVED(4, "已收件"),
    ABANDON(5, "已废弃"),
    CHANGING(6, "变更中");

    private final Integer status;
    private final String name;

    ProcessOrderStatus(Integer status, String name) {
        this.status = status;
        this.name = name;
    }


}
