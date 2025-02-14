package com.greenstone.mes.oa.infrastructure.enums;

public enum SpName {
    LEAVE("请假"),
    OUTSIDE("外出"),
    BUSINESS_TRIP("出差"),
    OVERTIME("加班"),
    NIGHT_SHIFT("夜班"),
    ;
    private final String name;

    SpName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}