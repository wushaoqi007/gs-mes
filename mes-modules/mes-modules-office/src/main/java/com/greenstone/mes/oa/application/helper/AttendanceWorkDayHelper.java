package com.greenstone.mes.oa.application.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.oa.application.dto.attendance.*;
import com.greenstone.mes.oa.infrastructure.config.WxAttendanceOptionProperties;
import com.greenstone.mes.oa.infrastructure.enums.PunchMachine;
import com.greenstone.mes.oa.infrastructure.util.CheckinUtil;
import com.greenstone.mes.oa.infrastructure.util.Periods;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AttendanceWorkDayHelper {

    @Autowired
    private AttendanceOldHelper attendanceOldHelper;

    /**
     * 计算迟到时间
     *
     * @param schWorkTime      标准工作区间
     * @param actualSignInTime 实际签到时间
     */
    public DailyAttendanceResult.ComeLate calcLateTime(Periods schWorkTime, Long actualSignInTime, List<ApprovalLeave> approvalLeaveList, List<WxCpCheckinData> checkinDataList) {
        DailyAttendanceResult.ComeLate late = new DailyAttendanceResult.ComeLate();
        if (actualSignInTime == null || schWorkTime == null) {
            return late;
        }
        // 获取标准签到时间，即标准工作区间中最小的时间
        long schSignInTime = schWorkTime.getMinTime();
        // 获取允许迟到的时间
        schSignInTime = attendanceOldHelper.getAllowLateTime(schSignInTime, checkinDataList);
        // 若迟到，则计算迟到时间
        if (schSignInTime < actualSignInTime) {
            // 迟到的时间段
            Periods latePeriods = new Periods();
            latePeriods.addPeriod(schSignInTime, actualSignInTime);
            // 请假时间段
            Periods leavePeriods = attendanceOldHelper.getMergedLeavePeriods(approvalLeaveList);
            // 实际迟到时间段，即将请假的时间段从迟到时间段中去掉
            Periods actualLatePeriods = latePeriods.complement(leavePeriods);
            // 计算实际迟到时间
            long lateTime = schWorkTime.intersect(actualLatePeriods).sum();

            // 以30分钟为单位，超过10分钟向上取整，否则向下取整，如：迟到 20分钟，算30分钟；迟到35分钟，算30分钟
            long roundLateTime = CheckinUtil.round(lateTime, TimeUnit.MINUTES.toSeconds(30), TimeUnit.MINUTES.toSeconds(11));
            if (roundLateTime > 0) {
                late.setLate(true);
                // edit by wsq 2022-12-02 梁冬琴要求8点01才算迟到
            } else if (lateTime >= TimeUnit.MINUTES.toSeconds(1)) {
                // 迟到小于10分钟，使用一次迟到早退次数，迟到早退次数每半年有20次，次数内不算迟到早退，超过次数之后算迟到早退
                late.setExemption(true);
            }
            late.setDuration(roundLateTime);
        }
        return late;
    }

    /**
     * 计算早退时间
     *
     * @param schWorkTime       标准工作区间
     * @param actualSignOutTime 实际签退时间
     */
    public DailyAttendanceResult.LeaveEarly calcEarlyTime(Periods schWorkTime, Long actualSignOutTime, List<ApprovalLeave> approvalLeaveList) {
        DailyAttendanceResult.LeaveEarly early = new DailyAttendanceResult.LeaveEarly();
        if (actualSignOutTime == null || schWorkTime == null) {
            return early;
        }
        // 获取标准签退时间，即标准工作区间中最大的时间
        long schSignOutTime = schWorkTime.getMaxTime();
        // 若早退，则计算早退时间
        if (actualSignOutTime < schSignOutTime) {
            // 设置早退的时间段
            Periods earlyPeriods = new Periods();
            earlyPeriods.addPeriod(actualSignOutTime, schSignOutTime);
            // 请假时间段
            Periods leavePeriods = attendanceOldHelper.getMergedLeavePeriods(approvalLeaveList);
            // 实际早退时间段，即将请假的时间段从早退时间段中去掉
            Periods actualEarlyPeriods = earlyPeriods.complement(leavePeriods);
            // 计算实际早退时间
            long earlyTime = schWorkTime.intersect(actualEarlyPeriods).sum();

            // 早退满10分钟，按照早退计算，
            if (earlyTime >= TimeUnit.MINUTES.toSeconds(10)) {
                // 以30分钟为单位，超过10分钟向上取整，否则向下取整，如：早退 20分钟，算30分钟；早退35分钟，算30分钟
                earlyTime = CheckinUtil.round(earlyTime, TimeUnit.MINUTES.toSeconds(30), TimeUnit.MINUTES.toSeconds(10));
                early.setEarly(true);
            } else {
                // 早退小于10分钟，使用一次迟到早退次数，迟到早退次数每半年有20次，次数内不算迟到早退，超过次数之后算迟到早退
                early.setExemption(true);
                earlyTime = 0;
            }
            early.setDuration(earlyTime);
        }
        return early;
    }

    /**
     * 计算旷工时间
     *
     * @param schWorkTime     标准工作区间
     * @param lateTime        迟到时间
     * @param earlyTime       早退时间
     * @param checkinDataList 打卡数据
     * @return 旷工时间
     */
    public DailyAttendanceResult.Absenteeism calcAbsenteeismTime(Periods schWorkTime, Long lateTime, Long earlyTime, List<WxCpCheckinData> checkinDataList, List<ApprovalLeave> approvalLeaveList) {
        DailyAttendanceResult.Absenteeism absenteeism = new DailyAttendanceResult.Absenteeism();
        if (schWorkTime == null) {
            return absenteeism;
        }
        // 如果打卡次数小于2，算整天旷工
        if (checkinDataList == null || checkinDataList.size() < 2) {
            // 请假时间段
            Periods calcPeriods = null;
            if (CollUtil.isNotEmpty(approvalLeaveList)) {
                Periods leavePeriods = attendanceOldHelper.getMergedLeavePeriods(approvalLeaveList);
                calcPeriods = schWorkTime.complement(leavePeriods);
            } else {
                calcPeriods = schWorkTime;
            }
            absenteeism.setDuration(calcPeriods.sum());
            absenteeism.setLackOfCheckin(true);
            absenteeism.setReason("缺卡");
        } else {
            // 如果打卡次数大于或等于2,旷工时间 = 迟到时间 + 早退时间
            long absenteeismTime = 0;
            if (lateTime != null) {
                absenteeismTime += lateTime;
            }
            if (earlyTime != null) {
                absenteeismTime += earlyTime;
            }
            if (lateTime != null || earlyTime != null) {
                absenteeism.setDuration(absenteeismTime);
                absenteeism.setReason("迟到/早退");
            }
        }
        return absenteeism;
    }

    /**
     * 计算请假时间
     *
     * @param schWorkTime       标准工作区间
     * @param approvalLeaveList 审批数据
     * @return 请假时间
     */
    public DailyAttendanceResult.Leave calcLeaveTime(Periods schWorkTime, List<ApprovalLeave> approvalLeaveList) {
        DailyAttendanceResult.Leave leave = new DailyAttendanceResult.Leave();
        if (schWorkTime == null || schWorkTime.isEmpty()) {
            return leave;
        }
        if (CollUtil.isNotEmpty(approvalLeaveList)) {
            // 过滤出今天的审批
            List<ApprovalLeave> todayLeaveApprovalList = approvalLeaveList.stream().filter(a -> {
                Periods periods = new Periods();
                periods.addPeriod(a.getStart(), a.getEnd());
                if (schWorkTime.intersect(periods).sum() > 0) {
                    // 和今天标准出勤时间有交集的，表示今天的请假
                    return true;
                } else {
                    return false;
                }
            }).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(todayLeaveApprovalList)) {
                // 请假时间段
                Periods leavePeriods = attendanceOldHelper.getMergedLeavePeriods(todayLeaveApprovalList);
                // 设置请假申请信息
                List<DailyAttendanceResult.LeaveApproval> leaveApprovals = new ArrayList<>();
                for (ApprovalLeave approvalLeave : todayLeaveApprovalList) {
                    leaveApprovals.add(DailyAttendanceResult.LeaveApproval.builder().spNo(approvalLeave.getSpNo()).start(approvalLeave.getStart()).end(approvalLeave.getEnd()).build());
                }
                leave.setLeaveApprovals(leaveApprovals);
                // 实际请假区间
                Periods actualLeavePeriods = schWorkTime.intersect(leavePeriods);
                leave.setActualLeavePeriods(actualLeavePeriods);
                // 设置请假总时长
                long leaveTime = actualLeavePeriods.sum();
                // 以30分钟为单位，超过10分钟算30分，否则忽略
                leaveTime = CheckinUtil.round(leaveTime, TimeUnit.MINUTES.toSeconds(30), TimeUnit.MINUTES.toSeconds(10));
                leave.setDuration(leaveTime);
                // 请假类型
                leave.setType(todayLeaveApprovalList.get(0).getType());
            }
        }
        return leave;
    }

    /**
     * 判断是否出差
     *
     * @param checkinDataList 打卡数据
     * @param approvals       审批单
     * @return 是否出差
     */
    public DailyAttendanceResult.BusinessTrip calcBusinessTrip(List<WxCpCheckinData> checkinDataList, List<ApprovalBusinessTrip> approvals) {
        DailyAttendanceResult.BusinessTrip businessTrip = new DailyAttendanceResult.BusinessTrip();
        // 出差需要有审批通过的出差单，并且打卡地点不在无锡
        boolean approved = true;
        if (WxAttendanceOptionProperties.businessTripNeedApproval) {
            // 若没有已经审批通过的出差单，则不算出差
            if (CollectionUtil.isEmpty(approvals)) {
                approved = false;
            }
        }
        boolean checkinNotInWuXi = false;
        for (WxCpCheckinData checkinData : checkinDataList) {
            // 先按照打卡机判断
            if (StrUtil.isNotBlank(checkinData.getLocationTitle())) {
                PunchMachine punchMachine = PunchMachine.getByName(checkinData.getLocationTitle());
                if (punchMachine.isNotInWuXi()) {
                    checkinNotInWuXi = true;
                    break;
                }
            }
            // 打卡地点不在无锡市，或在江阴市打卡，就算出差
            if (StrUtil.isNotBlank(checkinData.getLocationDetail()) && (!checkinData.getLocationDetail().contains("无锡市") || checkinData.getLocationDetail().contains("江阴市"))) {
                checkinNotInWuXi = true;
                businessTrip.setLocation(checkinData.getLocationDetail());
                break;
            }
        }
        businessTrip.setTrip(approved && checkinNotInWuXi);
        return businessTrip;
    }

    /**
     * 计算加班时间
     *
     * @param schWorkTime 标准工作时间段
     * @param approvals   审批单
     */
    public DailyAttendanceResult.OverTime calcOverTime(Periods schWorkTime, Periods schRestTime, DailyAttendanceResult.ActualCheckinTime actualCheckinTime,
                                                       List<ApprovalOverTime> approvals, boolean isWorkDay) {
        DailyAttendanceResult.OverTime overTime = new DailyAttendanceResult.OverTime();
        if (actualCheckinTime.getSingOutTime() == null) {
            return overTime;
        }

        // 加班区间
        Periods overTimePeriods = new Periods();

        boolean isOverWork = false;

        if (isWorkDay) {
            long schSignOutTime = schWorkTime.getMaxTime(); // 标准退勤时间
            if (schSignOutTime < actualCheckinTime.getSingOutTime()) {
                isOverWork = true;
                overTimePeriods.addPeriod(schSignOutTime, actualCheckinTime.getEffectiveSingOutTime());
            }
        } else {
            if (actualCheckinTime.getSingInTime() != null && actualCheckinTime.getSingOutTime() != null) {
                isOverWork = true;
                overTimePeriods.addPeriod(actualCheckinTime.getEffectiveSingInTime(), actualCheckinTime.getEffectiveSingOutTime());
            }
        }
        // 加班时长
        long overTimeDuration = 0;
        // 有效加班的加班区间
        Periods effectiveOverTimePeriods = null;
        // 如果实际加班了，则计算加班时长
        if (isOverWork) {
            // 处理加班需要审批的情况
            if (WxAttendanceOptionProperties.overTimeNeedApproval) {
                if (CollUtil.isNotEmpty(approvals)) {
                    // 过滤出今天的审批
                    List<ApprovalOverTime> todayOverTimeApprovalList = approvals.stream().filter(a -> {
                        Periods periods = new Periods();
                        periods.addPeriod(a.getStart(), a.getEnd());
                        if (periods.intersect(overTimePeriods).sum() > 0) {
                            // 和今天的实际加班有交集的，表示是今天的审批
                            return true;
                        } else {
                            return false;
                        }
                    }).collect(Collectors.toList());
                    // 如果加班需要审批，且没有已通过的加班审批记录，则不算加班，否则继续计算有效的加班时间
                    if (CollectionUtil.isNotEmpty(todayOverTimeApprovalList)) {
                        // 加班申请的加班时间段
                        Periods overTimeApprovalPeriods = attendanceOldHelper.getPeriodsFromApprovalVector(todayOverTimeApprovalList);
                        // 取实际的加班时间和申请的加班时间的交集，作为有效的加班时间
                        effectiveOverTimePeriods = overTimeApprovalPeriods.intersect(overTimePeriods);
                        // 设置加班审批数据
                        List<DailyAttendanceResult.OverTimeApproval> overTimeApprovalsResults = new ArrayList<>();
                        for (ApprovalOverTime approval : todayOverTimeApprovalList) {
                            overTimeApprovalsResults.add(DailyAttendanceResult.OverTimeApproval.builder().spNo(approval.getSpNo()).start(approval.getStart()).end(approval.getEnd()).build());
                        }
                        overTime.setOverTimeApprovals(overTimeApprovalsResults);
                    }
                }
            } else {
                // 若加班不需要审批，取实际的加班时间，作为有效的加班时间
                effectiveOverTimePeriods = overTimePeriods;
            }
            if (effectiveOverTimePeriods != null) {
                // 加班时长需要去掉休息时间
                Periods correctionalOverTimePeriods = effectiveOverTimePeriods.complement(schRestTime);
                overTime.setActualOverTimePeriods(correctionalOverTimePeriods);
                // 计算去掉休息时间之后的有效加班时长，并取整
                overTimeDuration = attendanceOldHelper.roundOverTime(correctionalOverTimePeriods.sum());
            }
        }
        overTime.setDuration(overTimeDuration);
        return overTime;
    }

    public long calcEffectiveWorkTime(Periods schWorkTime, DailyAttendanceResult.ActualCheckinTime actualCheckinTime, Periods schRestTime) {
        if (actualCheckinTime.getEffectiveSingInTime() == null || actualCheckinTime.getEffectiveSingOutTime() == null) {
            return 0;
        }
        Periods workTime = new Periods();
        if (schWorkTime == null) {
            workTime.addPeriod(actualCheckinTime.getEffectiveSingOutTime(), actualCheckinTime.getEffectiveSingInTime());
        } else {
            long workStartTime = Math.max(actualCheckinTime.getEffectiveSingInTime(), schWorkTime.getMinTime());
            workTime.addPeriod(workStartTime, actualCheckinTime.getEffectiveSingOutTime());
        }
        return workTime.complement(schRestTime).sum();
    }

    public List<ApprovalCalcPunchCorrection> filterPunchCorrection(Periods schWorkTime, List<ApprovalCalcPunchCorrection> punchCorrectionList) {
        List<ApprovalCalcPunchCorrection> approvalCalcPunchCorrections = new ArrayList<>();
        if (schWorkTime == null || schWorkTime.isEmpty()) {
            return approvalCalcPunchCorrections;
        }
        if (CollUtil.isNotEmpty(punchCorrectionList)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            approvalCalcPunchCorrections = punchCorrectionList.stream().filter(a -> {
                String minTime = format.format(new Date(schWorkTime.getMinTime() * 1000));
                String maxTime = format.format(new Date(schWorkTime.getMaxTime() * 1000));
                String checkInTime = format.format(new Date(a.getCheckInTime() * 1000));
                if (checkInTime.equals(maxTime) || checkInTime.equals(minTime)) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
        }
        return approvalCalcPunchCorrections;
    }
}
