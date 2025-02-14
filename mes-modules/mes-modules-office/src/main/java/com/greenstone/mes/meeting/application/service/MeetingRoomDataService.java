package com.greenstone.mes.meeting.application.service;

import com.greenstone.mes.form.domain.service.BaseFormDataService;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomDeleteCmd;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomInsertCmd;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomUpdateCmd;
import com.greenstone.mes.meeting.application.dto.result.MeetingRoomR;
import com.greenstone.mes.meeting.domain.entity.MeetingRoom;
import com.greenstone.mes.meeting.infrastructure.mapper.MeetingRoomMapper;
import com.greenstone.mes.meeting.infrastructure.persistence.MeetingRoomDO;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-10-27-13:12
 */
public interface MeetingRoomDataService extends BaseFormDataService<MeetingRoom, MeetingRoomDO, MeetingRoomMapper> {

    List<MeetingRoomR> list();

    MeetingRoomR detail(String id);

    void save(MeetingRoom entity);

    void update(MeetingRoomUpdateCmd updateCmd);

    void remove(MeetingRoomDeleteCmd deleteCmd);
}
