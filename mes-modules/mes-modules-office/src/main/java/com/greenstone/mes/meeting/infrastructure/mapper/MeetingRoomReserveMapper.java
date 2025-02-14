package com.greenstone.mes.meeting.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.meeting.application.dto.cqe.query.MeetingRoomReserveQuery;
import com.greenstone.mes.meeting.domain.entity.MeetingRoomReserve;
import com.greenstone.mes.meeting.infrastructure.persistence.MeetingRoomReserveDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-10-27-10:17
 */
@Repository
public interface MeetingRoomReserveMapper extends EasyBaseMapper<MeetingRoomReserveDO> {

    List<MeetingRoomReserve> queryMeetingRoomReserve(MeetingRoomReserveQuery query);

    MeetingRoomReserve getMeetingRoomReserveById(String id);
}
