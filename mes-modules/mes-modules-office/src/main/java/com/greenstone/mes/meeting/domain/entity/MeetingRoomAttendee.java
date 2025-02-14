package com.greenstone.mes.meeting.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingRoomAttendee {
    private String attendeeName;
    private String attendeeId;
    private String attendeeNo;
}
