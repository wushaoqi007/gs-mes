package com.greenstone.mes.oa.domain.service.handler;

import com.greenstone.mes.oa.domain.entity.AttendanceResultDetail;
import com.greenstone.mes.oa.domain.entity.AttendanceUserDayCalcDTO;
import com.greenstone.mes.oa.domain.helper.AttendanceHelper;
import com.greenstone.mes.oa.infrastructure.enums.ScheduleShift;
import com.greenstone.mes.oa.infrastructure.util.CheckinUtil;
import com.greenstone.mes.oa.infrastructure.util.Periods;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class WorkTimeHandler implements AttendanceCalcHandler {

    private final AttendanceHelper attendanceHelper;

    public WorkTimeHandler(AttendanceHelper attendanceHelper) {
        this.attendanceHelper = attendanceHelper;
    }

    @Override
    public AttendanceResultDetail handle(AttendanceUserDayCalcDTO calcData, Map<String, Object> context) {
        AttendanceResultDetail detail = getDetail(context);
        Integer workTime = calc(detail.getCheckinTime(),
                detail.getShift(), detail.getRestTime(), detail.getDayBeginTime());
        detail.setWorkTime(workTime);
        return null;
    }

    private Integer calc(AttendanceResultDetail.CheckinTime checkinTime, ScheduleShift shift, AttendanceResultDetail.RestTime restTime, Long dayBeginTime) {
        int workTime = 0;
        if (checkinTime.getSingInTime() != null && checkinTime.getSingOutTime() != null) {
            Periods workPeriods = new Periods();
            Periods restPeriods = restTime.toPeriods();
            workPeriods.addPeriod(attendanceHelper.getActualSingInTime(checkinTime.getSingInTime().getCheckinTime(), shift, dayBeginTime), checkinTime.getSingOutTime().getCheckinTime());
            attendanceHelper.roundPeriods(workPeriods);
            workTime = (int) workPeriods.complement(restPeriods).sum();
            if (workTime > 0) {
                workTime = (int) CheckinUtil.round(workTime, TimeUnit.MINUTES.toSeconds(30), TimeUnit.MINUTES.toSeconds(21));
            }
        }
        return workTime;
    }
}
