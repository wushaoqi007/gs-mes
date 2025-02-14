package com.greenstone.mes.oa.domain.service.handler;

import com.greenstone.mes.oa.application.service.impl.AttendanceRemitService;
import com.greenstone.mes.oa.domain.entity.AttendanceResultDetail;
import com.greenstone.mes.oa.domain.entity.AttendanceUserDayCalcDTO;
import com.greenstone.mes.oa.domain.entity.CheckinData;
import com.greenstone.mes.oa.infrastructure.enums.ScheduleShift;
import com.greenstone.mes.oa.infrastructure.util.Periods;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author gu_renkai
 * @date 2022/11/30 11:06
 */
@Component
public class ComeLateHandler implements AttendanceCalcHandler {

    private final AttendanceRemitService attendanceRemitService;

    public ComeLateHandler(AttendanceRemitService attendanceRemitService) {
        this.attendanceRemitService = attendanceRemitService;
    }

    @Override
    public AttendanceResultDetail handle(AttendanceUserDayCalcDTO calcData, Map<String, Object> context) {
        AttendanceResultDetail detail = getDetail(context);
        AttendanceResultDetail.ComeLate comeLate = calc(detail.isWorkDay(), detail.getDayBeginTime(), detail.getCheckinTime().getSingInTime(),
                detail.getShift(), detail.getVacation(), detail.getRestTime(), calcData.getCheckinDataList());
        detail.setComeLate(comeLate);
        attendanceRemitService.lateRemit(detail);
        return null;
    }

    private AttendanceResultDetail.ComeLate calc(boolean isWorkDay, long dayBeginTime, CheckinData singInTime, ScheduleShift shift, AttendanceResultDetail.Vacation vacation, AttendanceResultDetail.RestTime restTime, List<CheckinData> checkinDataList) {
        AttendanceResultDetail.ComeLate comeLate = new AttendanceResultDetail.ComeLate();
        long schWorkTime = dayBeginTime + shift.getWorkSec();
        if (isWorkDay && Objects.nonNull(singInTime) && (singInTime.getCheckinTime() / 60) > (schWorkTime / 60)) {
            Periods latePeriods = new Periods(schWorkTime, singInTime.getCheckinTime());

            Periods restPeriods = restTime.toPeriods();
            Periods vacationPeriods = vacation.toPeriods();
            int lateSec = (int) latePeriods.complement(restPeriods).complement(vacationPeriods).sum();
            if (lateSec > 0) {
                comeLate.setLate(true);
                comeLate.setDuration(lateSec);
            }
        }
        return comeLate;
    }

}
