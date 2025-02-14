package com.greenstone.mes.material.infrastructure.enums;

import lombok.Getter;

/**
 * 出入库动作
 *
 * @author gu_renkai
 * @date 2022/12/14 9:44
 */
@Getter
public enum StockAction {
    /**
     * 0 入库
     */
    IN(0),
    /**
     * 1 出库
     */
    OUT(1),
    TRANSFER(2),
    UPDATE(3),
    ;
    private final int id;

    StockAction(int id) {
        this.id = id;
    }
}
