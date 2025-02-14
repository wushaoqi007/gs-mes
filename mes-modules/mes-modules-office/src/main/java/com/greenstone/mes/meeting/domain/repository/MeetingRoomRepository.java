package com.greenstone.mes.meeting.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.enums.MeetingError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.form.domain.helper.FormHelper;
import com.greenstone.mes.form.domain.repository.AbstractFormDataRepository;
import com.greenstone.mes.meeting.domain.converter.MeetingRoomConverter;
import com.greenstone.mes.meeting.domain.entity.MeetingRoom;
import com.greenstone.mes.meeting.infrastructure.mapper.MeetingRoomMapper;
import com.greenstone.mes.meeting.infrastructure.persistence.MeetingRoomDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-10-27-10:19
 */
@Slf4j
@Service
public class MeetingRoomRepository extends AbstractFormDataRepository<MeetingRoom, MeetingRoomDO, MeetingRoomMapper> {

    private final MeetingRoomMapper meetingRoomMapper;
    private final MeetingRoomConverter converter;

    public MeetingRoomRepository(FormHelper formHelper, MeetingRoomMapper mapper, MeetingRoomMapper meetingRoomMapper,
            MeetingRoomConverter converter) {
        super(formHelper, mapper);
        this.meetingRoomMapper = meetingRoomMapper;
        this.converter = converter;
    }

    public List<MeetingRoom> queryAllMeetingRoom() {
        LambdaQueryWrapper<MeetingRoomDO> queryWrapper = Wrappers.lambdaQuery(MeetingRoomDO.class)
                .orderByDesc(MeetingRoomDO::getCreateTime);
        List<MeetingRoomDO> meetingRoomDOS = meetingRoomMapper.selectList(queryWrapper);
        return converter.dos2Entities(meetingRoomDOS);
    }

    public MeetingRoom getMeetingRoomById(String id) {
        return converter.do2Entity(meetingRoomMapper.selectById(id));
    }

    public void addMeetingRoom(MeetingRoom meetingRoom) {
        MeetingRoomDO query = MeetingRoomDO.builder().roomName(meetingRoom.getRoomName()).build();
        MeetingRoomDO selectDo = meetingRoomMapper.getOneOnly(query);
        if (Objects.nonNull(selectDo)) {
            log.info("会议室名称不能重复,已有会议室信息：{}", selectDo);
            throw new ServiceException(MeetingError.E130101);
        }
        MeetingRoomDO meetingRoomDO = converter.entity2Do(meetingRoom);
        meetingRoomMapper.insert(meetingRoomDO);
    }

    public void updateMeetingRoom(MeetingRoom meetingRoom) {
        MeetingRoomDO query = MeetingRoomDO.builder().roomName(meetingRoom.getRoomName()).build();
        MeetingRoomDO selectDo = meetingRoomMapper.getOneOnly(query);
        if (Objects.nonNull(selectDo) && !selectDo.getId().equals(meetingRoom.getId())) {
            log.info("会议室名称不能重复,已有会议室信息：{}", selectDo);
            throw new ServiceException(MeetingError.E130101);
        }
        MeetingRoomDO meetingRoomDO = converter.entity2Do(meetingRoom);
        meetingRoomMapper.updateById(meetingRoomDO);
    }

    public void delete(List<String> ids) {
        LambdaQueryWrapper<MeetingRoomDO> wrapper = Wrappers.lambdaQuery(MeetingRoomDO.class).in(MeetingRoomDO::getId, ids);
        meetingRoomMapper.delete(wrapper);
    }
}
