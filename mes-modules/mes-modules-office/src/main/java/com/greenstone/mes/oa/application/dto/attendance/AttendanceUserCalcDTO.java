package com.greenstone.mes.oa.application.dto.attendance;

import com.greenstone.mes.oa.domain.entity.*;
import com.greenstone.mes.wxcp.domain.types.CpId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/29 16:26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceUserCalcDTO {
    private CpId cpId;
    private OaWxUser user;
    private WxDept dept;
    private List<CheckinData> checkinDataList;
    private List<ApprovalExtraWork> approvalExtraWorks;
    private List<ApprovalNight> approvalNights;
    private List<ApprovalTemporaryChange> approvalTemporaryChanges;
    private List<ApprovalVacation> approvalVacations;
    private List<ApprovalCorrection> approvalCorrections;
    private List<Schedule> schedules;
    private List<CustomShift> customShifts;

}
