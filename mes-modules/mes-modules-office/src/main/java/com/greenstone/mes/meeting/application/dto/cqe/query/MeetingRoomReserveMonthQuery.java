package com.greenstone.mes.meeting.application.dto.cqe.query;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MeetingRoomReserveMonthQuery {

    @NotEmpty(message = "请选择查询月份")
    private String month;

}
