package com.greenstone.mes.oa.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.constant.SecurityConstants;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.oa.domain.repository.CustomShiftRepository;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.wxcp.domain.helper.WxDeptService;
import com.greenstone.mes.wxcp.domain.helper.WxUserService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.WxDeptId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import com.greenstone.mes.oa.application.assembler.AttendanceAssembler;
import com.greenstone.mes.oa.application.dto.ApprovalQuery;
import com.greenstone.mes.oa.application.dto.AttendCheatResult;
import com.greenstone.mes.oa.application.dto.PassedApprovalQuery;
import com.greenstone.mes.oa.application.dto.attendance.AttendanceUserCalcDTO;
import com.greenstone.mes.oa.application.service.AttendanceService;
import com.greenstone.mes.oa.application.service.OaWxScheduleService;
import com.greenstone.mes.oa.application.service.WxCheckinDataService;
import com.greenstone.mes.oa.domain.entity.*;
import com.greenstone.mes.oa.domain.helper.AttendanceHelper;
import com.greenstone.mes.oa.domain.repository.AttendanceResultRepository;
import com.greenstone.mes.oa.domain.repository.WxApprovalRepository;
import com.greenstone.mes.oa.domain.service.impl.AttendanceCalcServiceImpl;
import com.greenstone.mes.oa.infrastructure.enums.ScheduleShift;
import com.greenstone.mes.oa.infrastructure.enums.WxCp;
import com.greenstone.mes.oa.interfaces.request.AttendanceMyMonthQuery;
import com.greenstone.mes.oa.interfaces.resp.AttendanceMyMonthResult;
import com.greenstone.mes.oa.request.SyncCheckinDataCmd;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.model.LoginUser;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.WxCpDepart;
import me.chanjar.weixin.cp.bean.WxCpUser;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author gu_renkai
 * @date 2022/11/25 9:29
 */
