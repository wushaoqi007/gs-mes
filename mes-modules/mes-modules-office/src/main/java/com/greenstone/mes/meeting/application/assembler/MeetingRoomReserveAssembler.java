package com.greenstone.mes.meeting.application.assembler;

import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomReserveInsertCmd;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomReserveUpdateCmd;
import com.greenstone.mes.meeting.application.dto.result.MeetingRoomReserveR;
import com.greenstone.mes.meeting.domain.entity.MeetingRoomReserve;
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
public interface MeetingRoomReserveAssembler {

    MeetingRoomReserve toMeetingRoomReserve(MeetingRoomReserveInsertCmd insertCmd);

    MeetingRoomReserve toMeetingRoomReserve(MeetingRoomReserveUpdateCmd updateCmd);

    @Mapping(target = "attendeeList", expression = "java(meetingRoomReserve.attendeeJsonToList())")
    MeetingRoomReserveR toMeetingRoomReserveR(MeetingRoomReserve meetingRoomReserve);

    List<MeetingRoomReserveR> toMeetingRoomReserveRs(List<MeetingRoomReserve> meetingRoomReserves);

}
