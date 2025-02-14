package com.greenstone.mes.meeting.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.meeting.application.assembler.MeetingRoomAssembler;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomDeleteCmd;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomInsertCmd;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomUpdateCmd;
import com.greenstone.mes.meeting.application.dto.result.MeetingRoomR;
import com.greenstone.mes.meeting.application.service.MeetingRoomDataService;
import com.greenstone.mes.meeting.domain.entity.MeetingRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;

/**
 * @author wushaoqi
 * @date 2023-10-27-13:10
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/meeting/room")
public class MeetingRoomApi extends BaseController {

    private final MeetingRoomDataService meetingRoomService;
    private final MeetingRoomAssembler meetingRoomAssembler;

    @GetMapping("/list")
    public TableDataInfo list() {
        startPage();
        return getDataTable(meetingRoomService.list());
    }

    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable("id") @NotEmpty(message = "请选择会议室") String id) {
        MeetingRoomR meetingRoomR = meetingRoomService.detail(id);
        return AjaxResult.success(meetingRoomR);
    }

    @ApiLog
    @PostMapping
    public AjaxResult insert(@Validated @RequestBody MeetingRoomInsertCmd saveCmd) {
        MeetingRoom meetingRoom = meetingRoomAssembler.toMeetingRoom(saveCmd);
        meetingRoomService.save(meetingRoom);
        return AjaxResult.success();
    }

    @ApiLog
    @PutMapping
    public AjaxResult update(@Validated @RequestBody MeetingRoomUpdateCmd updateCmd) {
        meetingRoomService.update(updateCmd);
        return AjaxResult.success();
    }

    @ApiLog
    @DeleteMapping
    public AjaxResult delete(@Validated @RequestBody MeetingRoomDeleteCmd deleteCmd) {
        meetingRoomService.remove(deleteCmd);
        return AjaxResult.success();
    }
}
