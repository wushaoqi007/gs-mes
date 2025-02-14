package com.greenstone.mes.meeting.domain.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.meeting.application.dto.cqe.query.MeetingRoomReserveCheckQuery;
import com.greenstone.mes.meeting.application.dto.cqe.query.MeetingRoomReserveQuery;
import com.greenstone.mes.meeting.domain.converter.MeetingRoomReserveConverter;
import com.greenstone.mes.meeting.domain.entity.MeetingRoomReserve;
import com.greenstone.mes.meeting.infrastructure.enums.MeetingRoomReserveStatus;
import com.greenstone.mes.meeting.infrastructure.mapper.MeetingRoomReserveMapper;
import com.greenstone.mes.meeting.infrastructure.persistence.MeetingRoomReserveDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-10-27-10:19
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MeetingRoomReserveRepository {

    private final MeetingRoomReserveMapper meetingRoomReserveMapper;
    private final MeetingRoomReserveConverter converter;

    public List<MeetingRoomReserve> queryMeetingRoomReserve(MeetingRoomReserveQuery query) {
        return meetingRoomReserveMapper.queryMeetingRoomReserve(query);
    }

    public List<MeetingRoomReserve> querySameDayReserve(MeetingRoomReserveCheckQuery query) {
        LambdaQueryWrapper<MeetingRoomReserveDO> queryWrapper = Wrappers.lambdaQuery(MeetingRoomReserveDO.class)
                .eq(MeetingRoomReserveDO::getRoomId, query.getRoomId())
                .eq(MeetingRoomReserveDO::getUseDate, query.getUseDate())
                .ne(MeetingRoomReserveDO::getStatus, MeetingRoomReserveStatus.ENDED.getState())
                .ne(StrUtil.isNotEmpty(query.getId()), MeetingRoomReserveDO::getId, query.getId())
                .orderByDesc(MeetingRoomReserveDO::getCreateTime);
        List<MeetingRoomReserveDO> meetingRoomReserveDOS = meetingRoomReserveMapper.selectList(queryWrapper);
        return converter.dos2Entities(meetingRoomReserveDOS);
    }

    public List<MeetingRoomReserve> queryNotEndedReserve() {
        LambdaQueryWrapper<MeetingRoomReserveDO> queryWrapper = Wrappers.lambdaQuery(MeetingRoomReserveDO.class)
                .ne(MeetingRoomReserveDO::getStatus, MeetingRoomReserveStatus.ENDED.getState());
        List<MeetingRoomReserveDO> meetingRoomReserveDOS = meetingRoomReserveMapper.selectList(queryWrapper);
        return converter.dos2Entities(meetingRoomReserveDOS);
    }

    public MeetingRoomReserve getMeetingRoomReserveById(String id) {
        return meetingRoomReserveMapper.getMeetingRoomReserveById(id);
    }

    public void addMeetingRoomReserve(MeetingRoomReserve meetingRoomReserve) {
        MeetingRoomReserveDO meetingRoomReserveDO = converter.entity2Do(meetingRoomReserve);
        meetingRoomReserveDO.setStatus(MeetingRoomReserveStatus.NOT_STARTED.getState());
        meetingRoomReserveDO.setReserveTime(LocalDateTime.now());
        meetingRoomReserveMapper.insert(meetingRoomReserveDO);
    }

    public void updateMeetingRoomReserve(MeetingRoomReserve meetingRoomReserve) {
        MeetingRoomReserveDO meetingRoomReserveDO = converter.entity2Do(meetingRoomReserve);
        meetingRoomReserveMapper.updateById(meetingRoomReserveDO);
    }

    public void delete(List<String> ids) {
        LambdaQueryWrapper<MeetingRoomReserveDO> wrapper = Wrappers.lambdaQuery(MeetingRoomReserveDO.class).in(MeetingRoomReserveDO::getId, ids);
        meetingRoomReserveMapper.delete(wrapper);
    }

    public void end(List<String> ids) {
        for (String id : ids) {
            MeetingRoomReserveDO meetingRoomReserveDO = meetingRoomReserveMapper.selectById(id);
            if (meetingRoomReserveDO != null && MeetingRoomReserveStatus.ENDED.getState() != meetingRoomReserveDO.getStatus()) {
                meetingRoomReserveDO.setStatus(MeetingRoomReserveStatus.ENDED.getState());
                meetingRoomReserveMapper.updateById(meetingRoomReserveDO);
            }
        }
    }
}
