package com.greenstone.mes.oa.domain.entity;

import com.greenstone.mes.oa.infrastructure.enums.AttendanceExceptionType;
import com.greenstone.mes.oa.infrastructure.enums.ScheduleShift;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author gu_renkai
 * @date 2022/11/28 13:32
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceResult {

    private Long id;
    private CpId cpId;
    private WxUserId userId;
    private Date day;
    private boolean workDay;
    private ScheduleShift shift;
    private String checkinLocation;
    private String checkinLocationSecond;
    private String checkinRemark;
    private String checkinRemarkSecond;
    private Integer checkinTimes;
    private String customShiftName;
    private Date schSignInTime;
    private Date schSignOutTime;
    private Date signInTime;
    private Date signOutTime;
    private boolean trip;
    private Integer workTime;
    private Integer extraWorkTime;
    private Integer vacationTime;
    private String vacationType;
    private AttendanceExceptionType exceptionType;
    private Integer exceptionTime;
    private AttendanceExceptionType remitType;
    private Integer lateEarlyRemitTimes;
    private AttendanceExceptionType correctType;
    private Integer correctRemitTimes;

}
