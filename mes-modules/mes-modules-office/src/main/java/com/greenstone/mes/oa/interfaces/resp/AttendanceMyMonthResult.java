package com.greenstone.mes.oa.interfaces.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
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
public class AttendanceMyMonthResult {

    private String totalExtraWorkTime;

    private String totalVacationTime;

    private List<AttendanceMyDayResult> dayAttendances;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class AttendanceMyDayResult {

        /**
         * 企业微信用户ID
         */
        private String wxUserId;

        private String userName;
        private String deptName;

        /**
         * 企业微信企业ID
         */
        private String wxCpId;

        /**
         * 日期
         */
        @JsonFormat(pattern = "yyyy-MM-dd")
        private Date day;

        /**
         * 班次
         */
        private String shift;

        @JsonFormat(pattern = "H:mm", timezone = "GMT+8")
        private Date signInTime;

        @JsonFormat(pattern = "H:mm", timezone = "GMT+8")
        private Date signOutTime;

        private String checkinLocation;

        /**
         * 是否工作日
         */
        @JsonProperty("isWorkDay")
        private boolean workDay;

        @JsonProperty("isTrip")
        private boolean trip;

        private String extraWorkTime;

        private List<ApprovalInfo> extraWorkApprovals;
        private List<ApprovalInfo> vacationApprovals;
        private List<ApprovalInfo> nightApprovalList;
        private List<ApprovalInfo> temporaryChangeApprovals;
        private List<CorrectionInfo> punchCorrectionApprovals;

        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        @Data
        public static class ApprovalInfo{
            @JsonFormat(pattern = "yyyy/MM/dd H:mm")
            private Date startTime;
            @JsonFormat(pattern = "yyyy/MM/dd H:mm")
            private Date endTime;
            @JsonFormat(pattern = "yyyy/MM/dd H:mm")
            private Date applyTime;
            private String status;
        }

        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        @Data
        public static class CorrectionInfo{
            @JsonFormat(pattern = "yyyy/MM/dd H:mm")
            private Date correctionTime;
            @JsonFormat(pattern = "yyyy/MM/dd H:mm")
            private Date applyTime;
            private String status;
        }

        private String vacationTime;

        private String exceptionType;

        private String exceptionTime;


    }
}
