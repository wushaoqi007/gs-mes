package com.greenstone.mes.oa.domain.entity;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.oa.infrastructure.util.Periods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/24 15:21
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSections {

    private List<TimeSection> sections;

    public Periods toPeriods() {
        Periods periods = new Periods();
        if (CollUtil.isNotEmpty(sections)) {
            sections.forEach(s -> periods.addPeriod(s.start(), s.end()));
        }
        return periods;
    }

}
