package com.greenstone.mes.oa.infrastructure.enums;

/**
 * 班次:用于计算
 */
public enum DefaultShift {
    DAY(1, "早班", 28800, 61200),
    NIGHT(2, "晚班", 72000, 104400),
    NONE(0, "休息", 0, 0),
    ;

    DefaultShift(int id, String name, int workSec, int offWorkSec) {
        this.id = id;
        this.name = name;
        this.workSec = workSec;
        this.offWorkSec = offWorkSec;
    }

    private final int id;

    private final String name;

    private final int workSec;

    private final int offWorkSec;

    public static DefaultShift get(String name) {
        for (DefaultShift value : DefaultShift.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return NONE;
    }

    public static DefaultShift get(int id) {
        for (DefaultShift value : DefaultShift.values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        return NONE;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWorkSec() {
        return workSec;
    }

    public int getOffWorkSec() {
        return offWorkSec;
    }

    public boolean isNightShift() {
        return NIGHT == this;
    }

    public boolean isDayShift() {
        return DAY == this;
    }

    public boolean isRest() {
        return NONE == this;
    }

    public boolean isWorkDay() {
        return NONE != this;
    }
}
