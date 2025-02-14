package com.greenstone.mes.oa.application.service.impl;

import com.greenstone.mes.oa.domain.entity.ApprovalCorrection;
import com.greenstone.mes.oa.domain.entity.AttendanceRemit;
import com.greenstone.mes.oa.domain.entity.AttendanceResultDetail;
import com.greenstone.mes.oa.domain.entity.CheckinData;
import com.greenstone.mes.oa.domain.repository.AttendanceRemitRepository;
import com.greenstone.mes.oa.infrastructure.enums.CheckinType;
import com.greenstone.mes.oa.infrastructure.enums.RemitType;
import com.greenstone.mes.oa.infrastructure.enums.ScheduleShift;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author gu_renkai
 * @date 2022/11/28 11:15
 */

@Service
public class AttendanceRemitService {

    private final AttendanceRemitRepository attendanceRemitRepository;

    public AttendanceRemitService(AttendanceRemitRepository attendanceRemitRepository) {
        this.attendanceRemitRepository = attendanceRemitRepository;
    }

    public void correctRemit(AttendanceResultDetail detail, List<ApprovalCorrection> approvalCorrections) {
        List<Date> dates = approvalCorrections.stream().map(ApprovalCorrection::getCorrectionTime).toList();
        List<CheckinData> checkinDataList = detail.getCheckinDataList().stream().filter(c -> dates.contains(new Date(c.getCheckinTime() * 1000))).toList();
        for (CheckinData checkinData : checkinDataList) {
            AttendanceRemit lateRemit = AttendanceRemit.builder().cpId(detail.getCpId()).userId(detail.getUser().getUserId())
                    .day(new Date(detail.getDayBeginTime() * 1000)).time(checkinData.getCheckinTime())
                    .checkinType(CheckinType.WORK.getName().equals(checkinData.getCheckinType()) ? CheckinType.WORK : CheckinType.OFF_WORK)
                    .remitType(RemitType.CORRECTION).build();
            correctRemit(lateRemit);
            checkinData.setRemit(true);
        }
    }

    public void lateRemit(AttendanceResultDetail detail) {
        AttendanceResultDetail.ComeLate comeLate = detail.getComeLate();
        if (comeLate.isLate()) {
            AttendanceRemit lateRemit = AttendanceRemit.builder().cpId(detail.getCpId()).userId(detail.getUser().getUserId())
                    .day(new Date(detail.getDayBeginTime() * 1000)).time(Long.valueOf(comeLate.getDuration())).checkinType(CheckinType.WORK)
                    .remitType(RemitType.LATE_EARLY).build();
            if (lateOrEarlyRemit(lateRemit) && reallyLate(detail.getShift(), detail.getDayBeginTime(), detail.getCheckinTime().getSingInTime())) {
                comeLate.setLate(false);
                comeLate.setDuration(0);
                comeLate.setRemit(true);
                attendanceRemitRepository.save(lateRemit);
            }
        }
    }

    public void earlyRemit(AttendanceResultDetail detail) {
        AttendanceResultDetail.LeaveEarly leaveEarly = detail.getLeaveEarly();
        if (leaveEarly.isEarly()) {
            AttendanceRemit earlyRemit = AttendanceRemit.builder().cpId(detail.getCpId()).userId(detail.getUser().getUserId())
                    .day(new Date(detail.getDayBeginTime() * 1000)).time(Long.valueOf(leaveEarly.getDuration())).checkinType(CheckinType.OFF_WORK)
                    .remitType(RemitType.LATE_EARLY).build();
            if (lateOrEarlyRemit(earlyRemit) && reallyEarly(detail.getShift(), detail.getDayBeginTime(), detail.getCheckinTime().getSingOutTime())) {
                leaveEarly.setEarly(false);
                leaveEarly.setDuration(0);
                leaveEarly.setRemit(true);
                attendanceRemitRepository.save(earlyRemit);
            }
        }
    }

    private boolean lateOrEarlyRemit(AttendanceRemit remit) {
        return remit.getTime() < TimeUnit.MINUTES.toSeconds(11);
    }

    private boolean reallyLate(ScheduleShift shift, Long dayBeginTime, CheckinData singInTime) {
        long schWorkTime = dayBeginTime + shift.getWorkSec();
        return singInTime != null && (singInTime.getCheckinTime() / 60) > (schWorkTime / 60) && (singInTime.getCheckinTime() / 60) < ((schWorkTime / 60) + 11);
    }

    private boolean reallyEarly(ScheduleShift shift, Long dayBeginTime, CheckinData singOutTime) {
        long schOffWorkTime = dayBeginTime + shift.getOffWorkSec();
        return singOutTime != null && (singOutTime.getCheckinTime() / 60) < (schOffWorkTime / 60) && (singOutTime.getCheckinTime() / 60) > ((schOffWorkTime / 60) - 10);
    }

    private void correctRemit(AttendanceRemit remit) {
        attendanceRemitRepository.save(remit);
    }

}
