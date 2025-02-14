package com.greenstone.mes.meeting.application.service;

import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomReserveDeleteCmd;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomReserveEndCmd;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomReserveInsertCmd;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomReserveUpdateCmd;
import com.greenstone.mes.meeting.application.dto.cqe.query.MeetingRoomReserveDayQuery;
import com.greenstone.mes.meeting.application.dto.cqe.query.MeetingRoomReserveMonthQuery;
import com.greenstone.mes.meeting.application.dto.cqe.query.MeetingRoomReserveQuery;
import com.greenstone.mes.meeting.application.dto.result.MeetingRoomReserveDayR;
import com.greenstone.mes.meeting.application.dto.result.MeetingRoomReserveMonthR;
import com.greenstone.mes.meeting.application.dto.result.MeetingRoomReserveR;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-10-27-14:56
 */
public interface MeetingRoomReserveService {

    List<MeetingRoomReserveR> list(MeetingRoomReserveQuery query);

    MeetingRoomReserveR detail(String id);

    void insert(MeetingRoomReserveInsertCmd insertCmd);

    void update(MeetingRoomReserveUpdateCmd updateCmd);

    void remove(MeetingRoomReserveDeleteCmd deleteCmd);

    void end(MeetingRoomReserveEndCmd endCmd);

    List<MeetingRoomReserveDayR> listDayReserve(MeetingRoomReserveDayQuery query);

    List<MeetingRoomReserveMonthR> listMonthReserve(MeetingRoomReserveMonthQuery query);

    void changeStatus();
}
