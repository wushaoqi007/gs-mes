package com.greenstone.mes.oa.infrastructure.enums;

import com.greenstone.mes.oa.domain.entity.CustomShift;
import lombok.Builder;
import lombok.Data;

/**
 * 班次:用于计算
 */
@Data
@Builder
public class ScheduleShift {

    private int id;

    private String name;

    private int workSec;

    private int offWorkSec;

    private CustomShift customShift;

    public boolean isNightShift() {
        return this.id == DefaultShift.NIGHT.getId();
    }

    public boolean isDayShift() {
        return this.id == DefaultShift.DAY.getId();
    }

}
