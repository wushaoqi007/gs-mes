package com.greenstone.mes.oa.infrastructure.enums;

public enum CheckinExceptionType {
    TIME_ERROR("时间异常"),
    LOCATION_ERROR("地点异常"),
    NOT_SIGN("未打卡"),
    WIFI_ERROR("wifi异常"),
    NOT_COMMON_DEVICE("非常用设备"),

    ;
    private final String name;

    CheckinExceptionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
