package com.greenstone.mes.bom.enums;

public enum DetailAddStrategy {
    NEW(0),
    APPEND(1),
    REMOVE(2),
    RESET(3),
    ;
    private final Integer value;

    DetailAddStrategy(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
