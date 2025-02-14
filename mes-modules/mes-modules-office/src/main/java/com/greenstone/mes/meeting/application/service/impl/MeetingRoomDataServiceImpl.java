package com.greenstone.mes.meeting.application.service.impl;

import com.greenstone.mes.external.application.service.ProcessInstanceService;
import com.greenstone.mes.form.domain.helper.FormHelper;
import com.greenstone.mes.form.domain.service.AbstractFormDataService;
import com.greenstone.mes.form.dto.cmd.FormDataRemoveCmd;
import com.greenstone.mes.form.dto.cmd.FormDataRevokeCmd;
import com.greenstone.mes.form.dto.query.FormDataQuery;
import com.greenstone.mes.form.infrastructure.annotation.FormService;
import com.greenstone.mes.meeting.application.assembler.MeetingRoomAssembler;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomDeleteCmd;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomInsertCmd;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomUpdateCmd;
import com.greenstone.mes.meeting.application.dto.result.MeetingRoomR;
import com.greenstone.mes.meeting.application.service.MeetingRoomDataService;
import com.greenstone.mes.meeting.domain.entity.MeetingRoom;
import com.greenstone.mes.meeting.domain.repository.MeetingRoomRepository;
import com.greenstone.mes.meeting.infrastructure.mapper.MeetingRoomMapper;
import com.greenstone.mes.meeting.infrastructure.persistence.MeetingRoomDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-10-27-13:28
 */
@FormService("meetingRoom")
@Slf4j
@Service
public class MeetingRoomDataServiceImpl extends AbstractFormDataService<MeetingRoom, MeetingRoomDO, MeetingRoomMapper> implements MeetingRoomDataService {

    private final MeetingRoomRepository meetingRoomRepository;
    private final MeetingRoomAssembler meetingRoomAssembler;

    public MeetingRoomDataServiceImpl(MeetingRoomRepository meetingRoomRepository, MeetingRoomAssembler meetingRoomAssembler,
                                      ProcessInstanceService processInstanceService, FormHelper formHelper) {
        super(meetingRoomRepository, processInstanceService, formHelper);
        this.meetingRoomRepository = meetingRoomRepository;
        this.meetingRoomAssembler = meetingRoomAssembler;
    }

    @Override
    public List<MeetingRoomR> list() {
        return meetingRoomAssembler.toMeetingRoomRs(meetingRoomRepository.queryAllMeetingRoom());
    }

    @Override
    public MeetingRoomR detail(String id) {
        return meetingRoomAssembler.toMeetingRoomR(meetingRoomRepository.getMeetingRoomById(id));
    }

    @Override
    public void save(MeetingRoom entity) {
        meetingRoomRepository.addMeetingRoom(entity);
    }

    @Override
    public void update(MeetingRoomUpdateCmd updateCmd) {
        MeetingRoom meetingRoom = meetingRoomAssembler.toMeetingRoom(updateCmd);
        meetingRoomRepository.updateMeetingRoom(meetingRoom);
    }

    @Override
    public void remove(MeetingRoomDeleteCmd deleteCmd) {
        meetingRoomRepository.delete(deleteCmd.getIds());
    }

    @Override
    public List<MeetingRoom> query(FormDataQuery query) {
        return meetingRoomRepository.queryAllMeetingRoom();
    }

    @Override
    public MeetingRoom insertOrUpdateDraft(MeetingRoom entity) {
        throw new RuntimeException("Unsupported draft save.");
    }

    @Override
    public MeetingRoom insertOrUpdateCommit(MeetingRoom entity) {
        throw new RuntimeException("Unsupported commit save.");
    }

    @Override
    public void delete(FormDataRemoveCmd removeCmd) {
        meetingRoomRepository.delete(removeCmd.getIds());
    }

    @Override
    public void revoke(FormDataRevokeCmd revokeCmd) {
        throw new RuntimeException("Unsupported workflow form.");
    }

}