package com.greenstone.mes.oa.domain.service.impl;

import com.greenstone.mes.oa.domain.entity.AttendanceResultDetail;
import com.greenstone.mes.oa.domain.entity.AttendanceUserDayCalcDTO;
import com.greenstone.mes.oa.domain.service.AttendanceCalcService;
import com.greenstone.mes.oa.domain.service.handler.AttendanceCalcHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu_renkai
 * @date 2022/11/30 11:45
 */
@Service
public class AttendanceCalcServiceImpl implements AttendanceCalcService {

    private final List<AttendanceCalcHandler> handlers = new ArrayList<>();

    @Override
    public void addHandler(AttendanceCalcHandler handler) {
        this.handlers.add(handler);
    }

    @Override
    public AttendanceResultDetail calc(AttendanceUserDayCalcDTO calcData) {
        AttendanceResultDetail result = null;
        Map<String, Object> context = new HashMap<>();
        for (AttendanceCalcHandler handler : handlers) {
            AttendanceResultDetail resultDetail = handler.handle(calcData, context);
            if (resultDetail != null) {
                result = resultDetail;

            }
        }
        return result;
    }


}
