package com.greenstone.mes.meeting.application.dto.cqe.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingRoomReserveCheckQuery {
    
    private String id;
    private String roomId;
    private LocalDate useDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;


}
