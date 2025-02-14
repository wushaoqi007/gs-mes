package com.greenstone.mes.oa.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DutyLateResult {

    /**
     * 迟到的秒数
     */
    private long lateMinutes;

    /**
     * 是否使用迟到次数
     */
    private boolean useLateChance;

}
