package com.greenstone.mes.material.infrastructure.enums;

import lombok.Getter;

/**
 * 存放点动作
 *
 * @author gu_renkai
 * @date 2022/12/14 9:44
 */
@Getter
public enum StorePlaceAction {
    NONE(1),
    UNBIND(2),
    TRANSFER(3),
    ;
    private final int id;

    StorePlaceAction(int id) {
        this.id = id;
    }
}
