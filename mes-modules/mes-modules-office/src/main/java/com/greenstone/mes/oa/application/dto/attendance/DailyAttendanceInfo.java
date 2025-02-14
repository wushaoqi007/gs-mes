package com.greenstone.mes.oa.application.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.cp.bean.WxCpUser;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinData;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinSchedule;
import me.chanjar.weixin.cp.bean.oa.WxCpCropCheckinOption;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyAttendanceInfo {

    private WxCpUser user;

    private String userId;

    private String nickName;

    private String phoneNumber;

    private String departName;

    private WxCpCheckinSchedule.UserSchedule.Schedule schedule;

    /**
     * 允许打卡的时间范围
     */
    private Long[] allowedCheckinRange;

    /**
     * 当天0点
     */
    private Long dayBeginTime;

    /**
     * 当天的打卡规则
     */
    private WxCpCropCheckinOption checkinOption;

    /**
     * 此用户当天的打卡记录
     */
    private List<WxCpCheckinData> checkinDataList;

    /**
     * 此用户当天有效的打卡记录
     */
    private List<WxCpCheckinData> effectiveCheckinDataList;

    /**
     * 夜班审批
     */
    private List<ApprovalNightShift> approvalNightShiftList;

    /**
     * 加班审批
     */
    private List<ApprovalOverTime> approvalOverTimeList;

    /**
     * 请假审批
     */
    private List<ApprovalLeave> approvalLeaveList;

    /**
     * 出差审批
     */
    private List<ApprovalBusinessTrip> approvalBusinessTripList;

    /**
     * 打卡补卡审批
     */
    private List<ApprovalCalcPunchCorrection> punchCorrectionList;

}
