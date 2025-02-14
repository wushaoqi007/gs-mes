package com.greenstone.mes.oa.domain.service.handler;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.oa.domain.entity.*;
import com.greenstone.mes.oa.infrastructure.enums.ScheduleShift;
import com.greenstone.mes.oa.infrastructure.util.Periods;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author gu_renkai
 * @date 2022/11/30 11:06
 */
@Component
public class VacationHandler implements AttendanceCalcHandler {
    @Override
    public AttendanceResultDetail handle(AttendanceUserDayCalcDTO calcData, Map<String, Object> context) {
        AttendanceResultDetail detail = getDetail(context);
        AttendanceResultDetail.Vacation vacation = from(detail.isWorkDay(), detail.getDayBeginTime(), detail.getShift(), detail.getRestTime(), calcData.getApprovalVacations());
        detail.setVacation(vacation);
        return null;
    }

    private AttendanceResultDetail.Vacation from(boolean isWorkDay, long dayBeginTime, ScheduleShift shift, AttendanceResultDetail.RestTime restTime, List<ApprovalVacation> approvalVacations) {
        AttendanceResultDetail.Vacation vacation = new AttendanceResultDetail.Vacation();
        if (isWorkDay && CollUtil.isNotEmpty(approvalVacations)) {
            List<TimeSection> timeSections = approvalVacations.stream().map(v -> new TimeSection(v.getStartTime().getTime() / 1000, v.getEndTime().getTime() / 1000)).toList();
            vacation.setSections(new TimeSections(timeSections));

            Periods workPeriods = new Periods(dayBeginTime + shift.getWorkSec(), dayBeginTime + shift.getOffWorkSec());
            Periods vacationPeriods = vacation.toPeriods();
            Periods restPeriods = restTime.getRestSections().toPeriods();
            int vacationSec = (int) workPeriods.intersect(vacationPeriods).complement(restPeriods).sum();
            if (vacationSec > 0) {
                vacation.setVacation(true);
                vacation.setDuration(vacationSec);
                vacation.setType(approvalVacations.get(0).getType().getName());
            }

        }
        return vacation;
    }
}
