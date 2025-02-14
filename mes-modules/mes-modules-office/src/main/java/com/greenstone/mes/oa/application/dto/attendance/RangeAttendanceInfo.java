package com.greenstone.mes.oa.application.dto.attendance;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinData;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinSchedule;
import me.chanjar.weixin.cp.bean.oa.WxCpCropCheckinOption;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RangeAttendanceInfo {

    private String deptName;

    private WxCpCheckinSchedule.UserSchedule.Schedule schedule;

    /**
     * 所有打卡规则
     */
    private List<WxCpCropCheckinOption> checkinOptionList;

    /**
     * 所有打卡记录
     */
    private List<WxCpCheckinData> checkinDataList;

    /**
     * 所有夜班审批
     */
    private List<ApprovalNightShift> approvalNightShiftList;

    /**
     * 所有加班审批
     */
    private List<ApprovalOverTime> approvalOverTimeList;

    /**
     * 所有请假审批
     */
    private List<ApprovalLeave> approvalLeaveList;

    /**
     * 所有出差审批
     */
    private List<ApprovalBusinessTrip> approvalBusinessTripList;

    /**
     * 所有补卡审批
     */
    private List<ApprovalCalcPunchCorrection> approvalCalcPunchCorrectionList;

    public RangeAttendanceInfo filterByUserId(String userId) {
        RangeAttendanceInfo someoneAttendance = new RangeAttendanceInfo();
        someoneAttendance.setCheckinDataList(this.filterCheckinData(userId));
        someoneAttendance.setApprovalOverTimeList(this.filterApprovalOverTime(userId));
        someoneAttendance.setApprovalLeaveList(this.filterApprovalLeave(userId));
        someoneAttendance.setApprovalBusinessTripList(this.filterApprovalBusinessTrip(userId));
        someoneAttendance.setApprovalNightShiftList(this.filterApprovalNightShift(userId));
        someoneAttendance.setApprovalCalcPunchCorrectionList(this.filterApprovalPunchCorrection(userId));
        return someoneAttendance;
    }

    private List<WxCpCheckinData> filterCheckinData(String userId) {
        if (CollUtil.isEmpty(checkinDataList)) {
            return Collections.emptyList();
        }
        return checkinDataList.stream().filter(x -> x.getUserId().equals(userId)).collect(Collectors.toList());
    }

    private List<ApprovalNightShift> filterApprovalNightShift(String userId) {
        if (CollUtil.isEmpty(approvalNightShiftList)) {
            return Collections.emptyList();
        }
        return approvalNightShiftList.stream().filter(x -> x.getUserId().equals(userId)).collect(Collectors.toList());
    }

    private List<ApprovalOverTime> filterApprovalOverTime(String userId) {
        if (CollUtil.isEmpty(approvalOverTimeList)) {
            return Collections.emptyList();
        }
        return approvalOverTimeList.stream().filter(x -> x.getUserId().equals(userId)).collect(Collectors.toList());
    }

    private List<ApprovalLeave> filterApprovalLeave(String userId) {
        if (CollUtil.isEmpty(approvalLeaveList)) {
            return Collections.emptyList();
        }
        return approvalLeaveList.stream().filter(x -> x.getUserId().equals(userId)).collect(Collectors.toList());
    }

    private List<ApprovalBusinessTrip> filterApprovalBusinessTrip(String userId) {
        if (CollUtil.isEmpty(approvalBusinessTripList)) {
            return Collections.emptyList();
        }
        return approvalBusinessTripList.stream().filter(x -> x.getUserId().equals(userId)).collect(Collectors.toList());
    }

    private List<ApprovalCalcPunchCorrection> filterApprovalPunchCorrection(String userId) {
        if (CollUtil.isEmpty(approvalCalcPunchCorrectionList)) {
            return Collections.emptyList();
        }
        return approvalCalcPunchCorrectionList.stream().filter(x -> x.getUserId().equals(userId)).collect(Collectors.toList());
    }


}
