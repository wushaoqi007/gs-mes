package com.greenstone.mes.meeting.application.dto.cqe.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author wushaoqi
 * @date 2023-10-27-13:16
 */
@Data
public class MeetingRoomUpdateCmd {

    @NotEmpty(message = "会议室id不为空")
    private String id;

    @NotEmpty(message = "请填写会议室名称")
    private String roomName;

    private Integer capacity;

    private String location;

    private String device;
}
