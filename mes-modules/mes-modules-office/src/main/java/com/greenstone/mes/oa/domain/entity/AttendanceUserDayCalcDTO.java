package com.greenstone.mes.oa.domain.entity;

import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.oa.infrastructure.enums.ScheduleShift;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/24 11:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceUserDayCalcDTO {

    @NotNull
    private Long dayBeginTime;
    @NotNull
    private CpId cpId;
    @NotNull
    private OaWxUser user;
    @NotNull
    private WxDept dept;
    /**
     * 排班：用于判断是否上班
     */
    @NotNull
    private Schedule schedule;
    /**
     * 班次：用于标准工作时间的计算
     */
    @NotNull
    private ScheduleShift shift;
    private List<CheckinData> checkinDataList;
    private List<CheckinData> allCheckinDataList;
    private TimeSection allowedCheckinTime;
    private List<ApprovalExtraWork> approvalExtraWorks;
    private List<ApprovalNight> approvalNights;
    private List<ApprovalVacation> approvalVacations;
    private List<ApprovalCorrection> approvalCorrections;

    private List<CustomShift> customShifts;
    
}
