package com.greenstone.mes.oa.application.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.oa.application.dto.attendance.ApprovalLeave;
import com.greenstone.mes.oa.application.dto.attendance.ApprovalVector;
import com.greenstone.mes.oa.application.dto.attendance.DailyAttendanceResult;
import com.greenstone.mes.oa.infrastructure.config.WxAttendanceOptionProperties;
import com.greenstone.mes.oa.infrastructure.enums.PunchMachine;
import com.greenstone.mes.oa.infrastructure.util.CheckinUtil;
import com.greenstone.mes.oa.infrastructure.util.Periods;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinData;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinSchedule;
import me.chanjar.weixin.cp.bean.oa.WxCpCropCheckinOption;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AttendanceOldHelper {

    /**
     * 获取实际上下班打卡时间
     *
     * @param schWorkTime 标准工作区间
     * @return 实际上下班打卡时间
     */
    public DailyAttendanceResult.ActualCheckinTime getActualCheckinTime(Periods schWorkTime, List<WxCpCheckinData> effectiveCheckinDataList) {
        // 如果没有打卡记录，则上班打卡和下班打卡都是null
        if (CollectionUtil.isEmpty(effectiveCheckinDataList)) {
            return new DailyAttendanceResult.ActualCheckinTime();
        } else if (effectiveCheckinDataList.size() == 1) {
            // 如果只有一次打卡记录，到了标准下班时间则算下班打卡，否则算上班打卡
            Long checkinTime = effectiveCheckinDataList.get(0).getCheckinTime();
            DailyAttendanceResult.ActualCheckinTime actualCheckinTime;
            if (schWorkTime == null) {
                actualCheckinTime = DailyAttendanceResult.ActualCheckinTime.builder().singInTime(checkinTime).singOutTime(null).signInType(effectiveCheckinDataList.get(0).getCheckinType()).build();
            } else if (checkinTime >= schWorkTime.getMaxTime()) {
                actualCheckinTime = DailyAttendanceResult.ActualCheckinTime.builder().singInTime(null).singOutTime(checkinTime).signOutType(effectiveCheckinDataList.get(0).getCheckinType()).build();
            } else {
                actualCheckinTime = DailyAttendanceResult.ActualCheckinTime.builder().singInTime(checkinTime).singOutTime(null).signInType(effectiveCheckinDataList.get(0).getCheckinType()).build();
            }
            return actualCheckinTime;
        } else {
            // 如果有两次或以上打卡记录，则取最早的作为上把打卡，最晚的作为下班打卡
            List<WxCpCheckinData> sortedCheckinDataList = effectiveCheckinDataList.stream().sorted((o1, o2) -> o1.getCheckinTime() - o2.getCheckinTime() > 0 ? 1 : -1).
                    collect(Collectors.toList());
            return DailyAttendanceResult.ActualCheckinTime.builder().singInTime(sortedCheckinDataList.get(0).
                            getCheckinTime()).singOutTime(sortedCheckinDataList.get(sortedCheckinDataList.size() - 1).getCheckinTime()).
                    signInType(sortedCheckinDataList.get(0).getCheckinType()).signOutType(sortedCheckinDataList.get(sortedCheckinDataList.size() - 1).getCheckinType()).build();
        }
    }

    public void correctActualCheckinTime(DailyAttendanceResult.ActualCheckinTime actualCheckinTime, Periods schWorkTime, List<WxCpCheckinData> effectiveCheckinData) {
        if (actualCheckinTime.getSingInTime() != null) {
            if (schWorkTime != null && schWorkTime.getMinTime() >= actualCheckinTime.getSingInTime()) {
                actualCheckinTime.setEffectiveSingInTime(schWorkTime.getMinTime());
            } else {
                Long allowLateTime = getAllowLateTime(actualCheckinTime.getSingInTime(), effectiveCheckinData);
                long singInTime = actualCheckinTime.getSingInTime();
                if (singInTime < allowLateTime && schWorkTime != null) {
                    singInTime = schWorkTime.getMinTime();
                }
                actualCheckinTime.setEffectiveSingInTime(CheckinUtil.round(singInTime, TimeUnit.MINUTES.toSeconds(30), TimeUnit.MINUTES.toSeconds(10)));
            }
        }
        if (actualCheckinTime.getSingOutTime() != null) {
            long singOutTime = actualCheckinTime.getSingOutTime();
            actualCheckinTime.setEffectiveSingOutTime(CheckinUtil.round(singOutTime, TimeUnit.MINUTES.toSeconds(30), TimeUnit.MINUTES.toSeconds(21)));
        }

    }

    public List<Long> getBeginTimeStampOfDays(Long startTime, Long endTime) {
        if (startTime == null || endTime == null) {
            return Collections.emptyList();
        }
        return getBeginTimeStampOfDays(new Date(startTime * 1000), new Date(endTime * 1000));
    }

    /**
     * 获取每天的开始时间戳
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 开始时间戳
     */
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

    /**
     * 判断是否为工作日
     * 根据班次判断
     *
     * @param todayBeginTimeStamp   当天的开始时间戳
     * @param wxCpCropCheckinOption 打卡规则
     * @return 是否为工作日
     */
    public boolean isWorkDay(long todayBeginTimeStamp, WxCpCropCheckinOption wxCpCropCheckinOption, WxCpCheckinSchedule.UserSchedule.Schedule schedule) {
        // 首先根据特殊日期判断，即规则中必须上班和不需要上班的日期
        if (wxCpCropCheckinOption != null) {
            // 必须上班的日期
            List<WxCpCropCheckinOption.SpeWorkday> speWorkdays = wxCpCropCheckinOption.getSpeWorkdays();
            for (WxCpCropCheckinOption.SpeWorkday speWorkday : speWorkdays) {
                if (speWorkday.getTimestamp() == todayBeginTimeStamp) {
                    return true;
                }
            }
            // 不需要上班的日期
            List<WxCpCropCheckinOption.SpeOffDay> speOffDays = wxCpCropCheckinOption.getSpeOffDays();
            for (WxCpCropCheckinOption.SpeOffDay speOffDay : speOffDays) {
                if (speOffDay.getTimestamp() == todayBeginTimeStamp) {
                    return false;
                }
            }
        }
        if (schedule != null) {
            return schedule.getScheduleInfo().getScheduleId() != 0;
        }
        return false;
    }

    /**
     * 获取默认的白班的固定工作时间区间
     *
     * @param dayBeginTime 当天0点时间戳
     * @return 白班工作时间区间
     */
    public Periods getDefaultDayShiftWorkPeriods(long dayBeginTime, List<WxCpCheckinData> checkinDataList) {
        Periods workPeriods = new Periods();
        // 当天0点的时间
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime dateTime = LocalDateTime.ofInstant(new Date(dayBeginTime * 1000).toInstant(), zoneId);
        // 早上上班时间
        dateTime = dateTime.withHour(8).withMinute(0).withSecond(0);
        long morningWorkStartTime = Date.from(dateTime.atZone(zoneId).toInstant()).getTime() / 1000;
        // 下午结束时间
        dateTime = dateTime.withHour(17).withMinute(0).withSecond(0);
        long afternoonWorkEndTime = Date.from(dateTime.atZone(zoneId).toInstant()).getTime() / 1000;

        workPeriods.addPeriod(morningWorkStartTime, afternoonWorkEndTime);
        return workPeriods;
    }

    /**
     * 获取允许迟到的时间
     *
     * @param schSignInTime 标准签到时间
     * @return 允许迟到的时间
     */
    public Long getAllowLateTime(long schSignInTime, List<WxCpCheckinData> checkinDataList) {
        // 当天0点的时间
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime dateTime = LocalDateTime.ofInstant(new Date(schSignInTime * 1000).toInstant(), zoneId);
        // 早上上班时间
        // 在南京使用打卡机打卡的，可以晚15分钟到，因为打卡机距离园区大门非常远
        if (CollUtil.isNotEmpty(checkinDataList) && checkinDataList.size() >= 2 && isPunchMachineInNanJing(checkinDataList)) {
            dateTime = dateTime.withHour(8).withMinute(15).withSecond(0);
        }
        return Date.from(dateTime.atZone(zoneId).toInstant()).getTime() / 1000;
    }

    /**
     * 获取默认的夜班的固定工作时间区间
     *
     * @param todayBeginTimeStamp 当天0点(毫秒)
     * @return 夜班工作时间区间
     */
    public Periods getDefaultNightShiftWorkPeriods(long todayBeginTimeStamp) {
        Periods workPeriods = new Periods();
        // 当天0点的时间
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime dateTime = LocalDateTime.ofInstant(new Date(todayBeginTimeStamp * 1000).toInstant(), zoneId);
        // 晚班上班时间 当天晚上8点
        dateTime = dateTime.withHour(20).withMinute(0).withSecond(0);
        long workStartTime = Date.from(dateTime.atZone(zoneId).toInstant()).getTime() / 1000;
        // 晚班下班时间 次日5点
        dateTime = dateTime.plusHours(4);
        long workEndTime = Date.from(dateTime.atZone(zoneId).toInstant()).getTime() / 1000;

        workPeriods.addPeriod(workStartTime, workEndTime);
        return workPeriods;
    }

    /**
     * 是否在南京使用打卡机打卡
     *
     * @param checkinDataList 打卡数据
     * @return true 若在南京使用打卡机打卡
     */
    public boolean isPunchMachineInNanJing(List<WxCpCheckinData> checkinDataList) {
        if (CollUtil.isEmpty(checkinDataList)) {
            return false;
        }
        for (WxCpCheckinData wxCpCheckinData : checkinDataList) {
            if (StrUtil.isNotEmpty(wxCpCheckinData.getLocationTitle())) {
                PunchMachine punchMachine = PunchMachine.getByName(wxCpCheckinData.getLocationTitle());
                if (punchMachine.isInNanJing()) {
                    return true;
                }
            }
        }
        return false;
    }

    private DailyAttendanceResult.AllowedCheckinTime getDefaultDayShiftAllowedCheckinRange(long todayBeginTimeStamp) {
        // 当天0点的时间
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime dateTime = LocalDateTime.ofInstant(new Date(todayBeginTimeStamp * 1000).toInstant(), zoneId);
        // 4点以后才能签到
        dateTime = dateTime.withHour(4).withMinute(0).withSecond(0);
        long allowSignInTime = Date.from(dateTime.atZone(zoneId).toInstant()).getTime() / 1000;
        // 次日4点以前可以退勤
        dateTime = dateTime.plusDays(1);
        long allowSignOutTime = Date.from(dateTime.atZone(zoneId).toInstant()).getTime() / 1000;
        return DailyAttendanceResult.AllowedCheckinTime.builder().start(allowSignInTime).end(allowSignOutTime).build();
    }

    private DailyAttendanceResult.AllowedCheckinTime getDefaultNightShiftAllowedCheckinRange(long todayBeginTimeStamp) {
        // 当天0点的时间
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime dateTime = LocalDateTime.ofInstant(new Date(todayBeginTimeStamp * 1000).toInstant(), zoneId);
        // 16点以后才能签到
        dateTime = dateTime.withHour(16).withMinute(0).withSecond(0);
        long allowSignInTime = Date.from(dateTime.atZone(zoneId).toInstant()).getTime() / 1000;
        // 次日16点以前可以退勤
        dateTime = dateTime.plusDays(1);
        long allowSignOutTime = Date.from(dateTime.atZone(zoneId).toInstant()).getTime() / 1000;
        return DailyAttendanceResult.AllowedCheckinTime.builder().start(allowSignInTime).end(allowSignOutTime).build();
    }

    /**
     * 胡哦去允许打卡范围内的打卡记录
     *
     * @param checkinDataList
     * @param allowedCheckinTime
     * @return
     */
    public List<WxCpCheckinData> getCheckinDataInAllowedCheckinTime(List<WxCpCheckinData> checkinDataList, DailyAttendanceResult.AllowedCheckinTime allowedCheckinTime) {
        if (CollUtil.isEmpty(checkinDataList)) {
            return Collections.emptyList();
        } else {
            return checkinDataList.stream().filter(c -> c.getCheckinTime() >= allowedCheckinTime.getStart() &&
                    c.getCheckinTime() <= allowedCheckinTime.getEnd()).collect(Collectors.toList());
        }
    }

    /**
     * 将加班时间取整
     *
     * @param overTimeDuration 加班时间
     * @return 取整后的加班时间
     */
    public long roundOverTime(long overTimeDuration) {
        // 加班时间取整，以30分钟为单位进行计算，不满30分钟的，满21分钟按30分钟计算，否则不计算
        return CheckinUtil.round(overTimeDuration, TimeUnit.MINUTES.toSeconds(30), TimeUnit.MINUTES.toSeconds(21));
    }

    /**
     * 去除无效的打卡数据，以下是无效的数据
     * 1.未打卡 2.需要审批但未经过审批的外出打卡
     *
     * @param checkinData 打卡记录
     * @param approvals   审批记录
     */
    public List<WxCpCheckinData> getEffectiveCheckinData(List<WxCpCheckinData> checkinData, List<ApprovalLeave> approvals) {
        // 过滤掉未打卡数据
        checkinData = checkinData.stream().filter(d -> !"未打卡".equals(d.getExceptionType())).collect(Collectors.toList());

        // 外出打卡需要审批时，过滤掉无审批的外出打卡
        if (WxAttendanceOptionProperties.outNeedApproval) {
            // 有外出打卡记录，则查看是否有对应审批
            Optional<WxCpCheckinData> outsideCheckinData = checkinData.stream().filter(c -> "外出打卡".equals(c.getCheckinType())).findFirst();
            if (outsideCheckinData.isPresent()) {
                // 若没有审批单，则过滤掉外出打卡数据
                if (CollectionUtil.isEmpty(approvals)) {
                    checkinData = checkinData.stream().filter(c -> !"外出打卡".equals(c.getCheckinType())).collect(Collectors.toList());
                } else {
                    // 若有审批单，则保留审批通过了的，且在审批时间内的打卡数据
                    Periods outPeriods = getPeriodsFromApprovalVector(approvals);
                    checkinData = checkinData.stream().filter(c -> !"外出打卡".equals(c.getCheckinType()) || outPeriods.isInPeriods(c.getCheckinTime())).collect(Collectors.toList());
                }
            }
        }
        return checkinData;
    }

    public Periods getPeriodsFromApprovalVector(List<? extends ApprovalVector> approvals) {
        Periods approvalPeriods = new Periods();
        // 如果没有审批单，返回一个空的区间
        if (CollectionUtil.isEmpty(approvals)) {
            return approvalPeriods;
        }
        for (ApprovalVector approval : approvals) {
            // 时间以30分钟为单位计算
            long startTime = CheckinUtil.round(approval.getStart(), TimeUnit.MINUTES.toSeconds(30), TimeUnit.MINUTES.toSeconds(30) - 1);
            long endTime = CheckinUtil.round(approval.getEnd(), TimeUnit.MINUTES.toSeconds(30), 1);
            approvalPeriods.addPeriod(startTime, endTime);
        }
        return approvalPeriods;
    }


    /**
     * 判断是否为生产部门
     * 电气部门、装配部门、科技外包人员属于生产部门
     *
     * @param deptName 部门名称
     * @return 是否为生产部门
     */
    private boolean isProdDept(String deptName) {
        return "电气部门".equals(deptName) || "装配部门".equals(deptName) || "科技外包人员".equals(deptName);
    }

    public <T extends ApprovalVector> List<T> getRelatedApproval(List<T> approvalList, Long start, Long end) {
        if (CollUtil.isEmpty(approvalList)) {
            return null;
        }
        return approvalList.stream().filter(a ->
                a.getStart() >= start && a.getStart() <= end ||
                        a.getEnd() >= start && a.getEnd() <= end ||
                        a.getStart() <= start && a.getEnd() >= end).collect(Collectors.toList());
    }

    /**
     * 矫正请假数据
     *
     * @param todayLeaveApprovals 今天的请假数据
     * @param actualCheckinTime   实际打卡时间
     * @param schWorkTime         标准打卡时间
     */
    public void correctApprovalLeave(List<ApprovalLeave> todayLeaveApprovals, DailyAttendanceResult.ActualCheckinTime actualCheckinTime, Periods schWorkTime) {
        // 实际打卡区间
        Periods actualCheckinPeriods = new Periods();
        if (actualCheckinTime.getSingInTime() != null && actualCheckinTime.getSingOutTime() != null) {
            actualCheckinPeriods.addPeriod(actualCheckinTime.getSingInTime(), actualCheckinTime.getSingOutTime());
        }
        if (CollUtil.isNotEmpty(todayLeaveApprovals)) {
            for (ApprovalLeave todayLeaveApproval : todayLeaveApprovals) {
                Periods approvalPeriods = new Periods();
                // 请假时间以30分钟为单位计算
                long startTime = CheckinUtil.round(todayLeaveApproval.getStart(), TimeUnit.MINUTES.toSeconds(30), TimeUnit.MINUTES.toSeconds(30) - 1);
                long endTime = CheckinUtil.round(todayLeaveApproval.getEnd(), TimeUnit.MINUTES.toSeconds(30), 1);
                // 请假区间
                approvalPeriods.addPeriod(startTime, endTime);
                // 标准打卡时间点和请假区间有交集
                if (schWorkTime != null && (approvalPeriods.isInPeriods(schWorkTime.getMinTime()) || approvalPeriods.isInPeriods(schWorkTime.getMaxTime()))) {
                    // 请假区间去除打卡区间
                    approvalPeriods = approvalPeriods.complement(actualCheckinPeriods);
                }

                todayLeaveApproval.setPeriods(approvalPeriods);
            }
        }


    }

    /**
     * 合并请假区间
     *
     * @param approvalLeaveList 请假审批
     * @return 请假区间
     */
    public Periods getMergedLeavePeriods(List<ApprovalLeave> approvalLeaveList) {
        Periods periods = new Periods();
        // 将请假审批内的所有请假区间合并
        if (CollUtil.isNotEmpty(approvalLeaveList)) {
            for (ApprovalLeave approvalLeave : approvalLeaveList) {
                if (approvalLeave.getPeriods() != null) {
                    periods.merge(approvalLeave.getPeriods());
                }
            }
        }
        return periods;
    }
}
