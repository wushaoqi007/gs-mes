package com.greenstone.mes.oa.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.oa.application.assembler.AttendanceAssembler;
import com.greenstone.mes.oa.application.service.OaWxScheduleService;
import com.greenstone.mes.oa.domain.OaWxScheduleDO;
import com.greenstone.mes.oa.domain.entity.Schedule;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.oa.infrastructure.mapper.OaWxScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-06-29-9:38
 */
@Service
public class OaWxScheduleServiceImpl extends ServiceImpl<OaWxScheduleMapper, OaWxScheduleDO> implements OaWxScheduleService {


    private final AttendanceAssembler attendanceAssembler;

    @Autowired
    public OaWxScheduleServiceImpl(AttendanceAssembler attendanceAssembler) {
        this.attendanceAssembler = attendanceAssembler;
    }

    @Override
    public List<Schedule> listSchedule(CpId cpId, Date startTime, Date endTime, List<String> userList) {
        List<OaWxScheduleDO> list = listScheduleDO(cpId, startTime, endTime, userList);
        return attendanceAssembler.toSchedules(list);
    }

    @Override
    public List<OaWxScheduleDO> listScheduleDO(CpId cpId, Date startTime, Date endTime, List<String> userList) {
        QueryWrapper<OaWxScheduleDO> queryWrapper = new QueryWrapper<>();
        SimpleDateFormat formatYearMonth = new SimpleDateFormat("yyyyMMdd");
        String start = formatYearMonth.format(startTime);
        String end = formatYearMonth.format(endTime);
        queryWrapper.lambda().eq(OaWxScheduleDO::getCpId, cpId.id())
                .ge(OaWxScheduleDO::getScheduleDate, start).le(OaWxScheduleDO::getScheduleDate, end)
                .in(OaWxScheduleDO::getUserId, userList);
        return list(queryWrapper);
    }
}
