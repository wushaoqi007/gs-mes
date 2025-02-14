package com.greenstone.mes.material.infrastructure.enums;

/**
 * @author gu_renkai
 * @date 2022/10/31 10:56
 */

public enum PartType {

    PROCESS(1, "加工件"),
    STANDARD(2, "标准件"),
    ;

    private final Integer type;

    private final String name;

    PartType(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
