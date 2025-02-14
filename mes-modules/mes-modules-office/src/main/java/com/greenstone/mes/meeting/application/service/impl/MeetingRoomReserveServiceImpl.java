package com.greenstone.mes.meeting.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.common.core.enums.MeetingError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.meeting.application.assembler.MeetingRoomReserveAssembler;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomReserveDeleteCmd;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomReserveEndCmd;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomReserveInsertCmd;
import com.greenstone.mes.meeting.application.dto.cqe.cmd.MeetingRoomReserveUpdateCmd;
import com.greenstone.mes.meeting.application.dto.cqe.query.MeetingRoomReserveCheckQuery;
import com.greenstone.mes.meeting.application.dto.cqe.query.MeetingRoomReserveDayQuery;
import com.greenstone.mes.meeting.application.dto.cqe.query.MeetingRoomReserveMonthQuery;
import com.greenstone.mes.meeting.application.dto.cqe.query.MeetingRoomReserveQuery;
import com.greenstone.mes.meeting.application.dto.result.MeetingRoomReserveDayR;
import com.greenstone.mes.meeting.application.dto.result.MeetingRoomReserveMonthR;
import com.greenstone.mes.meeting.application.dto.result.MeetingRoomReserveR;
import com.greenstone.mes.meeting.application.service.MeetingRoomReserveService;
import com.greenstone.mes.meeting.domain.entity.MeetingRoomReserve;
import com.greenstone.mes.meeting.domain.repository.MeetingRoomReserveRepository;
import com.greenstone.mes.meeting.infrastructure.enums.MeetingRoomReserveStatus;
import com.greenstone.mes.oa.infrastructure.util.Periods;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2023-10-27-13:28
 */
@AllArgsConstructor
@Slf4j
@Service
public class MeetingRoomReserveServiceImpl implements MeetingRoomReserveService {

    private final MeetingRoomReserveRepository meetingRoomReserveRepository;
    private final MeetingRoomReserveAssembler meetingRoomReserveAssembler;

    @Override
    public List<MeetingRoomReserveR> list(MeetingRoomReserveQuery query) {
        return meetingRoomReserveAssembler.toMeetingRoomReserveRs(meetingRoomReserveRepository.queryMeetingRoomReserve(query));
    }

    @Override
    public MeetingRoomReserveR detail(String id) {
        return meetingRoomReserveAssembler.toMeetingRoomReserveR(meetingRoomReserveRepository.getMeetingRoomReserveById(id));
    }

    @Override
    public void insert(MeetingRoomReserveInsertCmd insertCmd) {
        MeetingRoomReserve meetingRoomReserve = meetingRoomReserveAssembler.toMeetingRoomReserve(insertCmd);
        if (!meetingRoomReserve.suitableTime()) {
            throw new ServiceException(MeetingError.E130105);
        }
        checkConflictReserve(null, insertCmd.getRoomId(), insertCmd.getUseDate(), insertCmd.getStartTime(), insertCmd.getEndTime());
        meetingRoomReserveRepository.addMeetingRoomReserve(meetingRoomReserve);
    }

    @Override
    public void update(MeetingRoomReserveUpdateCmd updateCmd) {
        MeetingRoomReserve meetingRoomReserve = meetingRoomReserveAssembler.toMeetingRoomReserve(updateCmd);
        if (!meetingRoomReserve.suitableTime()) {
            throw new ServiceException(MeetingError.E130105);
        }
        MeetingRoomReserve find = meetingRoomReserveRepository.getMeetingRoomReserveById(updateCmd.getId());
        if (Objects.isNull(find)) {
            throw new ServiceException(MeetingError.E130102);
        }
        if (MeetingRoomReserveStatus.ENDED.getState() == find.getStatus()) {
            throw new ServiceException(MeetingError.E130103);
        }
        checkConflictReserve(updateCmd.getId(), updateCmd.getRoomId(), updateCmd.getUseDate(), updateCmd.getStartTime(), updateCmd.getEndTime());
        meetingRoomReserveRepository.updateMeetingRoomReserve(meetingRoomReserve);
    }

    public void checkConflictReserve(String id, String roomId, LocalDate useDate, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("检查会议预约时间冲突,更新id:{},会议室id:{},使用日期:{},使用开始时间:{},使用结束时间:{}", id, roomId, useDate, startTime, endTime);
        List<MeetingRoomReserve> meetingRoomReserveList = meetingRoomReserveRepository.querySameDayReserve(
                MeetingRoomReserveCheckQuery.builder().id(id).roomId(roomId).useDate(useDate).build());
        if (CollUtil.isNotEmpty(meetingRoomReserveList)) {
            log.info("同一天预约:{}", meetingRoomReserveList);
            Periods reservePeriods = new Periods(startTime.toEpochSecond(ZoneOffset.ofHours(8)), endTime.toEpochSecond(ZoneOffset.ofHours(8)));
            Periods reservedPeriods = new Periods();
            for (MeetingRoomReserve roomReserve : meetingRoomReserveList) {
                reservedPeriods.addPeriod(roomReserve.getStartTime().toEpochSecond(ZoneOffset.ofHours(8)), roomReserve.getEndTime().toEpochSecond(ZoneOffset.ofHours(8)));
            }
            log.info("预约区间:{}", reservePeriods);
            log.info("已预约区间:{}", reservedPeriods);
            if (reservePeriods.intersect(reservedPeriods).sum() > 0) {
                log.info("有冲突区间:{}", reservePeriods.intersect(reservedPeriods));
                throw new ServiceException(MeetingError.E130104);
            }
        }
    }

