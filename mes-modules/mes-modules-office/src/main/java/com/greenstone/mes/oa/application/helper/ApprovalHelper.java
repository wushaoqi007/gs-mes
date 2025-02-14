package com.greenstone.mes.oa.application.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.oa.domain.entity.ApprovalNight;
import com.greenstone.mes.oa.domain.entity.ApprovalTemporaryChange;
import com.greenstone.mes.oa.domain.entity.ScheduleShiftOption;
import com.greenstone.mes.oa.domain.helper.AttendanceHelper;
import com.greenstone.mes.oa.infrastructure.enums.ApprovalStatus;
import com.greenstone.mes.wxcp.domain.helper.WxMsgService;
import com.greenstone.mes.wxcp.domain.helper.WxOaService;
import com.greenstone.mes.wxcp.infrastructure.config.WxConfig;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.Gender;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinSchedule;
import me.chanjar.weixin.cp.bean.oa.WxCpCropCheckinOption;
import me.chanjar.weixin.cp.bean.oa.WxCpSetCheckinSchedule;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gu_renkai
 * @date 2022/8/17 13:06
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class ApprovalHelper {

    private final WxConfig wxConfig;
    private final WxOaService externalWxOaService;
    private final WxMsgService externalWxMsgService;
    private final AttendanceHelper attendanceHelper;


    public void syncNightShift(ApprovalNight approvalNight) {
        log.info("Update wx checkin schedule by night shift approval, cpId:{}, spNo:{}", approvalNight.getCpId().id(), approvalNight.getSpNo().no());
        String scheduleName;
        if (approvalNight.getStatus().equals(ApprovalStatus.PASSED)) {
            scheduleName = "晚班";
        } else if (approvalNight.getStatus().equals(ApprovalStatus.PASS_UNDONE)) {
            scheduleName = "早班";
        } else {
            return;
        }
        // 设置班次转换选项
        ScheduleShiftOption scheduleShiftOption = ScheduleShiftOption.builder().cpId(approvalNight.getCpId()).userId(approvalNight.getUserId()).spNo(approvalNight.getSpNo())
                .startTime(approvalNight.getStartTime()).endTime(approvalNight.getEndTime()).spName("夜班").status(approvalNight.getStatus())
                .scheduleName(scheduleName).build();
        // 找到排班规则
        scheduleShiftOption = findCheckinOption(scheduleShiftOption);
        if (scheduleShiftOption.getCheckinOptionSchedule() == null) {
            log.error("Can not find night shift checkin option by name {}", scheduleName);
            return;
        }
        // 设置班次
        setCheckinSchedule(scheduleShiftOption);

    }

    public void syncDayShift(ApprovalTemporaryChange approvalTemporaryChange) {
        log.info("Update wx checkin schedule by temp change approval, cpId:{}, spNo:{}", approvalTemporaryChange.getCpId().id(), approvalTemporaryChange.getSpNo().no());
        String scheduleName;
        if (approvalTemporaryChange.getStatus().equals(ApprovalStatus.PASSED)) {
            scheduleName = "早班";
        } else if (approvalTemporaryChange.getStatus().equals(ApprovalStatus.PASS_UNDONE)) {
            scheduleName = "晚班";
        } else {
            return;
        }
        // 设置班次转换选项
        ScheduleShiftOption scheduleShiftOption = ScheduleShiftOption.builder().cpId(approvalTemporaryChange.getCpId()).userId(approvalTemporaryChange.getUserId()).spNo(approvalTemporaryChange.getSpNo())
                .startTime(approvalTemporaryChange.getStartTime()).endTime(approvalTemporaryChange.getEndTime()).spName("临时变更").status(approvalTemporaryChange.getStatus())
                .scheduleName(scheduleName).build();
        // 找到排班规则
        scheduleShiftOption = findCheckinOption(scheduleShiftOption);
        if (scheduleShiftOption.getCheckinOptionSchedule() == null) {
            log.error("Can not find day shift checkin option by name {}", scheduleName);
            return;
        }
        // 设置班次
        setCheckinSchedule(scheduleShiftOption);
    }

    public ScheduleShiftOption findCheckinOption(ScheduleShiftOption scheduleShiftOption) {
        // 找到排班规则
        List<WxCpCropCheckinOption> cropCheckinOption = externalWxOaService.listCropCheckinOption(scheduleShiftOption.getCpId());
        WxCpCropCheckinOption shiftOption = null;
        WxCpCropCheckinOption.Schedule shiftSchedule = null;
        for (WxCpCropCheckinOption checkinOption : cropCheckinOption) {
            List<WxCpCropCheckinOption.Schedule> scheduleList = checkinOption.getSchedulelist();
            for (WxCpCropCheckinOption.Schedule schedule : scheduleList) {
                if (scheduleShiftOption.getScheduleName().equals(schedule.getScheduleName())) {
                    shiftOption = checkinOption;
                    shiftSchedule = schedule;
                    break;
                }
            }
        }
        scheduleShiftOption.setCheckinOptionSchedule(shiftSchedule);
        scheduleShiftOption.setCheckinOption(shiftOption);
        return scheduleShiftOption;
    }

    public void setCheckinSchedule(ScheduleShiftOption scheduleShiftOption) {
        // 拿到夜班申请时段的班次信息
        List<WxCpCheckinSchedule> scheduleList = externalWxOaService.listUserSchedules(scheduleShiftOption.getCpId(), scheduleShiftOption.getStartTime(), scheduleShiftOption.getEndTime(), Lists.list(scheduleShiftOption.getUserId().id()));
        // 夜班涉及日期的0点时间戳
        List<Long> beginTimeStampOfDays = attendanceHelper.getBeginTimeStampOfDays(scheduleShiftOption.getStartTime(), scheduleShiftOption.getEndTime());
        // 拿到年月和日，并根据年月分组
        List<ScheduleDay> scheduleDays = getScheduleDays(beginTimeStampOfDays);
        Map<String, List<ScheduleDay>> scheduleMap = scheduleDays.stream().collect(Collectors.groupingBy(ScheduleDay::getYearMonth));
        // 设置班次请求对象
        WxCpCropCheckinOption finalShiftOption = scheduleShiftOption.getCheckinOption();
        WxCpCropCheckinOption.Schedule finalShiftSchedule = scheduleShiftOption.getCheckinOptionSchedule();
        scheduleMap.forEach((yearMonth, days) -> {
            WxCpSetCheckinSchedule checkinSchedule = new WxCpSetCheckinSchedule();
            checkinSchedule.setGroupId(finalShiftOption.getGroupId().intValue());
            checkinSchedule.setYearmonth(Integer.valueOf(yearMonth));
            List<WxCpSetCheckinSchedule.Item> items = new ArrayList<>();
            checkinSchedule.setItems(items);
            for (ScheduleDay day : days) {
                // 若是工作日就把班次改为晚班
                if (isWorkDay(yearMonth, day.getDay(), scheduleList)) {
                    WxCpSetCheckinSchedule.Item item = new WxCpSetCheckinSchedule.Item();
                    item.setUserid(scheduleShiftOption.getUserId().id());
                    item.setDay(Integer.valueOf(day.getDay()));
                    item.setScheduleId(finalShiftSchedule.getScheduleId());
                    items.add(item);
                } else {
                    log.info("No work on date: {}{}", yearMonth, day.getDay());
                }
            }
            try {
                if (CollUtil.isNotEmpty(items)) {
                    externalWxOaService.setUserSchedules(scheduleShiftOption.getCpId(), checkinSchedule);
                    log.info("Set schedule success with data: {}", checkinSchedule);
                }
            } catch (ServiceException e) {
                log.warn("Set schedule error with data: {}", checkinSchedule);
                log.info("Notice HR deal with shift approval");
                WxCpMessage msg = new WxCpMessage();
                msg.setToUser("LiangDongQin");
                msg.setMsgType("text");
                msg.setAgentId(wxConfig.getAgentId(WxConfig.ATTENDANCE));
                String startTimeStr = DateUtil.formatDate(scheduleShiftOption.getStartTime());
                String endTimeStr = DateUtil.formatDate(scheduleShiftOption.getEndTime());
                msg.setContent(StrUtil.format("{} 申请的{}审批需要人工处理，审批单号：{}，开始日期：{}，结束日期：{}", scheduleShiftOption.getUserId().id(), scheduleShiftOption.getSpName(), scheduleShiftOption.getSpNo().no(), startTimeStr, endTimeStr));

                externalWxMsgService.sendMsg(scheduleShiftOption.getCpId(), msg);

            }

        });
    }

    public boolean isWorkDay(String yearMonth, String day, List<WxCpCheckinSchedule> scheduleList) {
        for (WxCpCheckinSchedule checkinSchedule : scheduleList) {
            for (WxCpCheckinSchedule.UserSchedule.Schedule schedule : checkinSchedule.getSchedule().getScheduleList()) {
                if (Integer.valueOf(yearMonth).equals(checkinSchedule.getYearmonth()) && Integer.valueOf(day).equals(schedule.getDay())) {
                    return schedule.getScheduleInfo().getScheduleId() != 0;
                }
            }
        }
        return false;
    }

    private List<ScheduleDay> getScheduleDays(List<Long> beginOfDays) {
        List<ScheduleDay> scheduleDays = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        for (Long beginOfDay : beginOfDays) {
            c.setTime(new Date(beginOfDay * 1000));
            int month = c.get(Calendar.MONTH) + 1;
            String yearMonth = String.valueOf(c.get(Calendar.YEAR)) + (month < 10 ? "0" + month : month);
            String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
            ScheduleDay scheduleDay = new ScheduleDay();
            scheduleDay.setYearMonth(yearMonth);
            scheduleDay.setDay(day);
            scheduleDays.add(scheduleDay);
        }
        return scheduleDays;
    }


    @Data
    public static class ScheduleDay {
        private String yearMonth;

        private String day;
    }

    /**
     * 将企业微信的性别转为系统性别
     *
     * @param gender 企业微信性别编码
     * @return 系统性别编码
     */
    public static String getSysGenderFromWx(Gender gender) {
        if (gender == null) {
            return "2";
        }
        if ("1".equals(gender.getCode())) {
            return "0";
        } else if ("2".equals(gender.getCode())) {
            return "1";
        } else {
            return "2";
        }
    }

    public static String getSysGenderFromWx(String gender) {
        if ("1".equals(gender)) {
            return "0";
        } else if ("2".equals(gender)) {
            return "1";
        } else {
            return "2";
        }
    }

    /**
     * 将企业微信的性别转为系统性别
     *
     * @param genderStr 企业微信性别中文
     * @return 系统性别编码
     */
    public static String getSysGenderFromWxStr(String genderStr) {
        if ("男".equals(genderStr)) {
            return "0";
        } else if ("女".equals(genderStr)) {
            return "1";
        } else {
            return "2";
        }
    }

}
