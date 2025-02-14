package com.greenstone.mes.meeting.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.form.infrastructure.persistence.BaseFormPo;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

/**
 * @author wushaoqi
 * @date 2023-10-27-9:57
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@TableName(value = "meeting_room")
public class MeetingRoomDO extends BaseFormPo {

    @Serial
    private static final long serialVersionUID = 1692963683902997724L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String roomName;
    private Integer capacity;
    private String location;
    private String device;
}
