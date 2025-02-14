package com.greenstone.mes.meeting.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
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
import com.greenstone.mes.meeting.application.service.MeetingRoomReserveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-10-27-13:10
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/meeting/room/reserve")
public class MeetingRoomReserveApi extends BaseController {

    private final MeetingRoomReserveService meetingRoomReserveService;

    @GetMapping("/list")
    public TableDataInfo list(MeetingRoomReserveQuery query) {
        log.info("meeting reserve query params:{}", query);
        startPage();
        return getDataTable(meetingRoomReserveService.list(query));
    }

    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable("id") @NotEmpty(message = "请选择预约记录") String id) {
        MeetingRoomReserveR meetingRoomReserveR = meetingRoomReserveService.detail(id);
        return AjaxResult.success(meetingRoomReserveR);
    }

    @PostMapping
    public AjaxResult insert(@Validated @RequestBody MeetingRoomReserveInsertCmd saveCmd) {
        log.info("meeting reserve add params:{}", saveCmd);
        meetingRoomReserveService.insert(saveCmd);
        return AjaxResult.success();
    }

    @PutMapping
    public AjaxResult update(@Validated @RequestBody MeetingRoomReserveUpdateCmd updateCmd) {
        log.info("meeting reserve update params:{}", updateCmd);
        meetingRoomReserveService.update(updateCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult delete(@Validated @RequestBody MeetingRoomReserveDeleteCmd deleteCmd) {
        log.info("meeting reserve delete params:{}", deleteCmd);
        meetingRoomReserveService.remove(deleteCmd);
        return AjaxResult.success();
    }

    @PostMapping("/end")
    public AjaxResult end(@Validated @RequestBody MeetingRoomReserveEndCmd endCmd) {
        log.info("meeting reserve end params:{}", endCmd);
        meetingRoomReserveService.end(endCmd);
        return AjaxResult.success();
    }

    @GetMapping("/day")
    public AjaxResult day(MeetingRoomReserveDayQuery query) {
        log.info("meeting reserve query by day params:{}", query);
        List<MeetingRoomReserveDayR> dayRList =meetingRoomReserveService.listDayReserve(query);
        return AjaxResult.success(dayRList);
    }
    @GetMapping("/month")
    public AjaxResult month(MeetingRoomReserveMonthQuery query) {
        log.info("meeting reserve query by month params:{}", query);
        List<MeetingRoomReserveMonthR> dayRList =meetingRoomReserveService.listMonthReserve(query);
        return AjaxResult.success(dayRList);
    }

    @PostMapping("/changeStatus")
    public AjaxResult changeStatus() {
        log.info("meeting reserve change status start");
        meetingRoomReserveService.changeStatus();
        return AjaxResult.success();
    }
}
