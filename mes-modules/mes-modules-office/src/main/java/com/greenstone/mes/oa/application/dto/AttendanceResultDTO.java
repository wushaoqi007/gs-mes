package com.greenstone.mes.oa.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 考勤结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResultDTO {

    /**
     * 姓名
     */
    private String userName;

    /**
     * id
     */
    private String userId;

    /**
     * 电话
     */
    private String tel;

    /**
     * 部门
     */
    private String deptName;

    /**
     * 查询考勤开始时间字符串
     */
    private String startDate;

    /**
     * 查询考勤结束时间字符串
     */
    private String endDate;

    /**
     * 日期
     */
    private long dayTime;

    /**
     * 上班时间
     */
    private Long onDutyTime;

    /**
     * 下班时间
     */
    private Long offDutyTime;

    /**
     * 工作时长
     */
    private Long workTime;


    /**
     * 迟到
     */
    private LateDetail late;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LateDetail {
        /**
         * 迟到时间
         */
        private Long lateTime;
        /**
         * 是否迟到(10分钟以内)
         */
        private boolean isLate;
    }


    /**
     * 早退
     */
    private EarlyDetail early;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EarlyDetail {
        /**
         * 早退时间
         */
        private Long earlyTime;
        /**
         * 是否早退(10分钟以内)
         */
        private boolean isEarly;
    }

    /**
     * 旷工时间
     */
    private Long absenteeismTime;

    /**
     * 加班时间
     */
    private Long overTime;

    /**
     * 请假
     */
    private List<DayOff> dayOffs;

    /**
     * 状态
     */
    private String status;

    /**
     * 是否出差
     */
    private String isBusinessTrip;

    /**
     * 当天打卡次数
     */
    private Integer count;

    /**
     * 打卡补卡次数
     */
    private Integer correctionCount;

    /**
     * 打卡详情
     */
    private List<AttendanceDetail> detail;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendanceDetail {
        /**
         * 打卡时间
         */
        private Long checkinTime;
        /**
         * 打卡地点
         */
        private String locationDetail;

        /**
         * 打卡备注
         */
        private String notes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayOff {
        /**
         * 请假类型
         */
        private String type;

        /**
         * 请假时间
         */
        private long time;

        /**
         * 请假开始时间
         */
        private long starTime;

        /**
         * 请假结束时间
         */
        private long endTime;

        /**
         * 时长
         */
        private long length;
    }

}