@Slf4j
@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private WxUserService externalWxUserService;
    @Autowired
    private WxDeptService externalWxDeptService;
    @Autowired
    private WxApprovalRepository approvalRepository;
    @Autowired
    private AttendanceAssembler attendanceAssembler;
    @Autowired
    private AttendanceResultRepository attendanceResultRepository;
    @Autowired
    private AttendanceCalcServiceImpl attendanceCalcService;
    @Autowired
    private AttendanceHelper attendanceHelper;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private OaWxScheduleService oaWxScheduleService;
    @Autowired
    private WxCheckinDataService wxCheckinDataService;
    @Autowired
    private CustomShiftRepository customShiftRepository;

    @Override
    public List<AttendanceResultDetail> calcYesterday() {
        Date date = DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, -1);
        return calc(date, date);
    }

    @Override
    public void calcAndSaveYesterday() {
        Date yesterday = DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, -1);
        Date beforeYesterday = DateUtil.offset(yesterday, DateField.DAY_OF_MONTH, -1);
        log.info("开始计算昨日考勤，时间：{}----{}", beforeYesterday, yesterday);
        long start = System.currentTimeMillis();
        List<AttendanceResultDetail> resultDetails = calc(beforeYesterday, yesterday);
        long end = System.currentTimeMillis();
        log.info("cal use time:{}-{}={}毫秒", start, end, start - end);
        attendanceResultRepository.remove(beforeYesterday, yesterday);
        attendanceResultRepository.addBatch(resultDetails);
    }

    @Override
    @Async
    public void calcAndSaveYesterdayAsync() {
        calcAndSaveYesterday();
        // 补卡遗漏重算
        recalculateCorrection();
    }

    public void recalculateCorrection(){
        log.info("开始补卡重算");
        List<ApprovalCorrection> approvalCorrectionsOfRe = approvalRepository.listRecalculateApprovalCorrect();
        log.info("总计重算{}个", approvalCorrectionsOfRe.size());
        for (ApprovalCorrection approval : approvalCorrectionsOfRe) {
            // 更新打卡数据
            SyncCheckinDataCmd syncCheckinDataCmd = SyncCheckinDataCmd.builder().cpId(approval.getCpId().id()).wxUserId(approval.getUserId().id())
                    .startDate(DateUtil.beginOfDay(approval.getCorrectionTime())).endDate(DateUtil.endOfDay(approval.getCorrectionTime())).build();
            try {
                log.info("同步补卡打卡数据：{}", syncCheckinDataCmd);
                wxCheckinDataService.syncCheckData(syncCheckinDataCmd);
                log.info("重算考勤数据");
                calcAndSave(approval.getCorrectionTime(), approval.getCorrectionTime(), approval.getCpId(), approval.getUserId());
                log.info("重算完成");
                approvalRepository.changeCorrectionRed(approval);
            } catch (Exception e) {
                log.error("重算失败：{}", syncCheckinDataCmd);
            }
        }
        log.info("结束昨日考勤统计");
    }

    @Override
    public List<AttendanceResultDetail> calc(Date start, Date end) {
        List<AttendanceResultDetail> resultDetails = new ArrayList<>();
        for (WxCp wxCp : WxCp.values()) {
            resultDetails.addAll(calc(start, end, new CpId(wxCp.getCpId()), ""));
        }
        return resultDetails;
    }

    @Override
    @Async
    public void calcAndSaveAsync(Date start, Date end) {
        calcAndSave(start, end);
    }

    @Override
    public void calcAndSave(Date start, Date end) {
        attendanceResultRepository.remove(start, end);
        List<AttendanceResultDetail> resultDetails = calc(start, end);
        attendanceResultRepository.addBatch(resultDetails);
    }

    @Override
    public List<AttendanceResultDetail> calc(Date start, Date end, CpId cpId, String userId) {
        List<WxUserId> wxUserIds;
        if (StrUtil.isNotEmpty(userId)) {
            wxUserIds = List.of(new WxUserId(userId));
        } else {
            List<WxCpUser> allUsers = externalWxUserService.listAllUser(cpId);
            wxUserIds = allUsers.stream().map(u -> new WxUserId(u.getUserId())).toList();
        }
        return new ArrayList<>(calc(start, end, cpId, wxUserIds));
    }

    @Override
    @Async
    public void calcAndSaveAsync(Date start, Date end, CpId cpId, String userId) {
        calcAndSave(start, end, cpId, userId);
    }

    @Override
    public void calcAndSave(Date start, Date end, CpId cpId, String userId) {
        log.info("calcAndSave params:start{},end{},cpId{},userId{}", start, end, cpId, userId);
        long calStart = System.currentTimeMillis();
        List<AttendanceResultDetail> resultDetails = calc(start, end, cpId, userId);
        long calEnd = System.currentTimeMillis();
        log.info("cal use time:{}-{}={}毫秒", calStart, calEnd, calStart - calEnd);
        attendanceResultRepository.remove(start, end, cpId, userId);
        attendanceResultRepository.addBatch(resultDetails);
    }

    @Override
    public List<AttendanceResultDetail> calc(Date start, Date end, CpId cpId, List<WxUserId> wxUserIds) {
        List<AttendanceResultDetail> resultDetails = new ArrayList<>();
        // 每一天的时间戳，代表需要计算考勤的日期
        List<Long> dayBeginTimes = attendanceHelper.getBeginTimeStampOfDays(start, end);
        // 需要计算的用户ID
        List<String> userIds = wxUserIds.stream().map(WxUserId::id).toList();
        // 拿到班次信息
        List<Schedule> schedules = oaWxScheduleService.listSchedule(cpId, start, end, userIds);
        // 数据获取范围为 开始时间0点 到 结束时间第二天末 的打卡数据
        start = DateUtil.beginOfDay(start);
        end = DateUtil.offset(DateUtil.endOfDay(end), DateField.DAY_OF_MONTH, 1);


        // 获取打卡数据
        List<WxCpCheckinData> wxCheckinDataList = wxCheckinDataService.listCheckinData(cpId, start, end, userIds);
        wxCheckinDataList = wxCheckinDataList.stream().filter(c -> !c.getExceptionType().contains("未打卡")).toList();
        List<CheckinData> checkinDataList = attendanceAssembler.toCheckinDataList(wxCheckinDataList);
        // 拿到所有用户的加班、夜班、请假的审批
        PassedApprovalQuery query = PassedApprovalQuery.builder().cpId(cpId).userIds(userIds).start(start).end(end).build();
        List<ApprovalExtraWork> approvalExtraWorksOfAll = approvalRepository.listPassedApprovalAttendance(query);
        List<ApprovalNight> approvalNightsOfAll = approvalRepository.listPassedApprovalNight(query);
        List<ApprovalTemporaryChange> approvalTemporaryChangesOfAll = approvalRepository.listPassedTemporaryChangeNight(query);
        List<ApprovalVacation> approvalVacationsOfAll = approvalRepository.listPassedApprovalVacation(query);
        List<ApprovalCorrection> approvalCorrectionsOfAll = approvalRepository.listPassedApprovalCorrect(query);
        // 拿到所有自定义排班
        List<CustomShift> customShifts = customShiftRepository.list(new CustomShift());

        for (WxUserId wxUserId : wxUserIds) {
            // 拿到用户的加班、夜班、请假的审批
            List<ApprovalExtraWork> approvalExtraWorks = approvalExtraWorksOfAll.stream().filter(a -> a.getUserId().id().equals(wxUserId.id())).collect(Collectors.toList());
            List<ApprovalNight> approvalNights = approvalNightsOfAll.stream().filter(a -> a.getUserId().id().equals(wxUserId.id())).collect(Collectors.toList());
            List<ApprovalTemporaryChange> approvalTemporaryChanges = approvalTemporaryChangesOfAll.stream().filter(a -> a.getUserId().id().equals(wxUserId.id())).collect(Collectors.toList());
            List<ApprovalVacation> approvalVacations = approvalVacationsOfAll.stream().filter(a -> a.getUserId().id().equals(wxUserId.id())).collect(Collectors.toList());
            List<ApprovalCorrection> approvalCorrections = approvalCorrectionsOfAll.stream().filter(a -> a.getUserId().id().equals(wxUserId.id())).collect(Collectors.toList());
            // 拿到用户的班次信息
            List<Schedule> userSchedules = schedules.stream().filter(s -> s.getUserId().equals(wxUserId)).toList();
            // 获取完整的用户和部门信息
            WxCpUser wxUser = externalWxUserService.getUser(cpId, wxUserId);
            OaWxUser user = new OaWxUser(wxUserId, wxUser.getName());
            WxCpDepart wxDepart = externalWxDeptService.getDept(cpId, new WxDeptId(wxUser.getDepartIds()[0]));
            WxDept dept = attendanceAssembler.toWxDept(wxDepart);
            attendanceHelper.setDeptFullName(dept, cpId, wxDepart);
            // 组装计算一个用户一段时间考勤所需的数据
            AttendanceUserCalcDTO userCalcDTO = AttendanceUserCalcDTO.builder().cpId(cpId).user(user).dept(dept).checkinDataList(checkinDataList)
                    .schedules(userSchedules).customShifts(customShifts).approvalExtraWorks(approvalExtraWorks).approvalNights(approvalNights).approvalTemporaryChanges(approvalTemporaryChanges)
                    .approvalVacations(approvalVacations).approvalCorrections(approvalCorrections).build();
            // 计算一个用户一段时间的考勤
            resultDetails.addAll(calcRangeAttendance(dayBeginTimes, userCalcDTO));
        }
        return resultDetails;
    }

    @Override
    public void calcAndSave(Date start, Date end, CpId cpId, List<WxUserId> wxUserIds) {
        attendanceResultRepository.remove(start, end, cpId, wxUserIds);
        List<AttendanceResultDetail> resultDetails = calc(start, end, cpId, wxUserIds);
        attendanceResultRepository.addBatch(resultDetails);
    }

    @Override
    public void calcAndSave(Date start, Date end, CpId cpId, WxUserId userId) {
        calcAndSave(start, end, cpId, List.of(userId));
    }

    @Override
    public AttendanceMyMonthResult statMyMonthAttendance(AttendanceMyMonthQuery query) {
        DateTime endOfMonth = DateUtil.endOfMonth(query.getMonth());
        User user = SecurityUtils.getLoginUser().getUser();
        List<AttendanceResult> resultList = attendanceResultRepository.listResult(query.getMonth(), endOfMonth,
                new CpId(user.getWxCpId()), new WxUserId(user.getWxUserId()));
        // 拿到用户的加班、夜班、请假的审批
        ApprovalQuery approvalQuery =
                ApprovalQuery.builder().cpId(new CpId(user.getWxCpId())).userId(new WxUserId(user.getWxUserId())).start(query.getMonth()).end(endOfMonth).build();
        List<ApprovalExtraWork> approvalExtraWorks = approvalRepository.listApprovalAttendance(approvalQuery);
        List<ApprovalNight> approvalNights = approvalRepository.listApprovalNight(approvalQuery);
        List<ApprovalTemporaryChange> approvalTemporaryChanges = approvalRepository.listTemporaryChangeNight(approvalQuery);
        List<ApprovalVacation> approvalVacations = approvalRepository.listApprovalVacation(approvalQuery);
        List<ApprovalCorrection> approvalCorrections = approvalRepository.listApprovalCorrect(approvalQuery);
        return attendanceAssembler.toMonthResult(resultList, approvalExtraWorks, approvalNights, approvalTemporaryChanges, approvalVacations, approvalCorrections, user);
    }

    @Override
    public List<AttendCheatResult> analyseCheat(Date start, Date end, CpId cpId) {
        List<WxCpUser> allWxUsers = externalWxUserService.listAllUser(cpId);
        List<WxCpDepart> allWxDept = externalWxDeptService.listDept(cpId, null);
        List<AttendCheatResult> cheatResults = new ArrayList<>();

        List<String> wxUserIds = allWxUsers.stream().map(WxCpUser::getUserId).toList();

        List<WxCpCheckinData> checkinDataList = attendanceHelper.listCheckinData(cpId, start, end, wxUserIds);

        checkinDataList = checkinDataList.stream().filter(d -> d.getSchCheckinTime() != null && d.getCheckinTime() != null && StrUtil.isNotEmpty(d.getDeviceId())).toList();
        Map<Long, List<WxCpCheckinData>> schCheckinTimeMap = checkinDataList.stream().collect(Collectors.groupingBy(WxCpCheckinData::getSchCheckinTime));
        schCheckinTimeMap.forEach((schCheckinTime, checkinListByTime) -> {
            Map<String, List<WxCpCheckinData>> deviceMap = checkinListByTime.stream().collect(Collectors.groupingBy(WxCpCheckinData::getDeviceId));
            deviceMap.forEach((deviceId, checkinListByDevice) -> {
                checkinListByDevice = checkinListByDevice.stream().filter(distinctPredicate(WxCpCheckinData::getUserId)).toList();
                if (checkinListByDevice.size() > 1) {
                    for (WxCpCheckinData wxCpCheckinData : checkinListByDevice) {
                        WxCpUser wxCpUser = allWxUsers.stream().filter(u -> u.getUserId().equals(wxCpCheckinData.getUserId())).findFirst().orElse(null);
                        WxCpDepart wxCpDepart = allWxDept.stream().filter(d -> {
                            assert wxCpUser != null;
                            return d.getId().toString().equals(wxCpUser.getMainDepartment());
                        }).findFirst().orElse(null);

                        AttendCheatResult cheatResult = AttendCheatResult.builder().deviceId(deviceId)
                                .checkinTime(DateUtil.format(new Date(wxCpCheckinData.getCheckinTime() * 1000), "yyyy-MM-dd HH:mm:ss"))
                                .schCheckinTime(DateUtil.format(new Date(wxCpCheckinData.getSchCheckinTime() * 1000), "yyyy-MM-dd HH:mm:ss"))
                                .deptName(wxCpDepart == null ? null : wxCpDepart.getName())
                                .userName(wxCpUser == null ? null : wxCpUser.getName())
                                .location(wxCpCheckinData.getLocationTitle())
                                .remark(wxCpCheckinData.getNotes())
                                .build();
                        cheatResults.add(cheatResult);
                    }
                }
            });
        });
        return cheatResults;
    }

    public static <K> Predicate<K> distinctPredicate(Function<K, Object> function) {
        ConcurrentHashMap<Object, Boolean> map = new ConcurrentHashMap<>();
        return (t) -> null == map.putIfAbsent(function.apply(t), true);
    }

    private List<AttendanceResultDetail> calcRangeAttendance(List<Long> dayBeginTimes, AttendanceUserCalcDTO
            userCalcDTO) {
        List<AttendanceResultDetail> resultDetails = new ArrayList<>();
        for (Long dayBeginTime : dayBeginTimes) {
            // 拿到当天的排班
            Schedule schedule = attendanceHelper.findSchedule(userCalcDTO.getSchedules(), dayBeginTime, userCalcDTO.getUser().getUserId());
            // 拿到当天的夜班审批
            List<ApprovalNight> todayNights = userCalcDTO.getApprovalNights().stream()
                    .filter(n -> n.getStartTime().getTime() / 1000 <= dayBeginTime &&
                            n.getEndTime().getTime() / 1000 >= dayBeginTime).toList();
            // 拿到当天的临时白班变更登记审批
            List<ApprovalTemporaryChange> todayTemporaryChanges = userCalcDTO.getApprovalTemporaryChanges().stream()
                    .filter(n -> n.getStartTime().getTime() / 1000 <= dayBeginTime &&
                            n.getEndTime().getTime() / 1000 >= dayBeginTime).toList();
            // 判断班次和允许打卡的时间
            ScheduleShift shift = AttendanceHelper.pickScheduleShift(CollUtil.isNotEmpty(todayNights), CollUtil.isNotEmpty(todayTemporaryChanges));
            TimeSection allowedCheckinTime = AttendanceHelper.getAllowedCheckinSection(dayBeginTime, shift);
            // 拿到当天的打卡记录
            List<CheckinData> todayCheckinDataList = userCalcDTO.getCheckinDataList().stream()
                    .filter(c -> c.getUserId().equals(userCalcDTO.getUser().getUserId()))
                    .filter(c -> c.getCheckinTime() >= allowedCheckinTime.start() && c.getCheckinTime() <= allowedCheckinTime.end()).toList();
            // 拿到当天的加班审批
            List<ApprovalExtraWork> todayExtraWorks = userCalcDTO.getApprovalExtraWorks().stream().filter(a -> {
                long approvalStart = a.getStartTime().getTime() / 1000;
                long approvalEnd = a.getEndTime().getTime() / 1000;
                return (approvalStart >= allowedCheckinTime.start() && approvalStart <= allowedCheckinTime.end()) ||
                        (approvalEnd >= allowedCheckinTime.start() && approvalEnd <= allowedCheckinTime.end()) ||
                        (approvalStart < allowedCheckinTime.start() && approvalEnd > allowedCheckinTime.end());
            }).toList();
            // 拿到当天的请假审批
            List<ApprovalVacation> todayVacations = userCalcDTO.getApprovalVacations().stream().filter(a -> {
                long approvalStart = a.getStartTime().getTime() / 1000;
                long approvalEnd = a.getEndTime().getTime() / 1000;
                return (approvalStart >= allowedCheckinTime.start() && approvalStart <= allowedCheckinTime.end()) ||
                        (approvalEnd >= allowedCheckinTime.start() && approvalEnd <= allowedCheckinTime.end()) ||
                        (approvalStart < allowedCheckinTime.start() && approvalEnd > allowedCheckinTime.end());
            }).toList();
            // 拿到当天的补卡审批
            List<ApprovalCorrection> todayCorrections = userCalcDTO.getApprovalCorrections().stream().filter(a -> {
                long approvalTime = a.getCorrectionTime().getTime() / 1000;
                return (approvalTime >= allowedCheckinTime.start() && approvalTime <= allowedCheckinTime.end());
            }).toList();
            // 组装计算一个用户一天考勤所需的数据
            AttendanceUserDayCalcDTO userDayCalcDTO = AttendanceUserDayCalcDTO.builder().cpId(userCalcDTO.getCpId()).user(userCalcDTO.getUser())
                    .dayBeginTime(dayBeginTime).dept(userCalcDTO.getDept()).schedule(schedule).shift(shift).customShifts(userCalcDTO.getCustomShifts())
                    .checkinDataList(todayCheckinDataList).approvalNights(todayNights).approvalExtraWorks(todayExtraWorks)
                    .approvalVacations(todayVacations).approvalCorrections(todayCorrections)
                    .allCheckinDataList(userCalcDTO.getCheckinDataList()).allowedCheckinTime(allowedCheckinTime).build();
            // 计算一个用户一天的考勤
            resultDetails.add(calcDailyAttendance(userDayCalcDTO));
        }
        return resultDetails;
    }

    private AttendanceResultDetail calcDailyAttendance(@Valid AttendanceUserDayCalcDTO userDayCalcDTO) {
        return attendanceCalcService.calc(userDayCalcDTO);
    }

}
