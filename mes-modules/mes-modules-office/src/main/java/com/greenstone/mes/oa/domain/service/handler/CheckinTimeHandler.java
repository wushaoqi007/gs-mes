package com.greenstone.mes.oa.domain.service.handler;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.oa.application.service.impl.AttendanceRemitService;
import com.greenstone.mes.oa.domain.entity.*;
import com.greenstone.mes.oa.infrastructure.enums.ScheduleShift;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author gu_renkai
 * @date 2022/11/30 11:14
 */
@Component
public class CheckinTimeHandler implements AttendanceCalcHandler {

    private final AttendanceRemitService attendanceRemitService;

    public CheckinTimeHandler(AttendanceRemitService attendanceRemitService) {
        this.attendanceRemitService = attendanceRemitService;
    }

    @Override
    public AttendanceResultDetail handle(AttendanceUserDayCalcDTO calcData, Map<String, Object> context) {
        AttendanceResultDetail detail = getDetail(context);
        attendanceRemitService.correctRemit(detail, calcData.getApprovalCorrections());
        detail.setCheckinTime(buildCheckinTime(calcData.getCheckinDataList(), calcData.getDayBeginTime(), calcData.getShift(), calcData.getAllCheckinDataList(), calcData.getApprovalExtraWorks(), calcData.getAllowedCheckinTime()));
        return null;
    }

    private AttendanceResultDetail.CheckinTime buildCheckinTime(List<CheckinData> checkinDataList, long dayBeginTime, ScheduleShift shift, List<CheckinData> allCheckinDataList, List<ApprovalExtraWork> approvalExtraWorks, TimeSection allowedCheckinTime) {
        // 根据加班申请判断是否连班
        boolean isContinuousWorkButLack = false;
        CollUtil.isNotEmpty(approvalExtraWorks);
        AttendanceResultDetail.CheckinTime checkinTime = AttendanceResultDetail.CheckinTime.builder().checkinTimes(0).build();
        if (CollUtil.isEmpty(checkinDataList)) {
            return checkinTime;
        } else if (checkinDataList.size() < 2) {
            if (checkinDataList.get(0).getCheckinTime() < dayBeginTime + shift.getOffWorkSec()) {
                checkinTime = AttendanceResultDetail.CheckinTime.builder().singInTime(checkinDataList.get(0)).checkinTimes(1).build();
            } else {
                checkinTime = AttendanceResultDetail.CheckinTime.builder().singOutTime(checkinDataList.get(0)).checkinTimes(1).build();
            }
        } else {
            CheckinData min = checkinDataList.stream().min(CheckinData::compareTo).orElse(null);
            CheckinData max = checkinDataList.stream().max(CheckinData::compareTo).orElse(null);
            checkinTime = AttendanceResultDetail.CheckinTime.builder().singInTime(min).singOutTime(max).checkinTimes(checkinDataList.size()).build();
        }
        return checkinTime;
    }

}
