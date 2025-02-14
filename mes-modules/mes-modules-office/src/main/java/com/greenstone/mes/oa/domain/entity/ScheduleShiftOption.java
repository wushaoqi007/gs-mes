package com.greenstone.mes.oa.domain.entity;

import com.greenstone.mes.wxcp.domain.types.SpNo;
import com.greenstone.mes.oa.infrastructure.enums.ApprovalStatus;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.cp.bean.oa.WxCpCropCheckinOption;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 班次切换选项
 *
 * @author wushaoqi
 * @date 2023-02-06-8:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleShiftOption {

    @NotNull
    private CpId cpId;
    @NotNull
    private SpNo spNo;
    @NotNull
    private Date startTime;
    @NotNull
    private Date endTime;
    @NotNull
    private WxUserId userId;
    @NotNull
    private WxCpCropCheckinOption checkinOption;
    @NotNull
    private WxCpCropCheckinOption.Schedule checkinOptionSchedule;
    @NotNull
    private String spName;
    @NotNull
    private String scheduleName;
    @NotNull
    private ApprovalStatus status;
}
