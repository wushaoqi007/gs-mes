package com.greenstone.mes.oa.application.wrapper;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.greenstone.mes.oa.application.dto.attendance.*;
import com.greenstone.mes.oa.application.service.OaWxBusinessTripService;
import com.greenstone.mes.oa.domain.*;
import com.greenstone.mes.oa.infrastructure.mapper.ApprovalExtraWorkMapper;
import com.greenstone.mes.oa.infrastructure.mapper.ApprovalNightMapper;
import com.greenstone.mes.oa.infrastructure.mapper.ApprovalPunchCorrectionMapper;
import com.greenstone.mes.oa.infrastructure.mapper.ApprovalVacationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
public class ApprovalWrapper {

    @Autowired
    private OaWxBusinessTripService businessTripService;

    @Autowired
    private ApprovalNightMapper approvalNightMapper;

    @Autowired
    private ApprovalVacationMapper approvalVacationMapper;

    @Autowired
    private ApprovalExtraWorkMapper approvalExtraWorkMapper;

    @Autowired
    private ApprovalPunchCorrectionMapper punchCorrectionMapper;

    public List<ApprovalNightShift> getApprovalNightShiftList(Long start, Long end, String userId, String cpId) {
        QueryWrapper<ApprovalNightDO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StrUtil.isNotEmpty(userId), ApprovalNightDO::getUserId, userId).eq(ApprovalNightDO::getSpStatus, 2).eq(ApprovalNightDO::getCpId, cpId).
                and(w -> w.or(w1 -> w1.ge(ApprovalNightDO::getStartTime, start).le(ApprovalNightDO::getStartTime, end)).
                        or(w1 -> w1.ge(ApprovalNightDO::getEndTime, start).le(ApprovalNightDO::getEndTime, end)).
                        or(w1 -> w1.le(ApprovalNightDO::getStartTime, start).ge(ApprovalNightDO::getEndTime, end)));
        List<ApprovalNightDO> list = approvalNightMapper.selectList(wrapper);
        List<ApprovalNightShift> resultList = new ArrayList<>();
        for (ApprovalNightDO wxNight : list) {
            ApprovalNightShift approvalNightShift = new ApprovalNightShift();
            approvalNightShift.setStart(wxNight.getStartTime());
            approvalNightShift.setEnd(wxNight.getEndTime());
            approvalNightShift.setUserId(wxNight.getUserId());
            resultList.add(approvalNightShift);
        }
        return resultList;
    }

    public List<ApprovalBusinessTrip> getApprovalBusinessTripList(Long start, Long end, String userId) {
        QueryWrapper<ApprovalTripDO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StrUtil.isNotEmpty(userId), ApprovalTripDO::getUserId, userId).eq(ApprovalTripDO::getSpStatus, 2).
                and(w -> w.or(w1 -> w1.ge(ApprovalTripDO::getStartTime, start).le(ApprovalTripDO::getStartTime, end)).
                        or(w1 -> w1.ge(ApprovalTripDO::getEndTime, start).le(ApprovalTripDO::getEndTime, end)).
                        or(w1 -> w1.le(ApprovalTripDO::getStartTime, start).ge(ApprovalTripDO::getEndTime, end)));
        List<ApprovalTripDO> list = businessTripService.list(wrapper);
        List<ApprovalBusinessTrip> resultList = new ArrayList<>();
        for (ApprovalTripDO approvalTripDO : list) {
            ApprovalBusinessTrip trip = new ApprovalBusinessTrip();
            trip.setStart(approvalTripDO.getStartTime());
            trip.setEnd(approvalTripDO.getEndTime());
            trip.setUserId(approvalTripDO.getUserId());
            resultList.add(trip);
        }
        return resultList;
    }

    public List<ApprovalLeave> getApprovalLeaveList(Long start, Long end, String userId, String cpId) {
        QueryWrapper<ApprovalVacationDO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StrUtil.isNotEmpty(userId), ApprovalVacationDO::getUserId, userId).eq(ApprovalVacationDO::getSpStatus, 2).eq(ApprovalVacationDO::getCpId, cpId).
                and(w -> w.or(w1 -> w1.ge(ApprovalVacationDO::getStartTime, start).le(ApprovalVacationDO::getStartTime, end)).
                        or(w1 -> w1.ge(ApprovalVacationDO::getEndTime, start).le(ApprovalVacationDO::getEndTime, end)).
                        or(w1 -> w1.le(ApprovalVacationDO::getStartTime, start).ge(ApprovalVacationDO::getEndTime, end)));
        List<ApprovalVacationDO> list = approvalVacationMapper.selectList(wrapper);
        List<ApprovalLeave> resultList = new ArrayList<>();
        for (ApprovalVacationDO leave : list) {
            ApprovalLeave approvalLeave = new ApprovalLeave();
            approvalLeave.setSpNo(leave.getSpNo());
            approvalLeave.setStart(leave.getStartTime());
            approvalLeave.setEnd(leave.getEndTime());
            approvalLeave.setReason(leave.getReason());
            approvalLeave.setUserId(leave.getUserId());
            approvalLeave.setType(leave.getType());
            resultList.add(approvalLeave);
        }
        return resultList;
    }

    public List<ApprovalOverTime> getApprovalOverWorkList(Long start, Long end, String userId, String cpId) {
        QueryWrapper<ApprovalAttendanceDO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StrUtil.isNotEmpty(userId), ApprovalAttendanceDO::getUserId, userId).eq(ApprovalAttendanceDO::getSpStatus, 2).eq(ApprovalAttendanceDO::getCpId, cpId).
                and(w -> w.or(w1 -> w1.ge(ApprovalAttendanceDO::getStartTime, start).le(ApprovalAttendanceDO::getStartTime, end)).
                        or(w1 -> w1.ge(ApprovalAttendanceDO::getEndTime, start).le(ApprovalAttendanceDO::getEndTime, end)).
                        or(w1 -> w1.le(ApprovalAttendanceDO::getStartTime, start).ge(ApprovalAttendanceDO::getEndTime, end)));
        List<ApprovalAttendanceDO> list = approvalExtraWorkMapper.selectList(wrapper);
        List<ApprovalOverTime> resultList = new ArrayList<>();
        for (ApprovalAttendanceDO overTime : list) {
            ApprovalOverTime approvalOverTime = new ApprovalOverTime();
            approvalOverTime.setStart(overTime.getStartTime());
            approvalOverTime.setEnd(overTime.getEndTime());
            approvalOverTime.setUserId(overTime.getUserId());
            resultList.add(approvalOverTime);
        }
        return resultList;
    }

    /**
     * 获取加班审批，包含未审批通过的
     */
    public List<ApprovalOverTime> getOverWorkListNoNeedApplied(Long start, Long end, String userId, String cpId) {
        QueryWrapper<ApprovalAttendanceDO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StrUtil.isNotEmpty(userId), ApprovalAttendanceDO::getUserId, userId)
                .and(w -> w.eq(ApprovalAttendanceDO::getSpStatus, 2).or().eq(ApprovalAttendanceDO::getSpStatus, 1))
                .eq(ApprovalAttendanceDO::getCpId, cpId)
                .and(w -> w.or(w1 -> w1.ge(ApprovalAttendanceDO::getStartTime, start).le(ApprovalAttendanceDO::getStartTime, end)).
                        or(w1 -> w1.ge(ApprovalAttendanceDO::getEndTime, start).le(ApprovalAttendanceDO::getEndTime, end)).
                        or(w1 -> w1.le(ApprovalAttendanceDO::getStartTime, start).ge(ApprovalAttendanceDO::getEndTime, end)));
        List<ApprovalAttendanceDO> list = approvalExtraWorkMapper.selectList(wrapper);
        List<ApprovalOverTime> resultList = new ArrayList<>();
        for (ApprovalAttendanceDO overTime : list) {
            ApprovalOverTime approvalOverTime = new ApprovalOverTime();
            approvalOverTime.setStart(overTime.getStartTime());
            approvalOverTime.setEnd(overTime.getEndTime());
            approvalOverTime.setUserId(overTime.getUserId());
            resultList.add(approvalOverTime);
        }
        return resultList;
    }

    public List<ApprovalCalcPunchCorrection> getApprovalPunchCorrectionList(Long start, Long end, String userId, String cpId) {
        QueryWrapper<ApprovalPunchCorrectionDO> wrapper = new QueryWrapper<>();
        wrapper.lambda().ge(ApprovalPunchCorrectionDO::getCorrectionTime, start).le(ApprovalPunchCorrectionDO::getCorrectionTime, end).
                eq(StrUtil.isNotEmpty(userId), ApprovalPunchCorrectionDO::getUserId, userId).eq(ApprovalPunchCorrectionDO::getSpStatus, 2).eq(ApprovalPunchCorrectionDO::getCpId, cpId);
        List<ApprovalPunchCorrectionDO> list = punchCorrectionMapper.selectList(wrapper);
        List<ApprovalCalcPunchCorrection> resultList = new ArrayList<>();
        for (ApprovalPunchCorrectionDO punchCorrection : list) {
            ApprovalCalcPunchCorrection correction = new ApprovalCalcPunchCorrection();
            correction.setCheckInTime(punchCorrection.getCorrectionTime());
            correction.setUserId(punchCorrection.getUserId());
            resultList.add(correction);
        }
        return resultList;
    }

}
