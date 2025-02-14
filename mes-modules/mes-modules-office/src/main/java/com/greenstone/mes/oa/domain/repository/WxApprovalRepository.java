package com.greenstone.mes.oa.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.oa.application.dto.ApprovalQuery;
import com.greenstone.mes.oa.application.dto.PassedApprovalQuery;
import com.greenstone.mes.oa.domain.*;
import com.greenstone.mes.oa.domain.converter.ApprovalConverter;
import com.greenstone.mes.oa.domain.entity.*;
import com.greenstone.mes.oa.infrastructure.enums.ApprovalStatus;
import com.greenstone.mes.oa.infrastructure.mapper.*;
import com.greenstone.mes.oa.infrastructure.persistence.WxApprovalSyncDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author gu_renkai
 * @date 2022/11/17 9:05
 */
@Slf4j
@Service
public class WxApprovalRepository {

    private final ApprovalExtraWorkMapper approvalExtraWorkMapper;
    private final ApprovalVacationMapper approvalVacationMapper;
    private final ApprovalNightMapper approvalNightMapper;
    private final ApprovalTemporaryChangeMapper approvalTemporaryChangeMapper;
    private final ApprovalPunchCorrectionMapper approvalPunchCorrectionMapper;
    private final ApprovalConverter approvalConverter;
    private final WxApprovalSyncMapper approvalSyncMapper;

    @Autowired
    public WxApprovalRepository(ApprovalExtraWorkMapper approvalExtraWorkMapper, ApprovalVacationMapper approvalVacationMapper,
                                ApprovalNightMapper approvalNightMapper, ApprovalPunchCorrectionMapper approvalPunchCorrectionMapper,
                                ApprovalConverter approvalConverter, ApprovalTemporaryChangeMapper approvalTemporaryChangeMapper,
                                WxApprovalSyncMapper approvalSyncMapper) {
        this.approvalExtraWorkMapper = approvalExtraWorkMapper;
        this.approvalVacationMapper = approvalVacationMapper;
        this.approvalNightMapper = approvalNightMapper;
        this.approvalPunchCorrectionMapper = approvalPunchCorrectionMapper;
        this.approvalConverter = approvalConverter;
        this.approvalTemporaryChangeMapper = approvalTemporaryChangeMapper;
        this.approvalSyncMapper = approvalSyncMapper;
    }

    public Long lastSync(String cpId) {
        LambdaQueryWrapper<WxApprovalSyncDO> wrapper = Wrappers.lambdaQuery(WxApprovalSyncDO.class).eq(WxApprovalSyncDO::getWxCpId, cpId)
                .orderByDesc(WxApprovalSyncDO::getEndSec).last("limit 1");
        WxApprovalSyncDO wxApprovalSyncDO = approvalSyncMapper.selectOne(wrapper);
        if (wxApprovalSyncDO != null && wxApprovalSyncDO.getEndSec() != null) {
            return wxApprovalSyncDO.getEndSec();
        }
        return DateUtil.beginOfMonth(new Date()).getTime() / 1000;
    }

    public void insertApprovalSyncRecord(String wxCpId, long beginSec, long endSec) {
        // 记录同步时间
        WxApprovalSyncDO wxApprovalSyncDO = WxApprovalSyncDO.builder().beginSec(beginSec).endSec(endSec).wxCpId(wxCpId).build();
        approvalSyncMapper.insert(wxApprovalSyncDO);
    }

    public boolean saveExtraWork(ApprovalExtraWork approvalExtraWork) {
        boolean isUpdateOrSave = true;
        ApprovalAttendanceDO approvalEntity = approvalConverter.toDO(approvalExtraWork);

        ApprovalAttendanceDO existApproval = approvalExtraWorkMapper.getOneOnly(
                ApprovalAttendanceDO.builder().spNo(approvalEntity.getSpNo()).cpId(approvalEntity.getCpId()).build()
        );
        // 根据审批号，判断更新or新增
        if (Objects.nonNull(existApproval)) {
            if (approvalEntity.getSpStatus() > existApproval.getSpStatus()) {
                approvalEntity.setId(existApproval.getId());
                approvalExtraWorkMapper.updateById(approvalEntity);
                log.info("Update approval: {}", approvalEntity);
            } else {
                isUpdateOrSave = false;
                log.info("Ignore approval: {}", approvalEntity);
            }
        } else {
            approvalExtraWorkMapper.insert(approvalEntity);
            log.info("Save approval: {}", approvalEntity);
        }
        return isUpdateOrSave;
    }

