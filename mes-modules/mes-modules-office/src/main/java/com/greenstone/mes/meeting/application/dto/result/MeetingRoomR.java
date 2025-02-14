package com.greenstone.mes.meeting.application.dto.result;

import lombok.Data;

/**
 * @author wushaoqi
 * @date 2023-10-27-13:33
 */
@Data
public class MeetingRoomR {
    private String id;
    private String roomName;
    private Integer capacity;
    private String location;
    private String device;
}
