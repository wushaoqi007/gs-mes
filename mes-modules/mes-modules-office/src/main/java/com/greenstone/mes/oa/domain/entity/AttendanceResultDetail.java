package com.greenstone.mes.oa.domain.entity;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.oa.infrastructure.enums.ScheduleShift;
import com.greenstone.mes.oa.infrastructure.util.Periods;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/24 13:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResultDetail {

    private Long dayBeginTime;

    private CpId cpId;

    private OaWxUser user;

    private WxDept dept;

    private List<CheckinData> checkinDataList;

    private boolean workDay;

    /**
     * 班次：用于计算
     */
    private ScheduleShift shift;

    private CheckinTime checkinTime;

    private RestTime restTime;

    private Trip trip;

    private Vacation vacation;

    private ComeLate comeLate;

    private LeaveEarly leaveEarly;

    private Absenteeism absenteeism;

    private ExtraWork extraWork;

    private Integer workTime;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CheckinTime {
        private CheckinData singInTime;

        private CheckinData singOutTime;

        private Integer checkinTimes;

        public boolean isAbsent() {
            return singInTime == null || singOutTime == null;
        }

        public String getCheckinLocation() {
            if (singInTime == null) {
                return "";
            }
            if (StrUtil.isNotEmpty(singInTime.getLocationDetail()) && !singInTime.getLocationDetail().contains("null")) {
                return singInTime.getLocationDetail();
            } else {
                return singInTime.getLocationTitle();
            }
        }

        public String getCheckinLocationSecond() {
            if (singOutTime == null) {
                return "";
            }
            if (StrUtil.isNotEmpty(singOutTime.getLocationDetail()) && !singOutTime.getLocationDetail().contains("null")) {
                return singOutTime.getLocationDetail();
            } else {
                return singOutTime.getLocationTitle();
            }
        }

        public String getCheckinRemark() {
            if (singInTime == null) {
                return "";
            }
            return singInTime.getNotes();
        }

        public String getCheckinRemarkSecond() {
            if (singOutTime == null) {
                return "";
            }
            return singOutTime.getNotes();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RestTime {

        private TimeSections restSections;

        public Periods toPeriods() {
            if (restSections == null) {
                return new Periods();
            } else {
                return restSections.toPeriods();
            }
        }

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ComeLate {

        private boolean late;

        private Integer duration;

        private boolean remit;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LeaveEarly {

        private boolean early;

        private Long allowedLateSec;

        private Integer duration;

        private boolean remit;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Absenteeism {

        private boolean absenteeism;

        private Integer duration;

        private String reason;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExtraWork {

        private boolean extraWork;

        private Integer duration;

        private TimeSections sections;

        public Periods toPeriods() {
            if (sections == null) {
                return new Periods();
            } else {
                return sections.toPeriods();
            }
        }

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Vacation {

        private boolean vacation;

        private Integer duration;

        private String type;

        private TimeSections sections;

        public Periods toPeriods() {
            if (sections == null) {
                return new Periods();
            } else {
                return sections.toPeriods();
            }
        }

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Trip {

        private boolean trip;

        private String location;

    }

}
