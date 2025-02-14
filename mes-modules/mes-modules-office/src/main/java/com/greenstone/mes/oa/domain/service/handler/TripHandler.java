package com.greenstone.mes.oa.domain.service.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.oa.domain.entity.AttendanceResultDetail;
import com.greenstone.mes.oa.domain.entity.AttendanceUserDayCalcDTO;
import com.greenstone.mes.oa.domain.entity.CheckinData;
import com.greenstone.mes.oa.domain.helper.AttendanceHelper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author gu_renkai
 * @date 2022/11/30 11:06
 */
@Component
public class TripHandler implements AttendanceCalcHandler {
    private final AttendanceHelper attendanceHelper;

    public TripHandler(AttendanceHelper attendanceHelper) {
        this.attendanceHelper = attendanceHelper;
    }

    @Override
    public AttendanceResultDetail handle(AttendanceUserDayCalcDTO calcData, Map<String, Object> context) {
        AttendanceResultDetail detail = getDetail(context);
        detail.setTrip(from(calcData.getCheckinDataList()));
        return null;
    }

    private AttendanceResultDetail.Trip from(List<CheckinData> checkinDataList) {
        AttendanceResultDetail.Trip trip = new AttendanceResultDetail.Trip();
        if (CollUtil.isEmpty(checkinDataList)) {
            return trip;
        }
        for (CheckinData checkinData : checkinDataList) {
            // 使用了南京的打卡机，就算出差
            if (StrUtil.isNotBlank(checkinData.getLocationTitle()) && attendanceHelper.isNanJingPunchMachine(checkinData.getLocationTitle())) {
                trip.setTrip(true);
                trip.setLocation(checkinData.getLocationTitle());
                break;
            }
            // 打卡地点不在无锡市，或在江阴市打卡，就算出差
            if (StrUtil.isNotBlank(checkinData.getLocationDetail()) &&
                    (!checkinData.getLocationDetail().contains("无锡市") || checkinData.getLocationDetail().contains("江阴市"))) {
                // 使用了无锡总部的打卡机，不算出差（解决打卡地点为null算出差情况）
                if (StrUtil.isNotBlank(checkinData.getLocationTitle()) && attendanceHelper.isWuXiPunchMachine(checkinData.getLocationTitle())) {
                    continue;
                }
                trip.setTrip(true);
                trip.setLocation(checkinData.getLocationTitle());
                break;
            }
        }
        trip.setLocation(checkinDataList.get(0).getLocationTitle());
        return trip;
    }

}
