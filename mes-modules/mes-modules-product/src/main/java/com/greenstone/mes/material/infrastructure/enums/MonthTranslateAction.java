package com.greenstone.mes.material.infrastructure.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MonthTranslateAction {
    JAN(1, "Jan"),
    FEB(2, "Feb"),
    MAR(3, "Mar"),
    APR(4, "Apr"),
    MAY(5, "May"),
    JUN(6, "Jun"),
    JUL(7, "Jul"),
    AGU(8, "Agu"),
    SEP(9, "Sep"),
    OCT(10, "Oct"),
    NOV(11, "Nov"),
    DEC(12, "Dec"),
    TOTAL(0, "Total"),
    ;

    private final int month;
    private final String enName;

    MonthTranslateAction(int month, String enName) {
        this.month = month;
        this.enName = enName;
    }

    public static MonthTranslateAction getByMonth(int month) {
        return Arrays.stream(MonthTranslateAction.values()).filter(s -> s.getMonth() == month).findFirst().orElse(TOTAL);
    }
}
