package com.greenstone.mes.oa.domain.service.handler;

import com.greenstone.mes.oa.domain.entity.AttendanceResultDetail;
import com.greenstone.mes.oa.domain.entity.AttendanceUserDayCalcDTO;

import java.util.Map;

/**
 * @author gu_renkai
 * @date 2022/11/29 17:01
 */

public interface AttendanceCalcHandler {

    AttendanceResultDetail handle(AttendanceUserDayCalcDTO calcData, Map<String, Object> context);

    default void setDetail(Map<String, Object> context, AttendanceResultDetail result) {
        context.put("result", result);
    }

    default AttendanceResultDetail getDetail(Map<String, Object> context) {
        return (AttendanceResultDetail) context.get("result");
    }

}
