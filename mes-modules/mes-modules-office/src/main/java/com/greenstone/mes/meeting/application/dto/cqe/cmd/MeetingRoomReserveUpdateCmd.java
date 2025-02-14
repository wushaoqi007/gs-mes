package com.greenstone.mes.meeting.application.dto.cqe.cmd;

import com.greenstone.mes.meeting.domain.entity.MeetingRoomAttendee;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-10-27-13:16
 */
@Data
public class MeetingRoomReserveUpdateCmd {

    @NotEmpty(message = "请选择预约记录")
    private String id;
    @NotEmpty(message = "请选择会议室")
    private String roomName;
    @NotEmpty(message = "请选择会议室")
    private String roomId;
    @NotEmpty(message = "请填写主题")
    private String theme;
    @NotNull(message = "请选择使用日期")
    private LocalDate useDate;
    @NotNull(message = "请选择会议开始时间")
    private LocalDateTime startTime;
    @NotNull(message = "请选择会议结束时间")
    private LocalDateTime endTime;
    private Long reserveById;
    private String reserveByNo;
    private String reserveBy;
    private Integer attendeeCount;
    private String description;

    private String attendeeJson;
    private List<MeetingRoomAttendee> attendeeList;
}
