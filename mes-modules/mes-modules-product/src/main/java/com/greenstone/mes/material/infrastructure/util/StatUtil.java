package com.greenstone.mes.material.infrastructure.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author wushaoqi
 * @date 2023-02-23-13:57
 */
public class StatUtil {

    public static String dateToSimpleStr(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(time);
    }

    public static String dateToCompleteStr(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(time);
    }

    public static Date strToDate(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(time));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTime();
    }

    public static Date todayOffWork(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static long todayOffWorkTime(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(time));
            calendar.set(Calendar.HOUR_OF_DAY, 17);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTime().getTime() / 1000;
    }

    public static boolean inToday(Date time, String today) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(today));
            calendar.set(Calendar.HOUR_OF_DAY, 17);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date todayOffWork = calendar.getTime();
            Date yesterdayOffWork = reduceOneDay(todayOffWork);
            if (time.getTime() / 1000 > yesterdayOffWork.getTime() / 1000 && time.getTime() / 1000 <= todayOffWork.getTime() / 1000) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static long todayBeginTime(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(time));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTime().getTime() / 1000;
    }

    public static Date plusOneDay(Date day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static Date reduceOneDay(Date day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    public static Date monthStart(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date monthEnd(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static String monthToStr(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        return format.format(time);
    }

    public static Date monthStrToDate(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        try {
            return format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String reduceOneMonth(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.MONTH, -1);
        return format.format(calendar.getTime());
    }

    public static String firstMonthOfYear(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        return format.format(calendar.getTime());
    }

    public static String lastMonthOfYear(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        return format.format(calendar.getTime());
    }

    public static Date strToYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    public static String toYearStr(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return format.format(time);
    }

    public static String fourMonthAgo(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.MONTH, -4);
        return dateToSimpleStr(calendar.getTime());
    }

    public static Date[] getCurrentWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        //start of the week
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        calendar.add(Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK) - 2));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date startTime = calendar.getTime();
        //end of the week
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endTime = calendar.getTime();
        return new Date[]{startTime, endTime};
    }

    public static String getDayStrByWeek(String time, int week) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat format2 = new SimpleDateFormat("MM月dd日");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.WEEK_OF_MONTH, week);
        calendar.set(Calendar.DAY_OF_WEEK, 7);
        return format2.format(calendar.getTime());
    }

    /**
     * 获取间隔多少时间之前的时间
     *
     * @param time     初始时间
     * @param interval 间隔时间
     * @param unit     单位(day:天；hour:小时；minute:分钟)
     * @return 时间
     */
    public static Date timeBeforeInterval(Date time, int interval, String unit) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        if ("day".equals(unit)) {
            calendar.add(Calendar.DAY_OF_MONTH, -interval);
            calendar.add(Calendar.MINUTE, -10);
        }
        if ("hour".equals(unit)) {
            calendar.add(Calendar.HOUR_OF_DAY, -interval);
            calendar.add(Calendar.MINUTE, -10);
        }
        if ("minute".equals(unit)) {
            calendar.add(Calendar.MINUTE, -interval);
            calendar.add(Calendar.MINUTE, -10);
        }
        return calendar.getTime();
    }

    /**
     * 校验日期相差的范围
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param length    范围大小（天）
     */
    public static boolean checkDateRange(String startTime, String endTime, int length) {
        if (StrUtil.isNotEmpty(startTime) && StrUtil.isNotEmpty(endTime)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = null;
            Date end = null;
            try {
                start = format.parse(startTime);
                end = format.parse(endTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return DateUtil.between(start, end, DateUnit.DAY) <= length;
        }
        return true;
    }
}
