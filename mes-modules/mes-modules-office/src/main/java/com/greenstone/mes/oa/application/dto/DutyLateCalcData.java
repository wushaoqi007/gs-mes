package com.greenstone.mes.oa.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DutyLateCalcData {

    /**
     * 打卡时间 秒
     */
    private long checkinTime;

    /**
     * 标准打卡时间 秒
     */
    private long schCheckinTime;

    /**
     * 允许迟到的时间 毫秒
     */
    private long flexOnDutyMillis;

}
