package com.greenstone.mes.oa.domain.service.handler;

import com.greenstone.mes.oa.application.service.impl.AttendanceRemitService;
import com.greenstone.mes.oa.domain.entity.AttendanceResultDetail;
import com.greenstone.mes.oa.domain.entity.AttendanceUserDayCalcDTO;
import com.greenstone.mes.oa.domain.entity.CheckinData;
import com.greenstone.mes.oa.infrastructure.enums.ScheduleShift;
import com.greenstone.mes.oa.infrastructure.util.Periods;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * @author gu_renkai
 * @date 2022/11/30 11:06
 */
@Component
public class LeaveEarlyHandler implements AttendanceCalcHandler {

    private final AttendanceRemitService attendanceRemitService;

    public LeaveEarlyHandler(AttendanceRemitService attendanceRemitService) {
        this.attendanceRemitService = attendanceRemitService;
    }

    @Override
    public AttendanceResultDetail handle(AttendanceUserDayCalcDTO calcData, Map<String, Object> context) {
        AttendanceResultDetail detail = getDetail(context);
        AttendanceResultDetail.LeaveEarly leaveEarly = calc(detail.isWorkDay(), detail.getDayBeginTime(),
                detail.getCheckinTime().getSingOutTime(), detail.getShift(), detail.getVacation(), detail.getRestTime());
        detail.setLeaveEarly(leaveEarly);
        attendanceRemitService.earlyRemit(detail);
        return null;
    }

    private AttendanceResultDetail.LeaveEarly calc(boolean isWorkDay, long dayBeginTime, CheckinData singOutTime, ScheduleShift shift, AttendanceResultDetail.Vacation vacation, AttendanceResultDetail.RestTime restTime) {
        AttendanceResultDetail.LeaveEarly leaveEarly = new AttendanceResultDetail.LeaveEarly();
        long schOffWorkTime = dayBeginTime + shift.getOffWorkSec();
        if (isWorkDay && Objects.nonNull(singOutTime) && singOutTime.getCheckinTime() < schOffWorkTime) {

            Periods earlyPeriods = new Periods();
            earlyPeriods.addPeriod(singOutTime.getCheckinTime(), schOffWorkTime);
            Periods restPeriods = restTime.toPeriods();
            Periods vacationPeriods = vacation.toPeriods();
            int earlySec = (int) earlyPeriods.complement(restPeriods).complement(vacationPeriods).sum();
            if (earlySec > 0) {
                leaveEarly.setEarly(true);
                leaveEarly.setDuration(earlySec);
            }
        }
        return leaveEarly;
    }
}
