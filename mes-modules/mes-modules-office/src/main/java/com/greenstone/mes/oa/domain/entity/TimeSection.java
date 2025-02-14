package com.greenstone.mes.oa.domain.entity;

import com.greenstone.mes.common.core.enums.SysError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.oa.infrastructure.util.Periods;

/**
 * @author gu_renkai
 * @date 2022/11/24 15:21
 */

public record TimeSection(Long start, Long end) {

    public TimeSection {
        if (start > end) {
            throw new ServiceException(SysError.E10002, "Error time section.");
        }
    }

    public Periods toPeriods() {
        Periods periods = new Periods();
        periods.addPeriod(start, end);
        return periods;
    }

}
