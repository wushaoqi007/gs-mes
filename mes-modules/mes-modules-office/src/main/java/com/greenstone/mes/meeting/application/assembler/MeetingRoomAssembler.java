package com.greenstone.mes.meeting.application.assembler;

import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomInsertCmd;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomUpdateCmd;
import com.greenstone.mes.meeting.application.dto.result.MeetingRoomR;
import com.greenstone.mes.meeting.domain.entity.MeetingRoom;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MeetingRoomAssembler {

    MeetingRoom toMeetingRoom(MeetingRoomInsertCmd insertCmd);

    MeetingRoom toMeetingRoom(MeetingRoomUpdateCmd updateCmd);

    MeetingRoomR toMeetingRoomR(MeetingRoom meetingRoom);

    List<MeetingRoomR> toMeetingRoomRs(List<MeetingRoom> meetingRooms);

}
