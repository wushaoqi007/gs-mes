package com.greenstone.mes.meeting.application.dto.cqe.query;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class MeetingRoomReserveDayQuery {

    @NotNull(message = "请选择查询日期")
    private LocalDate day;

}
