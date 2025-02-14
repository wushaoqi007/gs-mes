package com.greenstone.mes.wxcp.domain.helper.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.wxcp.domain.helper.WxOaService;
import com.greenstone.mes.wxcp.domain.helper.WxcpService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.SpNo;
import com.greenstone.mes.wxcp.infrastructure.config.WxCpProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpOaService;
import me.chanjar.weixin.cp.bean.oa.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 企业微信业务处理包装类
 *
 * @author gu_renkai
 * @date 2022/8/2 11:26
 */

@RequiredArgsConstructor
@Slf4j
@Service
public class WxcpOaServiceImpl implements WxOaService {

    private final WxcpService wxcpService;

    /**
     * 获取审批单详情
     *
     * @param cpId 企业ID
     * @param spNo 审批单编号
     */
    @Override
    public WxCpApprovalDetailResult getApprovalDetail(CpId cpId, SpNo spNo) {
        WxCpOaService oaService = wxcpService.getOaSpService(cpId);
        try {
            return oaService.getApprovalDetail(spNo.no());
        } catch (WxErrorException e) {
            log.error("WxError: get approval detail failed: {}", spNo.no(), e);
            throw new ServiceException("获取审批单详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取审批单列表
     *
     * @param cpId      企业ID
     * @param startTime 从此时间
     * @param endTime   到此时间
     * @param template  审批模板
     */
    @Override
    public List<String> listApprovalNo(CpId cpId, Date startTime, Date endTime, WxCpProperties.SpTemplate template) {
        if (template == null) {
            log.error("Error get approval No list: no template");
            throw new ServiceException(StrUtil.format("无法获取审批单编号，缺少审批模板信息"));
        }
        WxCpOaService oaService = wxcpService.getOaSpService(cpId);
        WxCpApprovalInfoQueryFilter filter = new WxCpApprovalInfoQueryFilter();
        filter.setKey(WxCpApprovalInfoQueryFilter.KEY.TEMPLATE_ID);
        filter.setValue(template.getTemplateId());
        List<WxCpApprovalInfoQueryFilter> filters = Lists.newArrayList(filter);
        return listApprovalNo(oaService, startTime, endTime, filters);
    }

    private List<String> listApprovalNo(WxCpOaService oaService, Date startTime, Date endTime, List<WxCpApprovalInfoQueryFilter> filters) {
        int size = 100;
        Integer cursor = 0;
        List<String> spNoList = new ArrayList<>();
        try {
            while (cursor != null) {
                WxCpApprovalInfo approvalInfo = oaService.getApprovalInfo(startTime, endTime, cursor, size, filters);
                cursor = approvalInfo.getNextCursor();
                spNoList.addAll(approvalInfo.getSpNoList());
            }

        } catch (WxErrorException e) {
            log.error("Error get approval list", e);
            throw new ServiceException("获取审批单列表失败: " + e.getMessage());
        }
        return spNoList;
    }

    @Override
    public List<WxCpCheckinSchedule> listUserSchedules(CpId cpId, Date startTime, Date endTime, List<String> userList) {
        WxCpOaService oaService = wxcpService.getOaService(cpId);
        // 单次最多获取100个用户的数据
        List<List<String>> userLists = CollectionUtil.split(userList, 100);
        // 单次最多获取30天的数据
        List<Date[]> dates = new ArrayList<>();
        long millisOf30Days = TimeUnit.DAYS.toMillis(30);
        while (endTime.getTime() - startTime.getTime() > millisOf30Days) {
            Date tempDate = new Date(startTime.getTime() + millisOf30Days);
            dates.add(new Date[]{startTime, tempDate});
            startTime = tempDate;
        }
        dates.add(new Date[]{startTime, endTime});
        List<WxCpCheckinSchedule> allCheckinScheduleList = new ArrayList<>();
        for (List<String> users : userLists) {
            for (Date[] date : dates) {
                try {
                    List<WxCpCheckinSchedule> checkinScheduleList = oaService.getCheckinScheduleList(date[0], date[1], users);
                    allCheckinScheduleList.addAll(checkinScheduleList);
                } catch (WxErrorException e) {
                    log.error("Error get checkin data: ", e);
                    throw new ServiceException("获取班次信息失败: " + e.getMessage());
                }
            }
        }
        return allCheckinScheduleList;
    }

    @Override
    public void setUserSchedules(CpId cpId, WxCpSetCheckinSchedule checkinSchedule) {
        WxCpOaService oaService = wxcpService.getOaService(cpId);
        try {
            oaService.setCheckinScheduleList(checkinSchedule);
        } catch (WxErrorException e) {
            log.error("WxError: set user schedules failed: ", e);
        }
    }

    /**
     * 获取企业微信打卡数据
     *
     * @param cpId      企业ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param userList  用户ID列表
     */
    @Override
    public List<WxCpCheckinData> listCheckinData(@Validated CpId cpId, @NotNull Date startTime,
                                                 @NotNull Date endTime, List<String> userList) {
        if (CollUtil.isEmpty(userList)) {
            return Collections.emptyList();
        }
        WxCpOaService oaService = wxcpService.getOaService(cpId);
        // 企业微信限制：单次最多获取100个用户的打卡数据
        List<List<String>> userLists = CollectionUtil.split(userList, 100);
        // 企业微信限制：单词最多获取30天的打卡数据
        List<Date[]> dates = new ArrayList<>();
        long millisOf30Days = TimeUnit.DAYS.toMillis(30);
        while (endTime.getTime() - startTime.getTime() > millisOf30Days) {
            Date tempTime = new Date(startTime.getTime() + millisOf30Days);
            dates.add(new Date[]{startTime, tempTime});
            startTime = tempTime;
        }
        dates.add(new Date[]{startTime, endTime});

        List<WxCpCheckinData> checkinDataList = new ArrayList<>();
        for (List<String> users : userLists) {
            for (Date[] date : dates) {
                try {
                    List<WxCpCheckinData> checkinData = oaService.getCheckinData(3, date[0], date[1], users);
                    checkinDataList.addAll(checkinData);
                } catch (WxErrorException e) {
                    log.error("get checkin data error: ", e);
                    throw new ServiceException("获取打卡信息时发生错误，请稍后再试。");
                }
            }
        }
        return checkinDataList;
    }

    @Override
    public List<WxCpCheckinSchedule> listSchedule(@Validated CpId cpId, @NotNull Date startTime,
                                                  @NotNull Date endTime, List<String> userList) {
        WxCpOaService oaService = wxcpService.getOaService(cpId);
        // 企业微信限制：单次最多获取100个用户的班次数据
        List<List<String>> userLists = CollectionUtil.split(userList, 100);
        // 企业微信限制：单词最多获取30天的班次
        List<Date[]> dates = new ArrayList<>();
        long millisOf30Days = TimeUnit.DAYS.toMillis(30);
        while (endTime.getTime() - startTime.getTime() > millisOf30Days) {
            Date tempDate = new Date(startTime.getTime() + millisOf30Days);
            dates.add(new Date[]{startTime, tempDate});
            startTime = tempDate;
        }
        dates.add(new Date[]{startTime, endTime});

        List<WxCpCheckinSchedule> allCheckinScheduleList = new ArrayList<>();
        for (List<String> users : userLists) {
            for (Date[] date : dates) {
                try {
                    List<WxCpCheckinSchedule> checkinScheduleList = oaService.getCheckinScheduleList(date[0], date[1], users);
                    allCheckinScheduleList.addAll(checkinScheduleList);
                } catch (WxErrorException e) {
                    log.error("get checkin data error: ", e);
                    throw new ServiceException("获取打卡信息时发生错误，请稍后再试。");
                }
            }
        }
        return allCheckinScheduleList;
    }

    /**
     * 获取全部打卡规则
     *
     * @return 全部打卡规则
     */
    @Override
    public List<WxCpCropCheckinOption> listCropCheckinOption(CpId cpId) {
        try {
            return wxcpService.getOaService(cpId).getCropCheckinOption();
        } catch (WxErrorException e) {
            log.error("WxError: get checkin option failed: ", e);
            throw new RuntimeException("获取打卡规则失败，请稍后再试。");
        }
    }

}