    public List<ApprovalExtraWork> listPassedApprovalAttendance(PassedApprovalQuery query) {
        long start = query.getStart().getTime() / 1000;
        long end = query.getEnd().getTime() / 1000;
        LambdaQueryWrapper<ApprovalAttendanceDO> wrapper = Wrappers.lambdaQuery(ApprovalAttendanceDO.class)
                .eq(ApprovalAttendanceDO::getCpId, query.getCpId().id())
                .eq(ApprovalAttendanceDO::getSpStatus, ApprovalStatus.PASSED.getStatus())
                .and(w -> w.or(w1 -> w1.ge(ApprovalAttendanceDO::getStartTime, start).le(ApprovalAttendanceDO::getStartTime, end)).
                        or(w1 -> w1.ge(ApprovalAttendanceDO::getEndTime, start).le(ApprovalAttendanceDO::getEndTime, end)).
                        or(w1 -> w1.le(ApprovalAttendanceDO::getStartTime, start).ge(ApprovalAttendanceDO::getEndTime, end)));
        if (query.getUserId() != null) {
            wrapper.eq(ApprovalAttendanceDO::getUserId, query.getUserId().id());
        }
        if (CollUtil.isNotEmpty(query.getUserIds())) {
            wrapper.in(ApprovalAttendanceDO::getUserId, query.getUserIds());
        }
        List<ApprovalAttendanceDO> list = approvalExtraWorkMapper.selectList(wrapper);
        return approvalConverter.toApprovalAttendances(list);
    }

    public List<ApprovalExtraWork> listApprovalAttendance(ApprovalQuery query) {
        long start = query.getStart().getTime() / 1000;
        long end = query.getEnd().getTime() / 1000;
        LambdaQueryWrapper<ApprovalAttendanceDO> wrapper = Wrappers.lambdaQuery(ApprovalAttendanceDO.class)
                .eq(ApprovalAttendanceDO::getUserId, query.getUserId().id())
                .eq(ApprovalAttendanceDO::getCpId, query.getCpId().id())
                .and(w -> w.or(w1 -> w1.ge(ApprovalAttendanceDO::getStartTime, start).le(ApprovalAttendanceDO::getStartTime, end)).
                        or(w1 -> w1.ge(ApprovalAttendanceDO::getEndTime, start).le(ApprovalAttendanceDO::getEndTime, end)).
                        or(w1 -> w1.le(ApprovalAttendanceDO::getStartTime, start).ge(ApprovalAttendanceDO::getEndTime, end)));
        if (query.getStatus() != null) {
            wrapper.eq(ApprovalAttendanceDO::getSpStatus, query.getStatus().getStatus());
        }
        List<ApprovalAttendanceDO> list = approvalExtraWorkMapper.selectList(wrapper);
        return approvalConverter.toApprovalAttendances(list);
    }

    public boolean saveVacation(ApprovalVacation approvalVacation) {
        boolean isUpdateOrSave = true;
        ApprovalVacationDO approvalEntity = approvalConverter.toDO(approvalVacation);
        ApprovalVacationDO approvalSelectEntity = ApprovalVacationDO.builder().spNo(approvalVacation.getSpNo().no()).cpId(approvalVacation.getCpId().id()).build();
        ApprovalVacationDO existApproval = approvalVacationMapper.getOneOnly(approvalSelectEntity);
        if (Objects.nonNull(existApproval)) {
            if (approvalEntity.getSpStatus() > existApproval.getSpStatus()) {
                approvalEntity.setId(existApproval.getId());
                approvalVacationMapper.updateById(approvalEntity);
                log.info("Update approval: {}", approvalEntity);
            } else {
                isUpdateOrSave = false;
                log.info("Ignore approval: {}", approvalEntity);
            }
        } else {
            approvalVacationMapper.insert(approvalEntity);
            log.info("Save approval: {}", approvalEntity);
        }
        return isUpdateOrSave;
    }

