package com.greenstone.mes.oa.domain.service.handler;

import com.greenstone.mes.oa.domain.entity.AttendanceResultDetail;
import com.greenstone.mes.oa.domain.entity.AttendanceUserDayCalcDTO;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author gu_renkai
 * @date 2022/11/30 11:06
 */
@Component
public class PrepareHandler implements AttendanceCalcHandler {
    @Override
    public AttendanceResultDetail handle(AttendanceUserDayCalcDTO calcData, Map<String, Object> context) {
        AttendanceResultDetail detail = AttendanceResultDetail.builder().dayBeginTime(calcData.getDayBeginTime())
                .cpId(calcData.getCpId())
                .user(calcData.getUser())
                .dept(calcData.getDept())
                .checkinDataList(calcData.getCheckinDataList()).build();
        setDetail(context, detail);
        return null;
    }
}
