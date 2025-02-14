package com.greenstone.mes.oa.infrastructure.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateModifier;

import java.util.Calendar;
import java.util.Date;

/**
 * @author gu_renkai
 * @date 2022/11/28 13:51
 */

public class DateUtil {

    public static Date beginOfHalfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) / 6 * 6);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Calendar beginOfHalfYear = DateModifier.modify(calendar, DateField.DAY_OF_MONTH.getValue(), DateModifier.ModifyType.TRUNCATE, true);
        return beginOfHalfYear.getTime();
    }

    public static Date endOfHalfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) / 6 * 6 + 5);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Calendar beginOfHalfYear = DateModifier.modify(calendar, DateField.DAY_OF_MONTH.getValue(), DateModifier.ModifyType.TRUNCATE, true);
        return beginOfHalfYear.getTime();
    }

    public static void main(String[] args) {
        System.out.println(DateUtil.beginOfHalfYear(new Date(1667284807 * 1000L)));
    }

}
