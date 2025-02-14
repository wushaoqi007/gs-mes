package com.greenstone.mes.meeting.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2023-10-27-9:57
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "meeting_room_reserve")
public class MeetingRoomReserveDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -6777228057280425854L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String roomId;
    private String roomName;
    private String theme;
    private LocalDate useDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime reserveTime;
    private String reserveBy;
    private Long reserveById;
    private String reserveByNo;
    private Integer attendeeCount;
    private Integer status;
    private String description;
    private String attendeeJson;
}
