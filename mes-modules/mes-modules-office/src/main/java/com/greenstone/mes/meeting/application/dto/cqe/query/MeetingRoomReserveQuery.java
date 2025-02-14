package com.greenstone.mes.meeting.application.dto.cqe.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MeetingRoomReserveQuery {
    
    private String reserveById;
    private LocalDate useDate;
    private String roomId;
    private String month;

}
