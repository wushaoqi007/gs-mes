package com.greenstone.mes.material.infrastructure.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 零件步骤
 */
@Getter
public enum PartStep {
    PURCHASED(1, "已采购"),
    RECEIVED(2, "已收件"),
    CHECKED(3, "已检验"),
    FINISHED(4, "已入库"),
    USED(5, "已领用"),
    ;

    private final int id;
    private final String name;


    PartStep(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static PartStep getById(int id) {
        return Arrays.stream(PartStep.values()).filter(s -> s.getId() == id).findFirst().orElse(null);
    }

}
