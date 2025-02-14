package com.greenstone.mes.oa.domain.service.handler;

import com.greenstone.mes.oa.domain.entity.AttendanceResultDetail;
import com.greenstone.mes.oa.domain.entity.AttendanceUserDayCalcDTO;
import com.greenstone.mes.oa.domain.entity.TimeSection;
import com.greenstone.mes.oa.domain.entity.TimeSections;
import com.greenstone.mes.oa.domain.helper.AttendanceHelper;
import com.greenstone.mes.oa.infrastructure.enums.ScheduleShift;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author gu_renkai
 * @date 2022/11/30 11:06
 */
@Component
public class RestHandler implements AttendanceCalcHandler {

    private final AttendanceHelper attendanceHelper;

    public RestHandler(AttendanceHelper attendanceHelper) {
        this.attendanceHelper = attendanceHelper;
    }

    @Override
    public AttendanceResultDetail handle(AttendanceUserDayCalcDTO calcData, Map<String, Object> context) {
        AttendanceResultDetail detail = getDetail(context);
        AttendanceResultDetail.RestTime restTime = getRestTime(calcData.getDayBeginTime(), detail.getShift(), detail.getTrip().isTrip()
                , calcData.getDept().getFullName());
        detail.setRestTime(restTime);
        return null;
    }

    private AttendanceResultDetail.RestTime getRestTime(long dayBeginTime, ScheduleShift shift, boolean isTrip, String deptName) {
        List<TimeSection> timeSections = new ArrayList<>();
        // 自定义排班
        if (shift.getCustomShift() != null) {
            String restTime = shift.isNightShift() ? shift.getCustomShift().getNightRestTime() : shift.getCustomShift().getDayRestTime();
            String[] split = restTime.split(",");
            for (String s : split) {
                String[] time = s.split("-");
                LocalTime start = LocalTime.parse(time[0], DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime end = LocalTime.parse(time[1], DateTimeFormatter.ofPattern("HH:mm"));
                if (shift.isNightShift()) {
                    timeSections.add(new TimeSection(dayBeginTime + start.toSecondOfDay() + 86400,
                            dayBeginTime + end.toSecondOfDay() + 86400));
                } else {
                    timeSections.add(new TimeSection(dayBeginTime + start.toSecondOfDay(),
                            dayBeginTime + end.toSecondOfDay()));
                }

            }
        } else if (shift.isNightShift()) {
            // 如果是夜班，则上班的第4个小时到第5个小时之间的时间，不算加班，即如果是20点上班，则次日24:00-1:00 是休息时间
            timeSections.add(new TimeSection(dayBeginTime + TimeUnit.HOURS.toSeconds(20 + 4),
                    dayBeginTime + TimeUnit.HOURS.toSeconds(20 + 5)));
        } else {
            // 如果是白班，则上班的第3.5个小时到第4.5个小时之间的时间，不算加班，即
            // 生产中心：11:30-12:30 是休息时间
            // 其他部门：12:00-13:00 是休息时间
            if (attendanceHelper.isProdCenterDept(deptName)) {
                timeSections.add(new TimeSection(dayBeginTime + TimeUnit.HOURS.toSeconds(8 + 3) + TimeUnit.MINUTES.toSeconds(30),
                        dayBeginTime + TimeUnit.HOURS.toSeconds(8 + 4) + TimeUnit.MINUTES.toSeconds(30)));
            } else {
                timeSections.add(new TimeSection(dayBeginTime + TimeUnit.HOURS.toSeconds(12),
                        dayBeginTime + TimeUnit.HOURS.toSeconds(13)));
            }

            // 出差，加班的第2个小时到2.5个小时之间的时间，算休息时间，即如果是17点下班，19点到19:30 是休息时间
            if (isTrip) {
                timeSections.add(new TimeSection(dayBeginTime + TimeUnit.HOURS.toSeconds(17 + 2),
                        dayBeginTime + TimeUnit.HOURS.toSeconds(17 + 2) + TimeUnit.MINUTES.toSeconds(30)));
            }
            // 不出差的（2024-11-15开始）
            // 加班的第0.5个小时到1个小时之间的时间，算休息时间，即如果是17点下班，17:30到18:00 是休息时间
            if (!isTrip) {
                timeSections.add(new TimeSection(dayBeginTime + TimeUnit.HOURS.toSeconds(17) + TimeUnit.MINUTES.toSeconds(30),
                        dayBeginTime + TimeUnit.HOURS.toSeconds(18)));
            }
        }

        return new AttendanceResultDetail.RestTime(new TimeSections(timeSections));
    }
}
