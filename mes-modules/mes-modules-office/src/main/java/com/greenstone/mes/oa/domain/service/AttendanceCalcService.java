package com.greenstone.mes.oa.domain.service;

import com.greenstone.mes.oa.domain.entity.AttendanceResultDetail;
import com.greenstone.mes.oa.domain.entity.AttendanceUserDayCalcDTO;
import com.greenstone.mes.oa.domain.service.handler.AttendanceCalcHandler;

/**
 * @author gu_renkai
 * @date 2022/11/30 13:52
 */

public interface AttendanceCalcService {

    void addHandler(AttendanceCalcHandler handler);

    AttendanceResultDetail calc(AttendanceUserDayCalcDTO calcData);
}