    public List<ApprovalVacation> listPassedApprovalVacation(PassedApprovalQuery query) {
        long start = query.getStart().getTime() / 1000;
        long end = query.getEnd().getTime() / 1000;
        LambdaQueryWrapper<ApprovalVacationDO> wrapper = Wrappers.lambdaQuery(ApprovalVacationDO.class)
                .eq(ApprovalVacationDO::getCpId, query.getCpId().id())
                .eq(ApprovalVacationDO::getSpStatus, ApprovalStatus.PASSED.getStatus())
                .and(w -> w.or(w1 -> w1.ge(ApprovalVacationDO::getStartTime, start).le(ApprovalVacationDO::getStartTime, end)).
                        or(w1 -> w1.ge(ApprovalVacationDO::getEndTime, start).le(ApprovalVacationDO::getEndTime, end)).
                        or(w1 -> w1.le(ApprovalVacationDO::getStartTime, start).ge(ApprovalVacationDO::getEndTime, end)));
        if (query.getUserId() != null) {
            wrapper.eq(ApprovalVacationDO::getUserId, query.getUserId().id());
        }
        if (CollUtil.isNotEmpty(query.getUserIds())) {
            wrapper.in(ApprovalVacationDO::getUserId, query.getUserIds());
        }
        List<ApprovalVacationDO> list = approvalVacationMapper.selectList(wrapper);
        return approvalConverter.toApprovalVacations(list);
    }

    public List<ApprovalVacation> listApprovalVacation(ApprovalQuery query) {
        long start = query.getStart().getTime() / 1000;
        long end = query.getEnd().getTime() / 1000;
        LambdaQueryWrapper<ApprovalVacationDO> wrapper = Wrappers.lambdaQuery(ApprovalVacationDO.class)
                .eq(ApprovalVacationDO::getUserId, query.getUserId().id())
                .eq(ApprovalVacationDO::getCpId, query.getCpId().id())
                .and(w -> w.or(w1 -> w1.ge(ApprovalVacationDO::getStartTime, start).le(ApprovalVacationDO::getStartTime, end)).
                        or(w1 -> w1.ge(ApprovalVacationDO::getEndTime, start).le(ApprovalVacationDO::getEndTime, end)).
                        or(w1 -> w1.le(ApprovalVacationDO::getStartTime, start).ge(ApprovalVacationDO::getEndTime, end)));
        if (query.getStatus() != null) {
            wrapper.eq(ApprovalVacationDO::getSpStatus, query.getStatus().getStatus());
        }
        List<ApprovalVacationDO> list = approvalVacationMapper.selectList(wrapper);
        return approvalConverter.toApprovalVacations(list);
    }

    public boolean saveNight(ApprovalNight approvalNight) {
        boolean isUpdateOrSave = true;
        ApprovalNightDO approvalEntity = approvalConverter.toDO(approvalNight);
        ApprovalNightDO approvalSelectEntity = ApprovalNightDO.builder().spNo(approvalNight.getSpNo().no()).cpId(approvalNight.getCpId().id()).build();
        ApprovalNightDO existApproval = approvalNightMapper.getOneOnly(approvalSelectEntity);
        // 根据审批号，判断更新or新增
        if (Objects.nonNull(existApproval)) {
            if (approvalEntity.getSpStatus() > existApproval.getSpStatus()) {
                approvalEntity.setId(existApproval.getId());
                approvalNightMapper.updateById(approvalEntity);
                log.info("Update approval: {}", approvalEntity);
            } else {
                isUpdateOrSave = false;
                log.info("Ignore approval: {}", approvalEntity);
            }
        } else {
            approvalNightMapper.insert(approvalEntity);
            log.info("Save approval: {}", approvalEntity);
        }
        return isUpdateOrSave;
    }

