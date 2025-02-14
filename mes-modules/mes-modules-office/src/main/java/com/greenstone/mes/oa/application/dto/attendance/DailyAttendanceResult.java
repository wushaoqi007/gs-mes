package com.greenstone.mes.oa.application.dto.attendance;

import com.greenstone.mes.oa.infrastructure.enums.ScheduleShift;
import com.greenstone.mes.oa.infrastructure.util.Periods;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinData;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyAttendanceResult {

    /**
     * 当天0点时间戳
     */
    private Long dayBeginTime;

    /**
     * 是否工作日
     */
    private Boolean workDay;

    /**
     * 班次
     */
    private ScheduleShift actualShift;

    /**
     * 允许打卡时间
     */
    private AllowedCheckinTime allowedCheckinTime;

    /**
     * 当天的所有打卡数据
     */
    private List<WxCpCheckinData> todayCheckinDataList;

    /**
     * 当天的有效打卡数据
     */
    private List<WxCpCheckinData> effectiveCheckinDataList;

    /**
     * 实际打卡时间
     */
    private ActualCheckinTime actualCheckinTime;

    /**
     * 当天的标准打卡时间
     */
    private Periods schCheckinTime;

    /**
     * 当天的标准休息时间
     */
    private Periods schRestTime;

    /**
     * 当天的标准工作时间
     */
    private Periods schWorkTime;

    /**
     * 当天的请假审批
     */
    private List<ApprovalLeave> approvalLeaveList;

    /**
     * 工作时长（秒）
     */
    private Long workTime;

    /**
     * 迟到数据
     */
    private ComeLate comeLate;

    /**
     * 早退数据
     */
    private LeaveEarly leaveEarly;

    /**
     * 旷工数据
     */
    private Absenteeism absenteeism;

    /**
     * 请假数据
     */
    private Leave leave;

    /**
     * 出差
     */
    private BusinessTrip businessTrip;

    /**
     * 加班数据
     */
    private OverTime overTime;

    private List<ApprovalCalcPunchCorrection> punchCorrectionList;

    /**
     * @return 是否为工作日
     */
    public boolean isWorkDay() {
        return this.actualShift == null;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AllowedCheckinTime {
        private Long start;

        private Long end;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActualCheckinTime {
        private Long singInTime;

        private Long singOutTime;

        private Long effectiveSingInTime;

        private Long effectiveSingOutTime;

        private String signInType;

        private String signOutType;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ComeLate {

        /**
         * 是否迟到
         */
        private boolean late;

        /**
         * 迟到时间（秒）
         */
        private Long duration;

        /**
         * 豁免
         */
        private boolean exemption;

        /**
         * 剩余豁免次数
         */
        private Integer remainingExemptionTimes;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LeaveEarly {

        /**
         * 是否早退
         */
        private boolean early;

        /**
         * 早退时间（秒）
         */
        private Long duration;

        /**
         * 豁免
         */
        private boolean exemption;

        /**
         * 剩余豁免次数
         */
        private Integer remainingExemptionTimes;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Absenteeism {

        /**
         * 是否缺卡
         */
        private boolean lackOfCheckin;

        /**
         * 旷工时间（秒）
         */
        private Long duration;

        /**
         * 旷工原因
         */
        private String reason;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PunchCorrection {

        /**
         * 补打卡次数
         */
        private Integer count;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Leave {
        /**
         * 请假总时长（秒）
         */
        private Long duration;

        /**
         * 请假类型
         */
        private String type;

        /**
         * 实际请假区间
         */
        private Periods actualLeavePeriods;

        /**
         * 请假申请
         */
        private List<LeaveApproval> leaveApprovals;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LeaveApproval {
        /**
         * 请假时长（秒）
         */
        private Long duration;

        /**
         * 开始时间（unix时间戳）
         */
        private Long start;

        /**
         * 结束时间（unix时间戳）
         */
        private Long end;

        /**
         * 请假类型
         */
        private String type;

        /**
         * 申请时间（unix时间戳）
         */
        private Long applyTime;

        /**
         * 单号
         */
        private String spNo;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BusinessTrip {
        /**
         * 是否出差
         */
        private boolean isTrip;

        /**
         * 出差地点
         */
        private String location;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OverTime {
        /**
         * 加班总时长（秒）
         */
        private Long duration;

        /**
         * 实际请假区间
         */
        private Periods actualOverTimePeriods;

        /**
         * 加班申请
         */
        List<OverTimeApproval> overTimeApprovals;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OverTimeApproval {
        /**
         * 加班时长（秒）
         */
        private Long duration;

        /**
         * 加班开始时间
         */
        private Long start;

        /**
         * 加班结束时间
         */
        private Long end;

        /**
         * 申请时间（unix时间戳）
         */
        private Long applyTime;

        /**
         * 单号
         */
        private String spNo;
    }
}
