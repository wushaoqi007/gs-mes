package com.greenstone.mes.oa.domain.service.handler;

import com.greenstone.mes.oa.domain.entity.AttendanceResultDetail;
import com.greenstone.mes.oa.domain.entity.AttendanceUserDayCalcDTO;
import com.greenstone.mes.oa.domain.helper.AttendanceHelper;
import com.greenstone.mes.oa.infrastructure.util.CheckinUtil;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author gu_renkai
 * @date 2022/11/30 11:14
 */
@Component
public class CorrectHandler implements AttendanceCalcHandler {

    private final AttendanceHelper attendanceHelper;

    public CorrectHandler(AttendanceHelper attendanceHelper) {
        this.attendanceHelper = attendanceHelper;
    }

    @Override
    public AttendanceResultDetail handle(AttendanceUserDayCalcDTO calcData, Map<String, Object> context) {
        correct(getDetail(context));
        return null;
    }

    private void correct(AttendanceResultDetail attendance) {
        AttendanceResultDetail.ComeLate comeLate = attendance.getComeLate();
        if (comeLate.isLate()) {
            int lateSec = (int) CheckinUtil.round(comeLate.getDuration(), TimeUnit.MINUTES.toSeconds(30), TimeUnit.MINUTES.toSeconds(10));
            comeLate.setDuration(lateSec);
        }

        AttendanceResultDetail.LeaveEarly leaveEarly = attendance.getLeaveEarly();
        if (leaveEarly.isEarly()) {
            int learySec = (int) CheckinUtil.round(leaveEarly.getDuration(), TimeUnit.MINUTES.toSeconds(30), TimeUnit.MINUTES.toSeconds(10));
            leaveEarly.setDuration(learySec);
        }

        AttendanceResultDetail.Vacation vacation = attendance.getVacation();
        if (vacation.isVacation()) {
            int vacationSec = (int) CheckinUtil.round(vacation.getDuration(), TimeUnit.MINUTES.toSeconds(30), TimeUnit.MINUTES.toSeconds(10));
            vacation.setDuration(vacationSec);
        }

        AttendanceResultDetail.ExtraWork extraWork = attendance.getExtraWork();
        if (extraWork.isExtraWork()) {
            int extraWorkSec = (int) CheckinUtil.round(extraWork.getDuration(), TimeUnit.MINUTES.toSeconds(30), TimeUnit.MINUTES.toSeconds(21));
            if (!attendanceHelper.isProdDept(attendance.getDept().getFullName()) && !attendanceHelper.isPartsManagementDept(attendance.getDept().getFullName()) && extraWorkSec < TimeUnit.HOURS.toSeconds(1)) {
                extraWork.setExtraWork(false);
                extraWork.setDuration(null);
            } else {
                extraWork.setDuration(extraWorkSec);
            }

        }

    }
}