    public List<ApprovalNight> listPassedApprovalNight(PassedApprovalQuery query) {
        long start = query.getStart().getTime() / 1000;
        long end = query.getEnd().getTime() / 1000;
        LambdaQueryWrapper<ApprovalNightDO> wrapper = Wrappers.lambdaQuery(ApprovalNightDO.class)
                .eq(ApprovalNightDO::getCpId, query.getCpId().id())
                .eq(ApprovalNightDO::getSpStatus, ApprovalStatus.PASSED.getStatus())
                .and(w -> w.or(w1 -> w1.ge(ApprovalNightDO::getStartTime, start).le(ApprovalNightDO::getStartTime, end)).
                        or(w1 -> w1.ge(ApprovalNightDO::getEndTime, start).le(ApprovalNightDO::getEndTime, end)).
                        or(w1 -> w1.le(ApprovalNightDO::getStartTime, start).ge(ApprovalNightDO::getEndTime, end)));
        if (query.getUserId() != null) {
            wrapper.eq(ApprovalNightDO::getUserId, query.getUserId().id());
        }
        if (CollUtil.isNotEmpty(query.getUserIds())) {
            wrapper.in(ApprovalNightDO::getUserId, query.getUserIds());
        }
        List<ApprovalNightDO> list = approvalNightMapper.selectList(wrapper);
        return approvalConverter.toApprovalNights(list);
    }

    public List<ApprovalNight> listApprovalNight(ApprovalQuery query) {
        long start = query.getStart().getTime() / 1000;
        long end = query.getEnd().getTime() / 1000;
        LambdaQueryWrapper<ApprovalNightDO> wrapper = Wrappers.lambdaQuery(ApprovalNightDO.class)
                .eq(ApprovalNightDO::getUserId, query.getUserId().id())
                .eq(ApprovalNightDO::getCpId, query.getCpId().id())
                .and(w -> w.or(w1 -> w1.ge(ApprovalNightDO::getStartTime, start).le(ApprovalNightDO::getStartTime, end)).
                        or(w1 -> w1.ge(ApprovalNightDO::getEndTime, start).le(ApprovalNightDO::getEndTime, end)).
                        or(w1 -> w1.le(ApprovalNightDO::getStartTime, start).ge(ApprovalNightDO::getEndTime, end)));
        if (query.getStatus() != null) {
            wrapper.eq(ApprovalNightDO::getSpStatus, query.getStatus().getStatus());
        }
        List<ApprovalNightDO> list = approvalNightMapper.selectList(wrapper);
        return approvalConverter.toApprovalNights(list);
    }

    public boolean saveTemporaryChange(ApprovalTemporaryChange approvalTemporaryChange) {
        boolean isUpdateOrSave = true;
        ApprovalTemporaryChangeDO approvalEntity = approvalConverter.toDO(approvalTemporaryChange);
        ApprovalTemporaryChangeDO approvalSelectEntity = ApprovalTemporaryChangeDO.builder().spNo(approvalTemporaryChange.getSpNo().no()).cpId(approvalTemporaryChange.getCpId().id()).build();
        ApprovalTemporaryChangeDO existApproval = approvalTemporaryChangeMapper.getOneOnly(approvalSelectEntity);
        // 根据审批号，判断更新or新增
        if (Objects.nonNull(existApproval)) {
            if (approvalEntity.getSpStatus() > existApproval.getSpStatus()) {
                approvalEntity.setId(existApproval.getId());
                approvalTemporaryChangeMapper.updateById(approvalEntity);
                log.info("Update approval: {}", approvalEntity);
            } else {
                isUpdateOrSave = false;
                log.info("Ignore approval: {}", approvalEntity);
            }
        } else {
            approvalTemporaryChangeMapper.insert(approvalEntity);
            log.info("Save approval: {}", approvalEntity);
        }
        return isUpdateOrSave;
    }

