package com.greenstone.mes.job.task;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteOaService;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.oa.request.OaSyncApprovalCmd;
import com.greenstone.mes.oa.request.SyncCheckinDataCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * OA同步任务
 */
@Slf4j
@Component("oaSyncTask")
public class OaSyncTask {

    @Autowired
    private RemoteOaService remoteOaService;

    /**
     * 同步用户
     */
    public void syncWxDept() {
        log.info("sync wx dept start");
        remoteOaService.syncWxDept();
    }

    /**
     * 同步部门
     */
    public void syncWxUser() {
        log.info("sync wx user start");
        remoteOaService.syncWxUser();
    }

    /**
     * 同步企业微信审批数据
     */
    public void syncWxApproval(String startTime, String endTime, String templateName, String cpId) {
        log.info("执行同步审批数据方法");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date startDate = StrUtil.isEmpty(startTime) ? null : simpleDateFormat.parse(startTime);
            Date endDate = StrUtil.isEmpty(endTime) ? null : simpleDateFormat.parse(endTime);
            remoteOaService.syncApproval(OaSyncApprovalCmd.builder().startDate(startDate).endDate(endDate).templateName(templateName).cpId(cpId).build());
        } catch (ParseException e) {
            log.error("时间转换错误", e);
            throw new ServiceException("时间转换时发生错误");
        }
    }

    public void syncWxApprovalAuditing(String startTime, String endTime, String cpId) {
        log.info("执行同步审批数据方法-待审批的审批");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date startDate = StrUtil.isEmpty(startTime) ? null : simpleDateFormat.parse(startTime);
            Date endDate = StrUtil.isEmpty(endTime) ? null : simpleDateFormat.parse(endTime);
            remoteOaService.syncApprovalOfAuditing(OaSyncApprovalCmd.builder().startDate(startDate).endDate(endDate).cpId(cpId).build());
        } catch (ParseException e) {
            log.error("时间转换错误", e);
            throw new ServiceException("时间转换时发生错误");
        }
    }

    /**
     * 同步企业微信人员排班信息
     */
    public void syncWxSchedule(String startTime, String endTime) {
        log.info("执行同步企业微信人员排班方法");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = StrUtil.isEmpty(startTime) ? null : simpleDateFormat.parse(startTime);
            Date endDate = StrUtil.isEmpty(endTime) ? null : simpleDateFormat.parse(endTime);
            remoteOaService.syncSchedule(OaSyncApprovalCmd.builder().startDate(startDate).endDate(endDate).build());
        } catch (ParseException e) {
            log.error("时间转换错误", e);
            throw new ServiceException("时间转换时发生错误");
        }
    }

    /**
     * 昨日考勤异常提醒
     */
    public void attendanceRemindYesterday() {
        log.info("yesterday attendance abnormal remind");
        remoteOaService.remindYesterday();
    }

    /**
     * 同步企业微信打卡数据
     */
    public void syncWxCheckinData(String startTime, String endTime, String wxUserId, String cpId) {
        log.info("执行同步企业微信打卡数据方法");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date startDate = StrUtil.isEmpty(startTime) ? null : simpleDateFormat.parse(startTime);
            Date endDate = StrUtil.isEmpty(endTime) ? null : simpleDateFormat.parse(endTime);
            remoteOaService.syncCheckinData(SyncCheckinDataCmd.builder().startDate(startDate).endDate(endDate).wxUserId(wxUserId).cpId(cpId).build());
        } catch (ParseException e) {
            log.error("时间转换错误", e);
            throw new ServiceException("时间转换时发生错误");
        }
    }

}
