package com.greenstone.mes.oa.domain.helper;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.oa.application.service.WxCheckinDataService;
import com.greenstone.mes.oa.domain.entity.OaWxUser;
import com.greenstone.mes.oa.domain.entity.Schedule;
import com.greenstone.mes.oa.domain.entity.TimeSection;
import com.greenstone.mes.oa.domain.entity.WxDept;
import com.greenstone.mes.oa.infrastructure.constant.AttendanceParam;
import com.greenstone.mes.oa.infrastructure.enums.DefaultShift;
import com.greenstone.mes.oa.infrastructure.enums.ScheduleShift;
import com.greenstone.mes.oa.infrastructure.util.CheckinUtil;
import com.greenstone.mes.oa.infrastructure.util.Periods;
import com.greenstone.mes.wxcp.domain.helper.WxDeptService;
import com.greenstone.mes.wxcp.domain.helper.WxOaService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.WxDeptId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.WxCpDepart;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinData;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * @author gu_renkai
 * @date 2022/11/24 17:07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceHelper {

    private final WxOaService externalWxOaService;
    private final WxDeptService externalWxDeptService;
    private final WxCheckinDataService wxCheckinDataService;

    public List<Long> getBeginTimeStampOfDays(Date startTime, Date endTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        List<Long> beginTimeStamps = new ArrayList<>();
        while (calendar.getTime().getTime() <= endTime.getTime()) {
            beginTimeStamps.add(calendar.getTime().getTime() / 1000);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return beginTimeStamps;
    }

    public static TimeSection getAllowedCheckinSection(long todayBeginTimeStamp, ScheduleShift shift) {
        if (shift.isNightShift()) {
            return getNightAllowedCheckinRange(todayBeginTimeStamp);
        } else {
            return getDayAllowedCheckinRange(todayBeginTimeStamp);
        }
    }

    /**
     * 选择班次
     *
     * @param hasNightApproval 有夜班审批
     * @param hasChange        有临时变更审批
     * @return 班次（用于计算的班次）
     */
    public static ScheduleShift pickScheduleShift(boolean hasNightApproval, boolean hasChange) {
        if (hasNightApproval && !hasChange) {
            DefaultShift night = DefaultShift.NIGHT;
            return ScheduleShift.builder().id(night.getId()).name(night.getName()).workSec(night.getWorkSec()).offWorkSec(night.getOffWorkSec()).build();
        } else {
            DefaultShift day = DefaultShift.DAY;
            return ScheduleShift.builder().id(day.getId()).name(day.getName()).workSec(day.getWorkSec()).offWorkSec(day.getOffWorkSec()).build();
        }
    }

    private static TimeSection getDayAllowedCheckinRange(long todayBeginTimeStamp) {
        // 当天0点的时间
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime dateTime = LocalDateTime.ofInstant(new Date(todayBeginTimeStamp * 1000).toInstant(), zoneId);
        // 4点以后才能签到
        dateTime = dateTime.withHour(4).withMinute(0).withSecond(0);
        long allowSignInTime = Date.from(dateTime.atZone(zoneId).toInstant()).getTime() / 1000;
        // 次日4点以前可以退勤
        dateTime = dateTime.plusDays(1);
        long allowSignOutTime = Date.from(dateTime.atZone(zoneId).toInstant()).getTime() / 1000;
        return new TimeSection(allowSignInTime, allowSignOutTime);
    }

    private static TimeSection getNightAllowedCheckinRange(long todayBeginTimeStamp) {
        // 当天0点的时间
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime dateTime = LocalDateTime.ofInstant(new Date(todayBeginTimeStamp * 1000).toInstant(), zoneId);
        // 16点以后才能签到
        dateTime = dateTime.withHour(16).withMinute(0).withSecond(0);
        long allowSignInTime = Date.from(dateTime.atZone(zoneId).toInstant()).getTime() / 1000;
        // 次日16点以前可以退勤
        dateTime = dateTime.plusDays(1);
        long allowSignOutTime = Date.from(dateTime.atZone(zoneId).toInstant()).getTime() / 1000;
        return new TimeSection(allowSignInTime, allowSignOutTime);
    }

    public Schedule findSchedule(List<Schedule> schedules, Long dayBeginTime, WxUserId userId) {
        Date date = new Date(dayBeginTime * 1000);
        SimpleDateFormat formatYearMonth = new SimpleDateFormat("yyyyMMdd");
        String ymd = formatYearMonth.format(date);
        return schedules.stream().filter(s -> s.getUserId().equals(userId) && s.getYmd().equals(ymd)).findFirst().orElse(null);
    }


    public boolean isProdDept(String deptName) {
        String[] split = AttendanceParam.PROD_DEPT.split(";");
        return Arrays.asList(split).contains(deptName);
    }

    public boolean isNotCountExtraWork(String employeeNo) {
        String[] split = AttendanceParam.NOT_COUNT_EXTRA_WORK.split(";");
        return Arrays.asList(split).contains(employeeNo);
    }

    public boolean isProdCenterDept(String deptName) {
        if (StrUtil.isEmpty(deptName)) {
            return false;
        }
        return deptName.contains(AttendanceParam.PROD_CENTER_DEPT);
    }

    public boolean isTechDept(long dayBeginTime, String deptName) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse("2023-06-14");
            if (date.getTime() / 1000 >= dayBeginTime) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return AttendanceParam.RD_DEPT.contains(deptName);
    }

    public boolean isDesignDept(long dayBeginTime, String deptName) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse("2023-07-06");
            if (date.getTime() / 1000 > dayBeginTime) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return AttendanceParam.DESIGN_DEPT.contains(deptName);
    }

    public boolean isPartsManagementDept(String deptName) {
        return AttendanceParam.PARTS_DEPT.contains(deptName);
    }

    public String getChineseDayOfWeek(int day) {
        return switch (day) {
            case 1 -> "日";
            case 2 -> "一";
            case 3 -> "二";
            case 4 -> "三";
            case 5 -> "四";
            case 6 -> "五";
            case 7 -> "六";
            default -> null;
        };
    }

    public List<WxCpCheckinData> listCheckinData(CpId cpId, Date startTime, Date endTime, List<String> userList) {
        return wxCheckinDataService.listCheckinData(cpId, startTime, endTime, userList);
    }

    public void setDeptFullName(WxDept dept, CpId cpId, WxCpDepart wxDepart) {
        String fullName = dept.getFullName() == null ? dept.getName() : dept.getFullName();
        dept.setFullName(fullName);
        if (wxDepart.getParentId() != null && wxDepart.getId() != 1L && wxDepart.getParentId() != 1L) {
            WxCpDepart parentDepart = externalWxDeptService.getDept(cpId, new WxDeptId(wxDepart.getParentId()));
            if (parentDepart != null) {
                dept.setFullName(parentDepart.getName() + "/" + fullName);
                setDeptFullName(dept, cpId, parentDepart);
            }
        }
    }

    public boolean isInWhiteListOfExtraWorkApproval(OaWxUser user, WxDept dept) {
        if (AttendanceParam.WHITELIST_DEPT_EXTRA_WORK_APPROVAL.contains(dept.getFullName())) {
            if (StrUtil.isNotEmpty(AttendanceParam.WHITELIST_DEPT_EXCLUSION_USERID_EXTRA_WORK_APPROVAL)) {
                String[] whiteListExclusionUserId = AttendanceParam.WHITELIST_DEPT_EXCLUSION_USERID_EXTRA_WORK_APPROVAL.split(";");
                Optional<String> find = Arrays.stream(whiteListExclusionUserId).filter(userId -> userId.equals(user.getUserId().id())).findFirst();
                return find.isEmpty();
            }
            return true;
        }
        if (StrUtil.isNotEmpty(AttendanceParam.WHITELIST_USERID_EXTRA_WORK_APPROVAL)) {
            String[] whiteListUserId = AttendanceParam.WHITELIST_USERID_EXTRA_WORK_APPROVAL.split(";");
            Optional<String> find = Arrays.stream(whiteListUserId).filter(userId -> userId.equals(user.getUserId().id())).findFirst();
            return find.isPresent();
        }
        return false;
    }

    public boolean isWuXiPunchMachine(String name) {
        if (StrUtil.isNotEmpty(AttendanceParam.PUNCHMACHINE_IN_WUXI)) {
            String[] wuxiPunchines = AttendanceParam.PUNCHMACHINE_IN_WUXI.split(";");
            Optional<String> find = Arrays.stream(wuxiPunchines).filter(p -> p.equals(name)).findFirst();
            return find.isPresent();
        }
        return false;
    }

    public boolean isNanJingPunchMachine(String name) {
        if (StrUtil.isNotEmpty(AttendanceParam.PUNCHMACHINE_IN_NANJING)) {
            String[] nanJingPunchines = AttendanceParam.PUNCHMACHINE_IN_NANJING.split(";");
            Optional<String> find = Arrays.stream(nanJingPunchines).filter(p -> p.equals(name)).findFirst();
            return find.isPresent();
        }
        return false;
    }

    /**
     * 获取实际上班时间
     * 非工作日的加班开始时间:白班默认8点，不能早于8点；晚班默认20点
     */
    public Long getActualSingInTime(Long singInTime, ScheduleShift shift, Long dayBeginTime) {
        return Math.max(singInTime, shift.getWorkSec() + dayBeginTime);
    }

    /**
     * 区间取整：以30分钟整为准，开始时间满20分钟算30分钟，结束时间满21分钟算30分钟。
     */
    public void roundPeriods(Periods periods) {
        for (Periods.Period period : periods.getPeriods()) {
            period.setStart(roundStartTime(period.getStart()));
            period.setEnd(roundEndTime(period.getEnd()));
        }
    }

    public long roundStartTime(Long signInTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime calTime = LocalDateTime.ofInstant(new Date(signInTime * 1000).toInstant(), zoneId).withSecond(0).withNano(0);
        int calMinute = 60 - calTime.getMinute();
        long round = CheckinUtil.round(calMinute, 30, 19);
        LocalDateTime roundResult = calTime.withMinute(0).plusHours(1).plusMinutes(-round);
        return Date.from(roundResult.atZone(zoneId).toInstant()).getTime() / 1000;

    }

    public long roundEndTime(Long signOutTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime calTime = LocalDateTime.ofInstant(new Date(signOutTime * 1000).toInstant(), zoneId).withSecond(0).withNano(0);
        int calMinute = calTime.getMinute();
        long round = CheckinUtil.round(calMinute, 30, 20);
        LocalDateTime roundResult = calTime.withMinute(0).plusMinutes(round);
        return Date.from(roundResult.atZone(zoneId).toInstant()).getTime() / 1000;
    }
}