    public List<ApprovalTemporaryChange> listPassedTemporaryChangeNight(PassedApprovalQuery query) {
        long start = query.getStart().getTime() / 1000;
        long end = query.getEnd().getTime() / 1000;
        LambdaQueryWrapper<ApprovalTemporaryChangeDO> wrapper = Wrappers.lambdaQuery(ApprovalTemporaryChangeDO.class)
                .eq(ApprovalTemporaryChangeDO::getCpId, query.getCpId().id())
                .eq(ApprovalTemporaryChangeDO::getSpStatus, ApprovalStatus.PASSED.getStatus())
                .and(w -> w.or(w1 -> w1.ge(ApprovalTemporaryChangeDO::getStartTime, start).le(ApprovalTemporaryChangeDO::getStartTime, end)).
                        or(w1 -> w1.ge(ApprovalTemporaryChangeDO::getEndTime, start).le(ApprovalTemporaryChangeDO::getEndTime, end)).
                        or(w1 -> w1.le(ApprovalTemporaryChangeDO::getStartTime, start).ge(ApprovalTemporaryChangeDO::getEndTime, end)));
        if (query.getUserId() != null) {
            wrapper.eq(ApprovalTemporaryChangeDO::getUserId, query.getUserId().id());
        }
        if (CollUtil.isNotEmpty(query.getUserIds())) {
            wrapper.in(ApprovalTemporaryChangeDO::getUserId, query.getUserIds());
        }
        List<ApprovalTemporaryChangeDO> list = approvalTemporaryChangeMapper.selectList(wrapper);
        return approvalConverter.toApprovalTemporaryChanges(list);
    }

    public List<ApprovalTemporaryChange> listTemporaryChangeNight(ApprovalQuery query) {
        long start = query.getStart().getTime() / 1000;
        long end = query.getEnd().getTime() / 1000;
        LambdaQueryWrapper<ApprovalTemporaryChangeDO> wrapper = Wrappers.lambdaQuery(ApprovalTemporaryChangeDO.class)
                .eq(ApprovalTemporaryChangeDO::getUserId, query.getUserId().id())
                .eq(ApprovalTemporaryChangeDO::getCpId, query.getCpId().id())
                .and(w -> w.or(w1 -> w1.ge(ApprovalTemporaryChangeDO::getStartTime, start).le(ApprovalTemporaryChangeDO::getStartTime, end)).
                        or(w1 -> w1.ge(ApprovalTemporaryChangeDO::getEndTime, start).le(ApprovalTemporaryChangeDO::getEndTime, end)).
                        or(w1 -> w1.le(ApprovalTemporaryChangeDO::getStartTime, start).ge(ApprovalTemporaryChangeDO::getEndTime, end)));
        if (query.getStatus() != null) {
            wrapper.eq(ApprovalTemporaryChangeDO::getSpStatus, query.getStatus().getStatus());
        }
        List<ApprovalTemporaryChangeDO> list = approvalTemporaryChangeMapper.selectList(wrapper);
        return approvalConverter.toApprovalTemporaryChanges(list);
    }

    public boolean saveCorrection(ApprovalCorrection approvalCorrection) {
        boolean isUpdateOrSave = true;
        ApprovalPunchCorrectionDO approvalEntity = approvalConverter.toDO(approvalCorrection);
        ApprovalPunchCorrectionDO approvalSelectEntity = ApprovalPunchCorrectionDO.builder().spNo(approvalCorrection.getSpNo().no()).cpId(approvalCorrection.getCpId().id()).build();
        ApprovalPunchCorrectionDO existApproval = approvalPunchCorrectionMapper.getOneOnly(approvalSelectEntity);
        if (Objects.nonNull(existApproval)) {
            if (approvalEntity.getSpStatus() > existApproval.getSpStatus()) {
                approvalEntity.setId(existApproval.getId());
                approvalPunchCorrectionMapper.updateById(approvalEntity);
                log.info("Update approval: {}", approvalEntity);
            } else {
                isUpdateOrSave = false;
                log.info("Ignore approval: {}", approvalEntity);
            }
        } else {
            approvalPunchCorrectionMapper.insert(approvalEntity);
            log.info("Save approval: {}", approvalEntity);
        }
        return isUpdateOrSave;
    }

