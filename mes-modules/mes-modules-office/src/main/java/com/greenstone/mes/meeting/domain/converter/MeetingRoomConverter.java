package com.greenstone.mes.meeting.domain.converter;

import com.greenstone.mes.meeting.domain.entity.MeetingRoom;
import com.greenstone.mes.meeting.domain.entity.MeetingRoomReserve;
import com.greenstone.mes.meeting.infrastructure.persistence.MeetingRoomDO;
import com.greenstone.mes.meeting.infrastructure.persistence.MeetingRoomReserveDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MeetingRoomConverter {

    // MeetingRoom
    MeetingRoomDO entity2Do(MeetingRoom meetingRoom);

    List<MeetingRoomDO> entities2Dos(List<MeetingRoom> meetingRooms);


    MeetingRoom do2Entity(MeetingRoomDO meetingRoomDO);

    List<MeetingRoom> dos2Entities(List<MeetingRoomDO> meetingRoomDOS);

}
