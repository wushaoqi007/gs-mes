package com.greenstone.mes.bom.enums;

public enum ClashStrategy {
    DO_NOTHING(0),
    UPDATE_DETAIL(1),
    THROW_ERROR(2),
    ;
    private final Integer value;

    ClashStrategy(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
