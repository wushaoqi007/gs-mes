package com.greenstone.mes.meeting.application.dto.cqe.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MeetingRoomInsertCmd {

    @NotEmpty(message = "请填写会议室名称")
    private String roomName;

    private Integer capacity;

    private String location;

    private String device;

}
