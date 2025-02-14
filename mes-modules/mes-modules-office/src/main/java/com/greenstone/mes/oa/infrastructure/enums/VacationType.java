package com.greenstone.mes.oa.infrastructure.enums;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Date;

/**
 * @author gu_renkai
 * @date 2022/11/17 13:31
 */
@Getter
@Slf4j
public enum VacationType {

    ANNUAL("年假", "按天"),
    PERSONAL("事假", "按小时"),
    SICK("病假", "按小时"),
    REST_PRODUCTION("调休假（生产部门专用）", "按小时"),
    REST_PRODUCTION_2("调休假（生产部）", "按小时"),
    REST("调休假", "按小时"),
    MARRIAGE("婚假", "按天"),
    MATERNITY("产假", "按天"),
    PATERNITY("陪产假", "按天"),
    PRENATAL("产检假", "按天"),
    OTHER("其他", "按天"),
    UNKNOWN("未知", "按天"),
    BEREAVEMENT("丧假", "按天"),
    ;

    public Date castStartTime(String startTimeStr) {
        Date startTime = null;
        if ("按天".equals(this.getUnit())) {
            String[] times = startTimeStr.split(" ");
            DateTime dateTime = DateUtil.parse(times[0], "yyyy/MM/dd");
            String halfDay = times[1];
            if ("下午".equals(halfDay)) {
                dateTime.offset(DateField.HOUR_OF_DAY, 12);
            }
            startTime = dateTime;
        } else if ("按小时".equals(this.getUnit())) {
            startTime = DateUtil.parse(startTimeStr, "yyyy/MM/dd HH:mm");
        }
        return startTime;
    }

    public Date castEndTime(String endTimeStr) {
        Date endTime = null;
        if ("按天".equals(this.getUnit())) {
            String[] times = endTimeStr.split(" ");
            DateTime dateTime = DateUtil.parse(times[0], "yyyy/MM/dd");
            String halfDay = times[1];
            if ("下午".equals(halfDay)) {
                dateTime.offset(DateField.HOUR_OF_DAY, 24);
            } else if ("上午".equals(halfDay)) {
                dateTime.offset(DateField.HOUR_OF_DAY, 12);
            }
            endTime = dateTime;
        } else if ("按小时".equals(this.getUnit())) {
            endTime = DateUtil.parse(endTimeStr, "yyyy/MM/dd HH:mm");
        }
        return endTime;
    }

    private final String name;

    private final String unit;

    VacationType(String name, String unit) {
        this.name = name;
        this.unit = unit;
    }

    public static VacationType getByName(String name) {
        VacationType vacationType = Arrays.stream(VacationType.values()).filter(t -> t.getName().equals(name)).findFirst().orElse(null);
        if (vacationType == null) {
            log.error("unidentifiable vacation type: " + name);
            return UNKNOWN;
        }
        return vacationType;
    }
}
