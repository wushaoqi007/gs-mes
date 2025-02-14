package com.greenstone.mes.oa.response;

import lombok.*;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-09-16-9:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OaAttendanceMonthResultResp {

    private Double overTimeTotal;

    private Double leaveTotal;

    private List<OaAttendanceMonthResult> monthList;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class OaAttendanceMonthResult {
        /**
         * ID
         */
        private Long id;

        /**
         * 企业微信用户ID
         */
        private String wxUserId;

        /**
         * 企业微信企业ID
         */
        private String wxCpId;

        /**
         * 年
         */
        private Integer year;

        /**
         * 月
         */
        private Integer month;

        /**
         * 日
         */
        private Integer day;

        /**
         * 星期
         */
        private String week;

        /**
         * 是否工作日
         */
        private String isWorkDay;

        /**
         * 是否加班
         */
        private String isOverTime;

        /**
         * 是否请假
         */
        private String isLeave;

        /**
         * 考勤异常
         */
        private String abnormalAttendance;
    }
}
