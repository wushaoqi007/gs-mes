package com.greenstone.mes.oa.domain.service.handler;

import com.greenstone.mes.oa.domain.entity.*;
import com.greenstone.mes.oa.infrastructure.enums.CheckinType;
import com.greenstone.mes.oa.infrastructure.enums.ScheduleShift;
import com.greenstone.mes.oa.infrastructure.util.CoordinateUtil;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @author gu_renkai
 * @date 2022/11/30 11:03
 */
@Component
public class CheckOptionHandler implements AttendanceCalcHandler {

    @Override
    public AttendanceResultDetail handle(AttendanceUserDayCalcDTO calcData, Map<String, Object> context) {
        AttendanceResultDetail detail = getDetail(context);
        detail.setShift(chooseShift(calcData.getShift(), detail.getCheckinTime(), calcData.getCustomShifts(), calcData.getUser()));
        // 是否工作日一定要用排班判断，用班次计算来判断会导致非工作日上夜班变成工作日的夜班加班
        detail.setWorkDay(calcData.getSchedule() != null && calcData.getSchedule().getScheduleId() != 0);
        return null;
    }

    private ScheduleShift chooseShift(ScheduleShift shift, AttendanceResultDetail.CheckinTime checkinTime, List<CustomShift> customShifts, OaWxUser user) {
        CheckinData singInTime = checkinTime.getSingInTime();
        if (singInTime != null && singInTime.getCheckinType().equals(CheckinType.OUT_WORK.getName())) {
            for (CustomShift customShift : customShifts) {
                GlobalCoordinates source = new GlobalCoordinates((double) singInTime.getLat() / 1000000, (double) singInTime.getLng() / 1000000);
                GlobalCoordinates target = new GlobalCoordinates(customShift.getLat(), customShift.getLng());
                double distance = CoordinateUtil.getDistanceMeter(source, target, Ellipsoid.Sphere);
                if (distance <= customShift.getDistance()) {
                    System.out.println("Sphere坐标系计算结果：" + distance + "米，匹配自定义班次：" + customShift);
                    System.out.println("使用自定义班次计算：" + user);
                    String customShiftStr = customShift.getDayShift();
                    if (shift.isNightShift()) {
                        customShiftStr = customShift.getNightShift();
                        String[] splitShift = customShiftStr.split("-");
                        shift.setWorkSec(LocalTime.parse(splitShift[0], DateTimeFormatter.ofPattern("HH:mm")).toSecondOfDay());
                        shift.setOffWorkSec(LocalTime.parse(splitShift[1], DateTimeFormatter.ofPattern("HH:mm")).toSecondOfDay() + 86400);
                    } else {
                        String[] splitShift = customShiftStr.split("-");
                        shift.setWorkSec(LocalTime.parse(splitShift[0], DateTimeFormatter.ofPattern("HH:mm")).toSecondOfDay());
                        shift.setOffWorkSec(LocalTime.parse(splitShift[1], DateTimeFormatter.ofPattern("HH:mm")).toSecondOfDay());
                    }
                    shift.setCustomShift(customShift);
                    return shift;
                }
            }
        }
        // 只通过上班打卡来判定自定义班次
//        CheckinData singOutTime = checkinTime.getSingOutTime();
//        if (singOutTime != null && singOutTime.getCheckinType().equals(CheckinType.OUT_WORK.getName())) {
//            for (CustomShift customShift : customShifts) {
//                GlobalCoordinates source = new GlobalCoordinates((double) singOutTime.getLat() / 1000000, (double) singOutTime.getLng() / 1000000);
//                GlobalCoordinates target = new GlobalCoordinates(customShift.getLat(), customShift.getLng());
//                double distance = CoordinateUtil.getDistanceMeter(source, target, Ellipsoid.Sphere);
//                System.out.println("Sphere坐标系计算结果：" + distance + "米");
//                if (distance <= customShift.getDistance()) {
//                    shift.setWorkSec(customShift.getSignInTime().toSecondOfDay());
//                    shift.setOffWorkSec(customShift.getSignOutTime().toSecondOfDay());
//                    shift.setCustomShift(customShift);
//                    return shift;
//                }
//            }
//        }
        return shift;
    }

}
