package com.greenstone.mes.oa.application.assembler;

/**
 * @author gu_renkai
 * @date 2022/11/24 9:23
 */

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.greenstone.mes.oa.domain.OaWxScheduleDO;
import com.greenstone.mes.oa.domain.converter.BaseTypeConverter;
import com.greenstone.mes.oa.domain.entity.*;
import com.greenstone.mes.oa.infrastructure.enums.DefaultShift;
import com.greenstone.mes.oa.infrastructure.enums.ScheduleShift;
import com.greenstone.mes.oa.infrastructure.util.Periods;
import com.greenstone.mes.oa.interfaces.resp.AttendanceMyMonthResult;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import me.chanjar.weixin.cp.bean.WxCpDepart;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinData;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinSchedule;
import org.mapstruct.*;
import org.springframework.stereotype.Repository;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {BaseTypeConverter.class},
        imports = {Date.class}
)
public interface AttendanceAssembler {


    @Mapping(target = "deptId", source = "id")
    WxDept toWxDept(WxCpDepart depart);

    CheckinData toCheckinData(WxCpCheckinData cpCheckinData);

    List<CheckinData> toCheckinDataList(List<WxCpCheckinData> checkinDataList);

    default List<Schedule> toSchedules(CpId cpId, List<WxCpCheckinSchedule> checkinSchedules) {
        List<Schedule> schedules = new ArrayList<>();
        for (WxCpCheckinSchedule checkinSchedule : checkinSchedules) {
            List<WxCpCheckinSchedule.UserSchedule.Schedule> scheduleList = checkinSchedule.getSchedule().getScheduleList();
            for (WxCpCheckinSchedule.UserSchedule.Schedule wxSchedule : scheduleList) {
                List<WxCpCheckinSchedule.UserSchedule.Schedule.ScheduleInfo.TimeSection> timeSections = wxSchedule.getScheduleInfo().getTimeSection();
                int workSec = timeSections.stream().mapToInt(WxCpCheckinSchedule.UserSchedule.Schedule.ScheduleInfo.TimeSection::getWorkSec)
                        .min().orElse(0);
                int offWorkSec = timeSections.stream().mapToInt(WxCpCheckinSchedule.UserSchedule.Schedule.ScheduleInfo.TimeSection::getOffWorkSec)
                        .max().orElse(0);

                Schedule schedule = Schedule.builder().cpId(cpId).userId(new WxUserId(checkinSchedule.getUserid()))
                        .groupId(checkinSchedule.getGroupid()).groupName(checkinSchedule.getGroupName())
                        .ymd(String.valueOf(checkinSchedule.getYearmonth()) + (wxSchedule.getDay() < 10 ? "0" + wxSchedule.getDay() : wxSchedule.getDay()))
                        .scheduleId(wxSchedule.getScheduleInfo().getScheduleId()).scheduleName(wxSchedule.getScheduleInfo().getScheduleName())
                        .workSec(workSec).offWorkSec(offWorkSec).build();

                schedules.add(schedule);
            }
        }
        return schedules;
    }

    List<Schedule> toSchedules(List<OaWxScheduleDO> scheduleDOList);

    @Mapping(target = "ymd", source = "scheduleDate")
    Schedule toSchedule(OaWxScheduleDO scheduleDO);

    default List<OaWxScheduleDO> toScheduleDO(CpId cpId, List<WxCpCheckinSchedule> checkinSchedules) {
        List<OaWxScheduleDO> schedules = new ArrayList<>();
        for (WxCpCheckinSchedule checkinSchedule : checkinSchedules) {
            List<WxCpCheckinSchedule.UserSchedule.Schedule> scheduleList = checkinSchedule.getSchedule().getScheduleList();
            for (WxCpCheckinSchedule.UserSchedule.Schedule wxSchedule : scheduleList) {
                List<WxCpCheckinSchedule.UserSchedule.Schedule.ScheduleInfo.TimeSection> timeSections = wxSchedule.getScheduleInfo().getTimeSection();
                int workSec = timeSections.stream().mapToInt(WxCpCheckinSchedule.UserSchedule.Schedule.ScheduleInfo.TimeSection::getWorkSec)
                        .min().orElse(0);
                int offWorkSec = timeSections.stream().mapToInt(WxCpCheckinSchedule.UserSchedule.Schedule.ScheduleInfo.TimeSection::getOffWorkSec)
                        .max().orElse(0);

                OaWxScheduleDO wxScheduleDO = OaWxScheduleDO.builder().cpId(cpId.id()).userId(checkinSchedule.getUserid())
                        .groupId(checkinSchedule.getGroupid()).groupName(checkinSchedule.getGroupName())
                        .scheduleDate(String.valueOf(checkinSchedule.getYearmonth()) + (wxSchedule.getDay() < 10 ? "0" + wxSchedule.getDay() : wxSchedule.getDay()))
                        .scheduleId(wxSchedule.getScheduleInfo().getScheduleId()).scheduleName(wxSchedule.getScheduleInfo().getScheduleName())
                        .workSec(workSec).offWorkSec(offWorkSec).build();

                schedules.add(wxScheduleDO);
            }
        }
        return schedules;
    }