    @Override
    public void remove(MeetingRoomReserveDeleteCmd deleteCmd) {
        for (String id : deleteCmd.getIds()) {
            MeetingRoomReserve meetingRoomReserveById = meetingRoomReserveRepository.getMeetingRoomReserveById(id);
            if (meetingRoomReserveById.getStartTime().isBefore(LocalDateTime.now())) {
                throw new ServiceException(MeetingError.E130106);
            }
        }
        meetingRoomReserveRepository.delete(deleteCmd.getIds());
    }

    @Override
    public void end(MeetingRoomReserveEndCmd endCmd) {
        meetingRoomReserveRepository.end(endCmd.getIds());
    }

    @Override
    public List<MeetingRoomReserveDayR> listDayReserve(MeetingRoomReserveDayQuery query) {
        List<MeetingRoomReserveDayR> dayRList = new ArrayList<>();
        List<MeetingRoomReserve> meetingRoomReserveList = meetingRoomReserveRepository.queryMeetingRoomReserve(MeetingRoomReserveQuery.builder().useDate(query.getDay()).build());
        if (CollUtil.isNotEmpty(meetingRoomReserveList)) {
            Map<String, List<MeetingRoomReserve>> groupByRoomId = meetingRoomReserveList.stream().collect(Collectors.groupingBy(MeetingRoomReserve::getRoomId));
            groupByRoomId.forEach((roomId, list) -> {
                String roomName = list.get(0).getRoomName();
                Integer capacity = list.get(0).getCapacity();
                MeetingRoomReserveDayR meetingRoomReserveDayR = MeetingRoomReserveDayR.builder().roomName(roomName).capacity(capacity).build();
                List<MeetingRoomReserveDayR.DayReserve> dayReserveList = new ArrayList<>();
                meetingRoomReserveDayR.setReserveList(dayReserveList);
                for (MeetingRoomReserve meetingRoomReserve : list) {
                    MeetingRoomReserveDayR.DayReserve dayReserve = MeetingRoomReserveDayR.DayReserve.builder().theme(meetingRoomReserve.getTheme())
                            .reserveBy(meetingRoomReserve.getReserveBy()).useDate(meetingRoomReserve.getUseDate())
                            .startTime(meetingRoomReserve.getStartTime()).endTime(meetingRoomReserve.getEndTime()).build();
                    dayReserveList.add(dayReserve);
                }
                dayRList.add(meetingRoomReserveDayR);
            });
        }
        return dayRList;
    }

    @Override
    public List<MeetingRoomReserveMonthR> listMonthReserve(MeetingRoomReserveMonthQuery query) {
        List<MeetingRoomReserveMonthR> monthRList = new ArrayList<>();
        List<MeetingRoomReserve> meetingRoomReserveList = meetingRoomReserveRepository.queryMeetingRoomReserve(MeetingRoomReserveQuery.builder().month(query.getMonth()).build());
        if (CollUtil.isNotEmpty(meetingRoomReserveList)) {
            Map<LocalDate, List<MeetingRoomReserve>> groupByUseDate = meetingRoomReserveList.stream().collect(Collectors.groupingBy(MeetingRoomReserve::getUseDate));
            groupByUseDate.forEach((useDate, list) -> {
                MeetingRoomReserveMonthR meetingRoomReserveMonthR = MeetingRoomReserveMonthR.builder().useDate(useDate).build();
                List<MeetingRoomReserveMonthR.MonthReserve> monthReserveList = new ArrayList<>();
                meetingRoomReserveMonthR.setReserveList(monthReserveList);
                for (MeetingRoomReserve meetingRoomReserve : list) {
                    MeetingRoomReserveMonthR.MonthReserve monthReserve = MeetingRoomReserveMonthR.MonthReserve.builder().roomName(meetingRoomReserve.getRoomName())
                            .reserveBy(meetingRoomReserve.getReserveBy())
                            .startTime(meetingRoomReserve.getStartTime())
                            .endTime(meetingRoomReserve.getEndTime()).build();
                    monthReserveList.add(monthReserve);
                }
                monthRList.add(meetingRoomReserveMonthR);
            });
        }
        return monthRList;
    }

    @Override
    public void changeStatus() {
        List<MeetingRoomReserve> notEndedList = meetingRoomReserveRepository.queryNotEndedReserve();
        log.info("有{}个未结束的会议：{}", notEndedList.size(), notEndedList);
        for (MeetingRoomReserve meetingRoomReserve : notEndedList) {
            if (meetingRoomReserve.getEndTime().isBefore(LocalDateTime.now())) {
                meetingRoomReserve.setStatus(MeetingRoomReserveStatus.ENDED.getState());
                meetingRoomReserveRepository.updateMeetingRoomReserve(meetingRoomReserve);
                log.info("会议已结束。会议详情：{}", meetingRoomReserve);
                continue;
            }
            if (meetingRoomReserve.getStartTime().isBefore(LocalDateTime.now())) {
                meetingRoomReserve.setStatus(MeetingRoomReserveStatus.ONGOING.getState());
                meetingRoomReserveRepository.updateMeetingRoomReserve(meetingRoomReserve);
                log.info("会议开始。会议详情：{}", meetingRoomReserve);
            }
        }

    }
}
