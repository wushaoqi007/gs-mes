package com.greenstone.mes.oa.domain.service.handler;

import com.greenstone.mes.oa.domain.entity.AttendanceResultDetail;
import com.greenstone.mes.oa.domain.entity.AttendanceUserDayCalcDTO;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author gu_renkai
 * @date 2022/11/30 11:38
 */
@Component
public class ResultHandler implements AttendanceCalcHandler {
    
    @Override
    public AttendanceResultDetail handle(AttendanceUserDayCalcDTO calcData, Map<String, Object> context) {
        return getDetail(context);
    }
}