    public void changeCorrectionRed(ApprovalCorrection approvalCorrection) {
        ApprovalPunchCorrectionDO approvalEntity = approvalConverter.toDO(approvalCorrection);
        ApprovalPunchCorrectionDO approvalSelectEntity = ApprovalPunchCorrectionDO.builder().spNo(approvalCorrection.getSpNo().no()).cpId(approvalCorrection.getCpId().id()).build();
        ApprovalPunchCorrectionDO existApproval = approvalPunchCorrectionMapper.getOneOnly(approvalSelectEntity);
        if (Objects.nonNull(existApproval)) {
            approvalEntity.setId(existApproval.getId());
            approvalEntity.setRecalculate(1);
            approvalPunchCorrectionMapper.updateById(approvalEntity);
        }
    }

    public List<ApprovalCorrection> listPassedApprovalCorrect(PassedApprovalQuery query) {
        long start = query.getStart().getTime() / 1000;
        long end = query.getEnd().getTime() / 1000;
        LambdaQueryWrapper<ApprovalPunchCorrectionDO> wrapper = Wrappers.lambdaQuery(ApprovalPunchCorrectionDO.class)
                .eq(ApprovalPunchCorrectionDO::getCpId, query.getCpId().id())
                .eq(ApprovalPunchCorrectionDO::getSpStatus, ApprovalStatus.PASSED.getStatus())
                .ge(ApprovalPunchCorrectionDO::getCorrectionTime, start).le(ApprovalPunchCorrectionDO::getCorrectionTime, end);
        if (query.getUserId() != null) {
            wrapper.eq(ApprovalPunchCorrectionDO::getUserId, query.getUserId().id());
        }
        if (CollUtil.isNotEmpty(query.getUserIds())) {
            wrapper.in(ApprovalPunchCorrectionDO::getUserId, query.getUserIds());
        }
        List<ApprovalPunchCorrectionDO> list = approvalPunchCorrectionMapper.selectList(wrapper);
        return approvalConverter.toApprovalPunchCorrections(list);
    }

    public List<ApprovalCorrection> listRecalculateApprovalCorrect() {
        LambdaQueryWrapper<ApprovalPunchCorrectionDO> wrapper = Wrappers.lambdaQuery(ApprovalPunchCorrectionDO.class)
                .eq(ApprovalPunchCorrectionDO::getSpStatus, ApprovalStatus.PASSED.getStatus())
                .eq(ApprovalPunchCorrectionDO::getRecalculate, 0);
        List<ApprovalPunchCorrectionDO> list = approvalPunchCorrectionMapper.selectList(wrapper);
        return approvalConverter.toApprovalPunchCorrections(list);
    }

    public List<ApprovalCorrection> listApprovalCorrect(ApprovalQuery query) {
        long start = query.getStart().getTime() / 1000;
        long end = query.getEnd().getTime() / 1000;
        LambdaQueryWrapper<ApprovalPunchCorrectionDO> wrapper = Wrappers.lambdaQuery(ApprovalPunchCorrectionDO.class)
                .eq(ApprovalPunchCorrectionDO::getUserId, query.getUserId().id())
                .eq(ApprovalPunchCorrectionDO::getCpId, query.getCpId().id())
                .ge(ApprovalPunchCorrectionDO::getCorrectionTime, start).le(ApprovalPunchCorrectionDO::getCorrectionTime, end);
        if (query.getStatus() != null) {
            wrapper.eq(ApprovalPunchCorrectionDO::getSpStatus, query.getStatus().getStatus());
        }
        List<ApprovalPunchCorrectionDO> list = approvalPunchCorrectionMapper.selectList(wrapper);
        return approvalConverter.toApprovalPunchCorrections(list);
    }

