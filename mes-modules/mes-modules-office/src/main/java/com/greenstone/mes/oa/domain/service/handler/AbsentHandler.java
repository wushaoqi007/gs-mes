package com.greenstone.mes.oa.domain.service.handler;

import com.greenstone.mes.oa.domain.entity.AttendanceResultDetail;
import com.greenstone.mes.oa.domain.entity.AttendanceUserDayCalcDTO;
import com.greenstone.mes.oa.infrastructure.enums.ScheduleShift;
import com.greenstone.mes.oa.infrastructure.util.Periods;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author gu_renkai
 * @date 2022/11/30 11:06
 */
@Component
public class AbsentHandler implements AttendanceCalcHandler {
    @Override
    public AttendanceResultDetail handle(AttendanceUserDayCalcDTO calcData, Map<String, Object> context) {
        AttendanceResultDetail detail = getDetail(context);
        AttendanceResultDetail.Absenteeism absenteeism = calc(detail.isWorkDay(), detail.getDayBeginTime(), detail.getCheckinTime(),
                detail.getShift(), detail.getVacation(), detail.getRestTime());
        detail.setAbsenteeism(absenteeism);
        return null;
    }

    private AttendanceResultDetail.Absenteeism calc(boolean isWorkDay, long dayBeginTime, AttendanceResultDetail.CheckinTime checkinTime, ScheduleShift shift, AttendanceResultDetail.Vacation vacation, AttendanceResultDetail.RestTime restTime) {
        AttendanceResultDetail.Absenteeism absenteeism = new AttendanceResultDetail.Absenteeism();
        if (isWorkDay && checkinTime.isAbsent()) {
            Periods workPeriods = new Periods(dayBeginTime + shift.getWorkSec(), dayBeginTime + shift.getOffWorkSec());
            Periods restPeriods = restTime.toPeriods();
            Periods vacationPeriods = vacation.toPeriods();

            int absentSec = (int) workPeriods.complement(restPeriods).complement(vacationPeriods).sum();
            if (absentSec > 0) {
                absenteeism.setAbsenteeism(true);
                absenteeism.setDuration(absentSec);
            }
        }
        return absenteeism;
    }
}
