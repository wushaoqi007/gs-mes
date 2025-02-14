package com.greenstone.mes.meeting.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.meeting.domain.entity.MeetingRoomAttendee;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-10-27-13:33
 */
@Data
public class MeetingRoomReserveR {
    private String id;
    private String roomId;
    private String roomName;
    private String theme;
    private LocalDate useDate;
    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime endTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reserveTime;
    private String reserveById;
    private String reserveByNo;
    private String reserveBy;
    private Integer attendeeCount;
    private Integer status;
    private String description;

    private List<MeetingRoomAttendee> attendeeList;
}