    public List<String> listApprovalOfAuditing(Date startDate, Date endDate, String cpId) {
        List<String> approvalNoList = new ArrayList<>();
        long start = startDate.getTime() / 1000;
        long end = endDate.getTime() / 1000;
        LambdaQueryWrapper<ApprovalPunchCorrectionDO> wrapper1 = Wrappers.lambdaQuery(ApprovalPunchCorrectionDO.class)
                .eq(ApprovalPunchCorrectionDO::getCpId, cpId)
                .eq(ApprovalPunchCorrectionDO::getSpStatus, ApprovalStatus.AUDITING.getStatus())
                .ge(ApprovalPunchCorrectionDO::getApplyTime, start).le(ApprovalPunchCorrectionDO::getApplyTime, end);
        List<ApprovalPunchCorrectionDO> list1 = approvalPunchCorrectionMapper.selectList(wrapper1);
        List<String> punchCorrectionSpNoList = list1.stream().map(ApprovalPunchCorrectionDO::getSpNo).collect(Collectors.toList());

        LambdaQueryWrapper<ApprovalTemporaryChangeDO> wrapper2 = Wrappers.lambdaQuery(ApprovalTemporaryChangeDO.class)
                .eq(ApprovalTemporaryChangeDO::getCpId, cpId)
                .eq(ApprovalTemporaryChangeDO::getSpStatus, ApprovalStatus.AUDITING.getStatus())
                .ge(ApprovalTemporaryChangeDO::getApplyTime, start).le(ApprovalTemporaryChangeDO::getApplyTime, end);
        List<ApprovalTemporaryChangeDO> list2 = approvalTemporaryChangeMapper.selectList(wrapper2);
        List<String> temporaryChangeSpNoList = list2.stream().map(ApprovalTemporaryChangeDO::getSpNo).collect(Collectors.toList());

        LambdaQueryWrapper<ApprovalAttendanceDO> wrapper3 = Wrappers.lambdaQuery(ApprovalAttendanceDO.class)
                .eq(ApprovalAttendanceDO::getCpId, cpId)
                .eq(ApprovalAttendanceDO::getSpStatus, ApprovalStatus.AUDITING.getStatus())
                .ge(ApprovalAttendanceDO::getApplyTime, start).le(ApprovalAttendanceDO::getApplyTime, end);
        List<ApprovalAttendanceDO> list3 = approvalExtraWorkMapper.selectList(wrapper3);
        List<String> extraWorkSpNoList = list3.stream().map(ApprovalAttendanceDO::getSpNo).collect(Collectors.toList());

        LambdaQueryWrapper<ApprovalVacationDO> wrapper4 = Wrappers.lambdaQuery(ApprovalVacationDO.class)
                .eq(ApprovalVacationDO::getCpId, cpId)
                .eq(ApprovalVacationDO::getSpStatus, ApprovalStatus.AUDITING.getStatus())
                .ge(ApprovalVacationDO::getApplyTime, start).le(ApprovalVacationDO::getApplyTime, end);
        List<ApprovalVacationDO> list4 = approvalVacationMapper.selectList(wrapper4);
        List<String> vacationSpNoList = list4.stream().map(ApprovalVacationDO::getSpNo).collect(Collectors.toList());

        LambdaQueryWrapper<ApprovalNightDO> wrapper5 = Wrappers.lambdaQuery(ApprovalNightDO.class)
                .eq(ApprovalNightDO::getCpId, cpId)
                .eq(ApprovalNightDO::getSpStatus, ApprovalStatus.AUDITING.getStatus())
                .ge(ApprovalNightDO::getApplyTime, start).le(ApprovalNightDO::getApplyTime, end);
        List<ApprovalNightDO> list5 = approvalNightMapper.selectList(wrapper5);
        List<String> nightSpNoList = list5.stream().map(ApprovalNightDO::getSpNo).collect(Collectors.toList());

        approvalNoList.addAll(punchCorrectionSpNoList);
        approvalNoList.addAll(temporaryChangeSpNoList);
        approvalNoList.addAll(extraWorkSpNoList);
        approvalNoList.addAll(vacationSpNoList);
        approvalNoList.addAll(nightSpNoList);
        return approvalNoList;
    }
}
