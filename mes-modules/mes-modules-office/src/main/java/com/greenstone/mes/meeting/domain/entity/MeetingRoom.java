package com.greenstone.mes.meeting.domain.entity;

import com.greenstone.mes.form.domain.BaseFormDataEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingRoom extends BaseFormDataEntity {
    private String id;
    private String roomName;
    private Integer capacity;
    private String location;
    private String device;
}