    @Mapping(target = "wxUserId", source = "userId")
    @Mapping(target = "wxCpId", source = "cpId")
    @Mapping(target = "extraWorkTime", source = "extraWorkTime", qualifiedByName = "formatTime")
    @Mapping(target = "vacationTime", source = "vacationTime", qualifiedByName = "formatTime")
    @Mapping(target = "shift", expression = "java(result.getShift().getName())")
    @Mapping(target = "exceptionType", expression = "java(result.getExceptionType() == null ? null : result.getExceptionType().getName())")
    @Mapping(target = "exceptionTime", source = "exceptionTime", qualifiedByName = "toExceptionTime")
    AttendanceMyMonthResult.AttendanceMyDayResult toDayResult(AttendanceResult result);

    @Mapping(target = "status", expression = "java(approval.getStatus().getName())")
    AttendanceMyMonthResult.AttendanceMyDayResult.ApprovalInfo toNightApproval(ApprovalNight approval);

    List<AttendanceMyMonthResult.AttendanceMyDayResult.ApprovalInfo> toNightApprovals(List<ApprovalNight> approvals);

    @Mapping(target = "status", expression = "java(approval.getStatus().getName())")
    AttendanceMyMonthResult.AttendanceMyDayResult.ApprovalInfo toTemChangeApproval(ApprovalTemporaryChange approval);

    List<AttendanceMyMonthResult.AttendanceMyDayResult.ApprovalInfo> toTemChangeApprovals(List<ApprovalTemporaryChange> approvals);

    @Mapping(target = "status", expression = "java(approval.getStatus().getName())")
    AttendanceMyMonthResult.AttendanceMyDayResult.ApprovalInfo toExtraWorkApproval(ApprovalExtraWork approval);

    List<AttendanceMyMonthResult.AttendanceMyDayResult.ApprovalInfo> toExtraWorkApprovals(List<ApprovalExtraWork> approvals);

    @Mapping(target = "status", expression = "java(approval.getStatus().getName())")
    AttendanceMyMonthResult.AttendanceMyDayResult.ApprovalInfo toVacationApproval(ApprovalVacation approval);

    List<AttendanceMyMonthResult.AttendanceMyDayResult.ApprovalInfo> toVacationApprovals(List<ApprovalVacation> approvals);

    @Mapping(target = "status", expression = "java(approval.getStatus().getName())")
    AttendanceMyMonthResult.AttendanceMyDayResult.CorrectionInfo toCorrectionApproval(ApprovalCorrection approval);

    List<AttendanceMyMonthResult.AttendanceMyDayResult.CorrectionInfo> toCorrectionApprovals(List<ApprovalCorrection> approvals);

    @Named("formatTime")
    default String formatTime(Integer time) {
        if (time == null) {
            return null;
        }
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        return nf.format(((double) time) / 60 / 60);
    }

    @Named("toExceptionTime")
    default String toExceptionTime(Integer time) {
        if (time == null) {
            return null;
        }
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        return nf.format(((double) time) / 60 / 60);

    }

    List<AttendanceMyMonthResult.AttendanceMyDayResult> toDayResults(List<AttendanceResult> results);


    default boolean approvalHaveIntersect(String shiftName, Periods dayShiftPeriod, Periods nightShiftPeriod, Periods approval) {
        if (shiftName.equals(DefaultShift.DAY.getName())) {
            return dayShiftPeriod.intersect(approval).sum() > 0;
        } else {
            return nightShiftPeriod.intersect(approval).sum() > 0;
        }
    }

