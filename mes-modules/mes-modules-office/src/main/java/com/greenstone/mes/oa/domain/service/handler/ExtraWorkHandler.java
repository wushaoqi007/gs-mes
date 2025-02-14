package com.greenstone.mes.oa.domain.service.handler;

import com.greenstone.mes.oa.domain.entity.*;
import com.greenstone.mes.oa.domain.helper.AttendanceHelper;
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
public class ExtraWorkHandler implements AttendanceCalcHandler {

    private final AttendanceHelper attendanceHelper;

    public ExtraWorkHandler(AttendanceHelper attendanceHelper) {
        this.attendanceHelper = attendanceHelper;
    }

    @Override
    public AttendanceResultDetail handle(AttendanceUserDayCalcDTO calcData, Map<String, Object> context) {
        AttendanceResultDetail detail = getDetail(context);
        boolean isWhite = attendanceHelper.isInWhiteListOfExtraWorkApproval(detail.getUser(), detail.getDept());
        AttendanceResultDetail.ExtraWork extraWork = calc(detail.getShift(), detail.isWorkDay(),
                detail.getDayBeginTime(), detail.getCheckinTime(), detail.getRestTime(), calcData.getApprovalExtraWorks(), isWhite);
        detail.setExtraWork(extraWork);
        return null;
    }

    private AttendanceResultDetail.ExtraWork calc(ScheduleShift shift, boolean isWorkDay, long dayBeginTime, AttendanceResultDetail.CheckinTime checkinTime, AttendanceResultDetail.RestTime restTime, List<ApprovalExtraWork> approvalExtraWorks, boolean isWhite) {
        AttendanceResultDetail.ExtraWork extraWork = new AttendanceResultDetail.ExtraWork();
        if (!checkinTime.isAbsent()) {
            Periods workPeriods = new Periods();
            Periods restPeriods = restTime.toPeriods();

            List<TimeSection> timeSections = approvalExtraWorks.stream().map(v -> new TimeSection(v.getStartTime().getTime() / 1000, v.getEndTime().getTime() / 1000)).toList();
            extraWork.setSections(new TimeSections(timeSections));
            Periods approvalExtraWorkPeriods = extraWork.toPeriods();
            attendanceHelper.roundPeriods(approvalExtraWorkPeriods);
            if (isWorkDay && checkinTime.getSingOutTime().getCheckinTime() > dayBeginTime + shift.getOffWorkSec()) {
                workPeriods.addPeriod(dayBeginTime + shift.getOffWorkSec(), checkinTime.getSingOutTime().getCheckinTime());
                attendanceHelper.roundPeriods(workPeriods);
                if (!isWhite) {
                    workPeriods = workPeriods.intersect(approvalExtraWorkPeriods);
                }
                int extraWorkSec = (int) workPeriods.complement(restPeriods).sum();
                if (extraWorkSec > 0) {
                    extraWork.setDuration(extraWorkSec);
                }
            }
            if (!isWorkDay) {
                workPeriods.addPeriod(attendanceHelper.getActualSingInTime(checkinTime.getSingInTime().getCheckinTime(), shift, dayBeginTime), checkinTime.getSingOutTime().getCheckinTime());
                attendanceHelper.roundPeriods(workPeriods);
                if (!isWhite) {
                    workPeriods = workPeriods.intersect(approvalExtraWorkPeriods);
                }
                extraWork.setDuration((int) workPeriods.complement(restPeriods).sum());
            }
            extraWork.setExtraWork((extraWork.getDuration() == null ? 0 : extraWork.getDuration()) > 0);
        }
        return extraWork;
    }

}
