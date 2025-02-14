package com.greenstone.mes.meeting.domain.converter;

import com.greenstone.mes.meeting.domain.entity.MeetingRoomReserve;
import com.greenstone.mes.meeting.infrastructure.persistence.MeetingRoomReserveDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MeetingRoomReserveConverter {

    //MeetingRoomReserve
    @Mapping(target = "attendeeJson", expression = "java(meetingRoomReserve.attendeeListToJson())")
    MeetingRoomReserveDO entity2Do(MeetingRoomReserve meetingRoomReserve);

    List<MeetingRoomReserveDO> entities2Dos(List<MeetingRoomReserve> meetingRooms);


    MeetingRoomReserve do2Entity(MeetingRoomReserveDO meetingRoomReserveDO);

    List<MeetingRoomReserve> dos2Entities(List<MeetingRoomReserveDO> meetingRoomReserveDOS);

}