    default AttendanceMyMonthResult toMonthResult(List<AttendanceResult> results, List<ApprovalExtraWork> approvalExtraWorks,
                                                  List<ApprovalNight> approvalNights, List<ApprovalTemporaryChange> approvalTemporaryChanges,
                                                  List<ApprovalVacation> approvalVacations, List<ApprovalCorrection> approvalCorrections,
                                                  User sysUser) {
        Integer monthVacationTime = results.stream().map(AttendanceResult::getVacationTime)
                .filter(Objects::nonNull).reduce(Integer::sum).orElse(0);
        int monthExtraWorkTime = results.stream().map(AttendanceResult::getExtraWorkTime)
                .filter(Objects::nonNull).reduce(Integer::sum).orElse(0);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);

        List<AttendanceMyMonthResult.AttendanceMyDayResult> dayResultList = toDayResults(results);
        for (AttendanceMyMonthResult.AttendanceMyDayResult dayResult : dayResultList) {
            dayResult.setUserName(sysUser.getNickName());
            dayResult.setDeptName(sysUser.getDept().getDeptName());
            Date beginOfDay = DateUtil.beginOfDay(dayResult.getDay());
            Date endOfDay = DateUtil.endOfDay(dayResult.getDay());
            Date beginOfDayNext = DateUtil.offset(beginOfDay, DateField.DAY_OF_MONTH, 1);
            Date endOfDayNext = DateUtil.offset(endOfDay, DateField.DAY_OF_MONTH, 1);
            Periods dayShiftPeriod = new Periods(beginOfDay.getTime() / 1000, endOfDay.getTime() / 1000);
            Periods nightShiftPeriod = new Periods(beginOfDayNext.getTime() / 1000, endOfDayNext.getTime() / 1000);
            // 拿到当天的夜班审批
            List<ApprovalNight> todayNights = approvalNights.stream().filter(a -> {
                Periods approval = new Periods(a.getStartTime().getTime() / 1000, a.getEndTime().getTime() / 1000);
                return approvalHaveIntersect(dayResult.getShift(), dayShiftPeriod, nightShiftPeriod, approval);
            }).collect(Collectors.toList());
            dayResult.setNightApprovalList(toNightApprovals(todayNights));
            // 拿到当天的临时白班变更登记审批
            List<ApprovalTemporaryChange> todayTemporaryChanges = approvalTemporaryChanges.stream().filter(a -> {
                Periods approval = new Periods(a.getStartTime().getTime() / 1000, a.getEndTime().getTime() / 1000);
                return approvalHaveIntersect(dayResult.getShift(), dayShiftPeriod, nightShiftPeriod, approval);
            }).collect(Collectors.toList());
            dayResult.setTemporaryChangeApprovals(toTemChangeApprovals(todayTemporaryChanges));
            // 拿到当天的加班审批
            List<ApprovalExtraWork> todayExtraWorks = approvalExtraWorks.stream().filter(a -> {
                Periods approval = new Periods(a.getStartTime().getTime() / 1000, a.getEndTime().getTime() / 1000);
                return approvalHaveIntersect(dayResult.getShift(), dayShiftPeriod, nightShiftPeriod, approval);
            }).collect(Collectors.toList());
            dayResult.setExtraWorkApprovals(toExtraWorkApprovals(todayExtraWorks));
            // 拿到当天的请假审批
            List<ApprovalVacation> todayVacations = approvalVacations.stream().filter(a -> {
                Periods approval = new Periods(a.getStartTime().getTime() / 1000, a.getEndTime().getTime() / 1000);
                return approvalHaveIntersect(dayResult.getShift(), dayShiftPeriod, nightShiftPeriod, approval);
            }).collect(Collectors.toList());
            dayResult.setVacationApprovals(toVacationApprovals(todayVacations));
            // 拿到当天的补卡审批
            List<ApprovalCorrection> todayCorrections = approvalCorrections.stream().filter(a -> {
                long approvalTime = a.getCorrectionTime().getTime() / 1000;
                if (dayResult.getShift().equals(DefaultShift.DAY.getName())) {
                    return dayShiftPeriod.isInPeriods(approvalTime);
                } else {
                    return nightShiftPeriod.isInPeriods(approvalTime);
                }
            }).collect(Collectors.toList());
            dayResult.setPunchCorrectionApprovals(toCorrectionApprovals(todayCorrections));
        }
        return AttendanceMyMonthResult.builder().totalVacationTime(nf.format(((double) monthVacationTime) / 60 / 60))
                .totalExtraWorkTime(nf.format(((double) monthExtraWorkTime) / 60 / 60))
                .dayAttendances(dayResultList).build();
    }
}
