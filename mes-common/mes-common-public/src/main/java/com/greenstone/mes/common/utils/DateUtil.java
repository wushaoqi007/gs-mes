package com.greenstone.mes.common.utils;

import cn.hutool.core.date.LocalDateTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author gu_renkai
 * @date 2023/2/6 11:18
 */

public class DateUtil {
    private static final DateTimeFormatter formatDf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter dateSerialFormat = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final DateTimeFormatter yearSerialFormat = DateTimeFormatter.ofPattern("yyyy");

    private static final DateTimeFormatter yearMonthSerialFormat = DateTimeFormatter.ofPattern("yyyyMM");

    public static String timeStr(LocalDateTime time) {
        return formatDf.format(time);
    }

    public static String dateSerialStr(LocalDateTime time) {
        return dateSerialFormat.format(time);
    }

    public static String dateSerialStr(LocalDate time) {
        return dateSerialFormat.format(time);
    }

    public static String dateSerialStrNow() {
        return dateSerialStr(LocalDateTime.now());
    }

    public static String yearSerialStr(LocalDateTime time) {
        return yearSerialFormat.format(time);
    }

    public static String yearMonthSerialStr(LocalDateTime time) {
        return yearMonthSerialFormat.format(time);
    }

    public static String yearSerialStrNow() {
        return yearSerialStr(LocalDateTime.now());
    }

    public static String yearMonthSerialStrNow() {
        return yearMonthSerialStr(LocalDateTime.now());
    }

    public static LocalDateTime beginOfDay() {
        return LocalDateTimeUtil.beginOfDay(LocalDateTime.now());
    }

}
