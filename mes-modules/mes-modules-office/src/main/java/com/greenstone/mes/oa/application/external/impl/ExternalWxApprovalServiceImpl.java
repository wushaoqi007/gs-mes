package com.greenstone.mes.oa.application.external.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalCheckTakeCommitCmd;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalCheckedTakeCommitCmd;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalFinishedCommitCmd;
import com.greenstone.mes.oa.domain.external.ExternalWxApprovalService;
import com.greenstone.mes.oa.infrastructure.constant.AttendanceParam;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.wxcp.domain.helper.WxcpService;
import com.greenstone.mes.wxcp.infrastructure.config.WxConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpOaService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.oa.SummaryInfo;
import me.chanjar.weixin.cp.bean.oa.WxCpOaApplyEventRequest;
import me.chanjar.weixin.cp.bean.oa.applydata.ApplyDataContent;
import me.chanjar.weixin.cp.bean.oa.applydata.ContentTitle;
import me.chanjar.weixin.cp.bean.oa.applydata.ContentValue;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2024-07-08-16:01
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ExternalWxApprovalServiceImpl implements ExternalWxApprovalService {

    private final WxcpService wxcpService;
    private final RemoteUserService userService;
    private final WxConfig wxConfig;

    @Override
    public String commitCheckTakeApproval(WxApprovalCheckTakeCommitCmd command) {
        // 默认使用格林司通自动化企业微信发送
        if (StrUtil.isEmpty(command.getCpId())) {
            command.setCpId(AttendanceParam.ENABLE_QYWX_CROPID);
        }
        // 默认使用格林司通系统自建应用发送
        if (Objects.isNull(command.getAppId())) {
            command.setAppId(wxConfig.getAgentId(AttendanceParam.ENABLE_QYWX_CROPID, WxConfig.SYSTEM));
        }

        SysUser takeByUser = userService.getUser(SysUser.builder().userId(command.getTakeById()).build());
        if (Objects.isNull(takeByUser)) {
            log.error("发送质检取件审批失败，取件人未找到 user id:{}", command.getTakeById());
            throw new ServiceException(StrUtil.format("发送质检取件审批失败，取件人未找到 user id:{}", command.getTakeById()));
        }
        SysUser sponsorUser = userService.getUser(SysUser.builder().userId(command.getSponsorId()).build());
        if (Objects.isNull(sponsorUser)) {
            log.error("发送质检取件审批失败，经手人未找到 user id:{}", command.getSponsorId());
            throw new ServiceException(StrUtil.format("发送质检取件审批失败，经手人未找到 user id:{}", command.getSponsorId()));
        }

        WxCpService wxCpService = wxcpService.getWxCpService(command.getCpId(), command.getAppId());
        WxCpOaService oaService = wxCpService.getOaService();
        WxCpOaApplyEventRequest request = new WxCpOaApplyEventRequest();
        request.setCreatorUserId(sponsorUser.getWxUserId());
        request.setTemplateId(AttendanceParam.MACHINE_CHECK_TAKE_SIGN_TAMP_ID);
        request.setUseTemplateApprover(1);
        WxCpOaApplyEventRequest.ApplyData applyData = new WxCpOaApplyEventRequest.ApplyData();
        List<ApplyDataContent> contents = new ArrayList<>();
        contents.add(setApplyDataContentOfSingleContact("Contact-1719818594453", "经手人", command.getSponsor(), sponsorUser.getWxUserId()));
        contents.add(setApplyDataContentOfSingleContact("Contact-1719818312073", "取件人", command.getTakeBy(), takeByUser.getWxUserId()));
        contents.add(setApplyDataContentOfDate("Date-1719818517457", "日期", "hour", String.valueOf(DateUtil.parse(command.getTakeTime()).getTime() / 1000)));
        contents.add(setApplyDataContentOfText("Text-1719818323760", "关联取件单", command.getSerialNo()));
        applyData.setContents(contents);
        request.setApplyData(applyData);
        List<SummaryInfo> summaryInfos = new ArrayList<>();
        summaryInfos.add(setSummaryInfo("经手人：" + command.getSponsor()));
        summaryInfos.add(setSummaryInfo("取件人：" + command.getTakeBy()));
        summaryInfos.add(setSummaryInfo("日期：" + command.getTakeTime()));
        request.setSummaryList(summaryInfos);

        try {
            log.info("提交企业微信审批：{}", request);
            String spNo = oaService.apply(request);
            log.info("完成企业微信审批提交：{}", spNo);
            return spNo;
        } catch (Exception e) {
            log.error("发送质检取件审批失败：{}", e.getMessage());
            throw new ServiceException(StrUtil.format("发送质检取件审批失败：{}", e.getMessage()));
        }
    }

    @Override
    public String commitCheckedTakeApproval(WxApprovalCheckedTakeCommitCmd command) {
        // 默认使用格林司通自动化企业微信发送
        if (StrUtil.isEmpty(command.getCpId())) {
            command.setCpId(AttendanceParam.ENABLE_QYWX_CROPID);
        }
        // 默认使用格林司通系统自建应用发送
        if (Objects.isNull(command.getAppId())) {
            command.setAppId(wxConfig.getAgentId(AttendanceParam.ENABLE_QYWX_CROPID, WxConfig.SYSTEM));
        }

        SysUser takeByUser = userService.getUser(SysUser.builder().userId(command.getTakeById()).build());
        if (Objects.isNull(takeByUser)) {
            log.error("发送合格品取件审批失败，取件人未找到 user id:{}", command.getTakeById());
            throw new ServiceException(StrUtil.format("发送合格品取件审批失败，取件人未找到 user id:{}", command.getTakeById()));
        }
        SysUser sponsorUser = userService.getUser(SysUser.builder().userId(command.getSponsorId()).build());
        if (Objects.isNull(sponsorUser)) {
            log.error("发送合格品取件审批失败，经手人未找到 user id:{}", command.getSponsorId());
            throw new ServiceException(StrUtil.format("发送合格品取件审批失败，经手人未找到 user id:{}", command.getSponsorId()));
        }

        WxCpService wxCpService = wxcpService.getWxCpService(command.getCpId(), command.getAppId());
        WxCpOaService oaService = wxCpService.getOaService();
        WxCpOaApplyEventRequest request = new WxCpOaApplyEventRequest();
        request.setCreatorUserId(sponsorUser.getWxUserId());
        request.setTemplateId(AttendanceParam.MACHINE_CHECKED_TAKE_SIGN_TAMP_ID);
        request.setUseTemplateApprover(1);
        WxCpOaApplyEventRequest.ApplyData applyData = new WxCpOaApplyEventRequest.ApplyData();
        List<ApplyDataContent> contents = new ArrayList<>();
        contents.add(setApplyDataContentOfSingleContact("Contact-1719818594453", "经手人", command.getSponsor(), sponsorUser.getWxUserId()));
        contents.add(setApplyDataContentOfSingleContact("Contact-1719818312073", "取件人", command.getTakeBy(), takeByUser.getWxUserId()));
        contents.add(setApplyDataContentOfDate("Date-1719818517457", "日期", "hour", String.valueOf(DateUtil.parse(command.getTakeTime()).getTime() / 1000)));
        contents.add(setApplyDataContentOfText("Text-1719818323760", "关联取件单", command.getSerialNo()));
        applyData.setContents(contents);
        request.setApplyData(applyData);
        List<SummaryInfo> summaryInfos = new ArrayList<>();
        summaryInfos.add(setSummaryInfo("经手人：" + command.getSponsor()));
        summaryInfos.add(setSummaryInfo("取件人：" + command.getTakeBy()));
        summaryInfos.add(setSummaryInfo("日期：" + command.getTakeTime()));
        request.setSummaryList(summaryInfos);

        try {
            log.info("提交企业微信审批：{}", request);
            String spNo = oaService.apply(request);
            log.info("完成企业微信审批提交：{}", spNo);
            return spNo;
        } catch (Exception e) {
            log.error("发送合格品取件审批失败：{}", e.getMessage());
            throw new ServiceException(StrUtil.format("发送合格品取件审批失败：{}", e.getMessage()));
        }
    }

    @Override
    public String commitFinishedApproval(WxApprovalFinishedCommitCmd command) {
        // 默认使用格林司通自动化企业微信发送
        if (StrUtil.isEmpty(command.getCpId())) {
            command.setCpId(AttendanceParam.ENABLE_QYWX_CROPID);
        }
        // 默认使用格林司通系统自建应用发送
        if (Objects.isNull(command.getAppId())) {
            command.setAppId(wxConfig.getAgentId(AttendanceParam.ENABLE_QYWX_CROPID, WxConfig.SYSTEM));
        }

        SysUser takeByUser = userService.getUser(SysUser.builder().userId(command.getTakeById()).build());
        if (Objects.isNull(takeByUser)) {
            log.error("发送出库审批失败，领用人未找到 user id:{}", command.getTakeById());
            throw new ServiceException(StrUtil.format("发送出库审批失败，领用人未找到 user id:{}", command.getTakeById()));
        }
        SysUser sponsorUser = userService.getUser(SysUser.builder().userId(command.getSponsorId()).build());
        if (Objects.isNull(sponsorUser)) {
            log.error("发送出库审批失败，经手人未找到 user id:{}", command.getSponsorId());
            throw new ServiceException(StrUtil.format("发送出库审批失败，经手人未找到 user id:{}", command.getSponsorId()));
        }

        WxCpService wxCpService = wxcpService.getWxCpService(command.getCpId(), command.getAppId());
        WxCpOaService oaService = wxCpService.getOaService();
        WxCpOaApplyEventRequest request = new WxCpOaApplyEventRequest();
        request.setCreatorUserId(sponsorUser.getWxUserId());
        request.setTemplateId(AttendanceParam.MACHINE_FINISHED_SIGN_TAMP_ID);
        request.setUseTemplateApprover(1);
        WxCpOaApplyEventRequest.ApplyData applyData = new WxCpOaApplyEventRequest.ApplyData();
        List<ApplyDataContent> contents = new ArrayList<>();
        contents.add(setApplyDataContentOfSingleContact("Contact-1719818594453", "经手人", command.getSponsor(), sponsorUser.getWxUserId()));
        contents.add(setApplyDataContentOfSingleContact("Contact-1719818312073", "领用人", command.getTakeBy(), takeByUser.getWxUserId()));
        contents.add(setApplyDataContentOfDate("Date-1719818517457", "日期", "hour", String.valueOf(DateUtil.parse(command.getTakeTime()).getTime() / 1000)));
        contents.add(setApplyDataContentOfText("Text-1719818323760", "关联出库单", command.getSerialNo()));
        applyData.setContents(contents);
        request.setApplyData(applyData);
        List<SummaryInfo> summaryInfos = new ArrayList<>();
        summaryInfos.add(setSummaryInfo("经手人：" + command.getSponsor()));
        summaryInfos.add(setSummaryInfo("领用人：" + command.getTakeBy()));
        summaryInfos.add(setSummaryInfo("日期：" + command.getTakeTime()));
        request.setSummaryList(summaryInfos);

        try {
            log.info("提交企业微信审批：{}", request);
            String spNo = oaService.apply(request);
            log.info("完成企业微信审批提交：{}", spNo);
            return spNo;
        } catch (Exception e) {
            log.error("发送出库审批失败：{}", e.getMessage());
            throw new ServiceException(StrUtil.format("发送出库审批失败：{}", e.getMessage()));
        }
    }

    /**
     * 设置摘要
     *
     * @param text 摘要内容
     */
    public SummaryInfo setSummaryInfo(String text) {
        List<SummaryInfo.SummaryInfoData> summaryInfoData = new ArrayList<>();
        SummaryInfo.SummaryInfoData data = new SummaryInfo.SummaryInfoData();
        data.setLang("zh_CN");
        data.setText(text);
        summaryInfoData.add(data);
        SummaryInfo summaryInfo = new SummaryInfo();
        summaryInfo.setSummaryInfoData(summaryInfoData);
        return summaryInfo;
    }

    /**
     * 组装单选成员控件内容
     *
     * @param controlId 控件id
     * @param title     标题
     * @param name      成员姓名
     * @param userId    成员id
     */
    public ApplyDataContent setApplyDataContentOfSingleContact(String controlId, String title, String name, String userId) {
        ApplyDataContent content1 = new ApplyDataContent();
        content1.setControl("Contact");
        content1.setId(controlId);
        content1.setTitles(setTitles(title));
        content1.setValue(setSingleContactValue(name, userId));
        return content1;
    }

    /**
     * 组装日期控件内容
     *
     * @param controlId   控件id
     * @param title       标题
     * @param type        日期类型
     * @param s_timestamp 时间戳
     */
    public ApplyDataContent setApplyDataContentOfDate(String controlId, String title, String type, String s_timestamp) {
        ApplyDataContent content1 = new ApplyDataContent();
        content1.setControl("Date");
        content1.setId(controlId);
        content1.setTitles(setTitles(title));
        content1.setValue(setDateValue(type, s_timestamp));
        return content1;
    }

    /**
     * 组装文本控件
     *
     * @param controlId 控件id
     * @param title     标题
     * @param text      文本
     */
    public ApplyDataContent setApplyDataContentOfText(String controlId, String title, String text) {
        ApplyDataContent content1 = new ApplyDataContent();
        content1.setControl("Text");
        content1.setId(controlId);
        content1.setTitles(setTitles(title));
        content1.setValue(setTextValue(text));
        return content1;
    }

    /**
     * 设置标题
     *
     * @param text 标题
     */
    public List<ContentTitle> setTitles(String text) {
        List<ContentTitle> titles = new ArrayList<>();
        ContentTitle title1 = new ContentTitle();
        title1.setText(text);
        title1.setLang("zh_CN");
        return titles;
    }

    /**
     * 设置单选成员
     *
     * @param name   姓名
     * @param userId 微信id
     */
    public ContentValue setSingleContactValue(String name, String userId) {
        List<ContentValue.Member> members = new ArrayList<>();
        ContentValue.Member m1 = new ContentValue.Member();
        m1.setName(name);
        m1.setUserId(userId);
        members.add(m1);
        ContentValue value = new ContentValue();
        value.setMembers(members);
        return value;
    }

    /**
     * 设置日期
     *
     * @param type        日期类型
     * @param s_timestamp 时间戳
     */
    public ContentValue setDateValue(String type, String s_timestamp) {
        ContentValue.Date date = new ContentValue.Date();
        date.setType(type);
        date.setTimestamp(s_timestamp);
        ContentValue value = new ContentValue();
        value.setDate(date);
        return value;
    }

    /**
     * 设置文本
     *
     * @param text 文本内容
     */
    public ContentValue setTextValue(String text) {
        ContentValue value = new ContentValue();
        value.setText(text);
        return value;
    }
}
