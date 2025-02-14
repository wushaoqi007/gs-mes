package com.greenstone.mes.meal.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.exception.BusinessException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.meal.application.assembler.MealAssembler;
import com.greenstone.mes.meal.application.helper.MealHelper;
import com.greenstone.mes.meal.application.service.MealService;
import com.greenstone.mes.meal.domain.entity.MealReport;
import com.greenstone.mes.meal.domain.entity.MealTicket;
import com.greenstone.mes.meal.domain.repository.MealRepository;
import com.greenstone.mes.meal.infrastructure.constant.MealConst;
import com.greenstone.mes.meal.infrastructure.constant.MealParamConst;
import com.greenstone.mes.meal.infrastructure.persistence.MealManageDo;
import com.greenstone.mes.meal.interfaces.event.MealTicketUsedEvent;
import com.greenstone.mes.oa.application.dto.attendance.ApprovalOverTime;
import com.greenstone.mes.oa.application.wrapper.ApprovalWrapper;
import com.greenstone.mes.office.application.service.CustomParamService;
import com.greenstone.mes.office.infrastructure.constant.ParamKey;
import com.greenstone.mes.office.infrastructure.constant.ParamModule;
import com.greenstone.mes.office.meal.dto.cmd.*;
import com.greenstone.mes.office.meal.dto.query.MealManageQuery;
import com.greenstone.mes.office.meal.dto.query.MealReportQuery;
import com.greenstone.mes.office.meal.dto.query.TicketUseStatQuery;
import com.greenstone.mes.office.meal.dto.result.MealManageResult;
import com.greenstone.mes.office.meal.dto.result.MealReportResult;
import com.greenstone.mes.office.meal.dto.result.TicketUseResult;
import com.greenstone.mes.office.meal.dto.result.TicketUseStatResult;
import com.greenstone.mes.system.api.RemoteDeptService;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.domain.SysDept;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.dto.query.UserQuery;
import com.greenstone.mes.system.dto.result.UserResult;
import com.greenstone.mes.wxcp.domain.helper.WxMediaService;
import com.greenstone.mes.wxcp.domain.helper.WxMsgService;
import com.greenstone.mes.wxcp.domain.helper.WxOaService;
import com.greenstone.mes.wxcp.domain.helper.WxcpTagService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.infrastructure.config.WxConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.cp.bean.WxCpTag;
import me.chanjar.weixin.cp.bean.WxCpUser;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinData;
import me.chanjar.weixin.cp.bean.templatecard.CheckboxOption;
import me.chanjar.weixin.cp.bean.templatecard.MultipleSelect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class MealServiceImpl implements MealService {

    private final MealRepository mealRepository;
    private final MealAssembler mealAssembler;
    private final MealHelper mealHelper;

    private final CustomParamService customParamService;

    private final WxConfig wxConfig;

    private final RemoteUserService userService;
    private final RemoteDeptService deptService;

    private final WxMediaService wxMediaService;
    private final WxMsgService wxMsgService;
    private final WxOaService wxOaService;
    private final WxcpTagService wxTagService;
    private final ApprovalWrapper approvalWrapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<MealReportResult> queryReposts(MealReportQuery query) {
        List<MealReport> mealReports = mealRepository.queryMealReports(query);
        List<MealReportResult> mealReportResults = mealAssembler.reportEntities2results(mealReports);
        // 补充用餐时间
        for (MealReportResult mealReportResult : mealReportResults) {
            if (mealReportResult.getUsedNum() >= 1) {
                List<MealTicket> tickets = mealRepository.getTickets(mealReportResult.getId());
                if (CollUtil.isNotEmpty(tickets)) {
                    mealReportResult.setUseTime(tickets.get(0).getUseTime());
                }
            }
        }
        return mealReportResults;
    }

    @Override
    public List<MealManageResult> queryManages(MealManageQuery query) {
        List<MealManageDo> mealManageDos = mealRepository.queryMealManages(query);
        return mealAssembler.manageDos2results(mealManageDos);
    }

    @Transactional
    @Override
    public void selfReport(MealReportCmd mealReportCmd) {
        SysUser user = userService.getUser(SysUser.builder().wxUserId(mealReportCmd.getWxUserId()).mainWxcpId(mealReportCmd.getWxCpId()).build());
        if (user == null) {
            throw new BusinessException("系统账号不存在，创建账号请点击'账号-创建账号'。");
        }
        // 组装报餐信息
        MealReport mealReport = mealAssembler.addCmd2Entity(mealReportCmd);
        mealReport.setReportById(user.getUserId());
        mealReport.setReportBy(user.getNickName());
        mealReport.setDeptId(user.getDeptId());
        mealReport.setCreateBy(user.getNickName());
        mealReport.setCreateById(user.getUserId());
        mealReport.setCreateTime(LocalDateTime.now());

        // 检查能否报餐
        reportPreconditionCheck(mealReport, user);

        SysDept sysDept = deptService.getSysDept(SysDept.builder().deptId(user.getDeptId()).build());
        if (sysDept != null) {
            mealReport.setDeptName(sysDept.getDeptName());
        }
        // 生成餐券
        genTicket(mealReport, user);
        // 保存报餐信息
        mealRepository.addMealReport(mealReport);
        for (MealTicket mealTicket : mealReport.getMealTickets()) {
            sendMealTicketImg(new CpId(user.getMainWxcpId()), wxConfig.getAgentId(user.getMainWxcpId(), WxConfig.SYSTEM), user.getWxUserId(), mealTicket,
                    null);
        }
    }

    @Transactional
    @Override
    public void adminReport(AdminMealReportCmd reportCmd) {
        if (reportCmd.getDay().isBefore(LocalDate.now())) {
            throw new BusinessException("请选择当天或之后的日期。");
        }
        SysUser user = userService.getUser(SysUser.builder().userId(reportCmd.getUserId()).build());
        if (user == null) {
            throw new BusinessException("用户不存在。");
        }
        MealReportCmd mealReportCmd = MealReportCmd.builder().mealType(reportCmd.getMealType()).reportType(MealConst.ReportType.ADMIN_REPORT).mealNum(reportCmd.getMealNum()).haveMeal(true).day(reportCmd.getDay()).remark(reportCmd.getRemark()).wxCpId(user.getMainWxcpId()).wxUserId(user.getWxUserId()).build();
        // 组装报餐信息
        MealReport mealReport = mealAssembler.addCmd2Entity(mealReportCmd);
        mealReport.setReportById(user.getUserId());
        mealReport.setReportBy(user.getNickName());
        mealReport.setDeptId(user.getDeptId());

        SysDept sysDept = deptService.getSysDept(SysDept.builder().deptId(user.getDeptId()).build());
        if (sysDept != null) {
            mealReport.setDeptName(sysDept.getDeptName());
        }
        // 审计信息
        User loginUser = SecurityUtils.getLoginUser().getUser();
        mealReport.setCreateBy(loginUser.getNickName());
        mealReport.setCreateById(loginUser.getUserId());
        mealReport.setCreateTime(LocalDateTime.now());
        // 生成餐券
        genTicket(mealReport, user);
        // 保存报餐信息
        mealRepository.addMealReport(mealReport);


        String content = StrUtil.format("管理员已为您报餐：{} {} {} 份", DateUtil.today(), MealConst.MealType.LUNCH == mealReportCmd.getMealType() ? "午餐" :
                "晚餐", mealReport.getMealTickets().size());
        WxCpMessage msg = WxCpMessage.TEXT().content(content).toUser(user.getWxUserId()).build();
        wxMsgService.sendMsg(new CpId(user.getMainWxcpId()), wxConfig.getAgentId(user.getMainWxcpId(), WxConfig.SYSTEM), msg);

        for (MealTicket mealTicket : mealReport.getMealTickets()) {
            sendMealTicketImg(new CpId(user.getMainWxcpId()), wxConfig.getAgentId(user.getMainWxcpId(), WxConfig.SYSTEM), user.getWxUserId(), mealTicket, null);
        }
    }

    @Transactional
    @Override
    public void selfRevoke(MealRevokeCmd revokeCmd) {
        SysUser user = userService.getUser(SysUser.builder().wxUserId(revokeCmd.getWxUserId()).mainWxcpId(revokeCmd.getWxCpId()).build());
        MealReport selectEntity = MealReport.builder().mealType(revokeCmd.getMealType()).reportById(user.getUserId())
                .day(revokeCmd.getDay()).build();
        MealReport mealReport = mealRepository.getReport(selectEntity);
        if (mealReport == null) {
            throw new BusinessException("无法撤销，未找到报餐信息。");
        }
        selectEntity.setRevoked(false);
        MealReport existReport = mealRepository.getReport(selectEntity);
        if (existReport == null) {
            throw new BusinessException("无法撤销，未找到报餐信息。");
        }

        List<MealTicket> tickets = mealRepository.getTickets(existReport.getId());
        boolean isTicketUsed = tickets.stream().anyMatch(MealTicket::getUsed);
        if (isTicketUsed) {
            throw new BusinessException("无法撤销，餐券已使用。");
        }

        existReport.setReportType(MealConst.ReportType.SELF_REVOKE);
        mealRepository.revokeMealReport(existReport);

        String content = StrUtil.format("您 {} 的 {} 报餐已撤销。", DateUtil.today(), mealReport.getMealName());
        WxCpMessage msg = WxCpMessage.TEXT().content(content).toUser(user.getWxUserId()).build();
        wxMsgService.sendMsg(new CpId(user.getMainWxcpId()), wxConfig.getAgentId(user.getMainWxcpId(), WxConfig.SYSTEM), msg);
    }

    @Transactional
    @Override
    public void adminRevoke(MealRevokeCmd revokeCmd) {
        revokeCmd.setRevokeType(MealConst.ReportType.ADMIN_REVOKE);

        MealReport mealReport = mealRepository.getReport(MealReport.builder().id(revokeCmd.getReportId()).build());
        SysUser user = userService.getUser(SysUser.builder().userId(mealReport.getReportById()).build());
        if (mealReport.getId() == null) {
            throw new RuntimeException("请选择报餐记录。");
        }
        MealReport selectEntity = MealReport.builder().id(mealReport.getId()).build();
        MealReport existReport = mealRepository.getReport(selectEntity);
        if (existReport == null) {
            throw new BusinessException("无法撤销，未找到报餐信息。");
        }

        List<MealTicket> tickets = mealRepository.getTickets(existReport.getId());
        boolean isTicketUsed = tickets.stream().anyMatch(MealTicket::getUsed);
        if (isTicketUsed) {
            throw new BusinessException("无法撤销，餐券已使用。");
        }

        existReport.setReportType(MealConst.ReportType.ADMIN_REVOKE);
        mealRepository.revokeMealReport(existReport);

        String content = StrUtil.format("您 {} 的 {} 报餐已被管理员撤销，撤销数量 {}。", DateUtil.today(), mealReport.getMealName(), tickets.size());
        WxCpMessage msg = WxCpMessage.TEXT().content(content).toUser(user.getWxUserId()).build();
        wxMsgService.sendMsg(new CpId(user.getMainWxcpId()), wxConfig.getAgentId(user.getMainWxcpId(), WxConfig.SYSTEM), msg);
    }

    @Override
    public void sysRevoke(MealApplyCancelRevokeCmd revokeCmd) {
        SysUser user = userService.getUser(SysUser.builder().mainWxcpId(revokeCmd.getWxCpId()).wxUserId(revokeCmd.getWxUserId()).build());
        if (user == null) {
            log.debug("用户不存在。");
        }
        MealReport selectEntity = MealReport.builder().mealType(MealConst.MealType.DINNER)
                .day(revokeCmd.getDay())
                .reportById(user.getUserId()).revoked(false).build();
        MealReport existReport = mealRepository.getReport(selectEntity);
        if (existReport == null) {
            log.debug("无法撤销，未找到报餐信息。");
            return;
        }
        List<MealTicket> tickets = mealRepository.getTickets(existReport.getId());
        boolean isTicketUsed = tickets.stream().anyMatch(MealTicket::getUsed);
        if (isTicketUsed) {
            log.debug("无法撤销，餐券已使用。");
            return;
        }
        if (haveActiveOverworkApproval(revokeCmd.getDay(), user.getWxUserId(), user.getMainWxcpId())) {
            log.info("No need revoke meal report: an other overwork application exist.");
            return;
        }
        existReport.setReportType(MealConst.ReportType.SYS_REVOKE);
        mealRepository.revokeMealReport(existReport);

        String content = StrUtil.format("因加班申请已撤销，系统自动撤销 {} 的 {} 报餐。", DateUtil.today(), existReport.getMealName());
        WxCpMessage msg = WxCpMessage.TEXT().content(content).toUser(user.getWxUserId()).build();
        wxMsgService.sendMsg(new CpId(user.getMainWxcpId()), wxConfig.getAgentId(user.getMainWxcpId(), WxConfig.SYSTEM), msg);
    }

    /**
     * 生成餐券
     */
    private void genTicket(MealReport mealReport, SysUser reportUser) {
        for (int i = 0; i < mealReport.getMealNum(); i++) {
            String ticketCode = getUniqueTicketCode(reportUser.getUserId());
            MealTicket saveMealTicket = MealTicket.builder().mealType(mealReport.getMealType())
                    .ticketCode(ticketCode)
                    .day(mealReport.getDay())
                    .reportById(reportUser.getUserId())
                    .reportBy(reportUser.getNickName()).build();
            mealReport.addMealTicket(saveMealTicket);
        }
    }

    @Override
    public void sendMealTicket(CpId cpId, String wxUserId) {
        SysUser user = userService.getUser(SysUser.builder().wxUserId(wxUserId).mainWxcpId(cpId.id()).build());
        if (user == null) {
            throw new BusinessException("系统账号不存在，创建账号请点击'账号-创建账号'。");
        }
        Integer mealType = mealHelper.getMealTypeNow();
        List<MealTicket> tickets = mealRepository.getTickets(mealType, user.getUserId(), LocalDate.now());
        if (CollUtil.isEmpty(tickets)) {
            WxCpMessage textMsg = WxCpMessage.TEXT().content("您没有当前餐次的餐券，请先报餐。").toUser(wxUserId).build();
            wxMsgService.sendMsg(cpId, wxConfig.getAgentId(cpId.id(), WxConfig.SYSTEM), textMsg);
        } else {
            if (tickets.size() == 1) {
                sendMealTicketImg(cpId, wxConfig.getAgentId(cpId.id(), WxConfig.SYSTEM), wxUserId, tickets.get(0), null);
            } else {
                for (int i = 0; i < tickets.size(); i++) {
                    sendMealTicketImg(cpId, wxConfig.getAgentId(cpId.id(), WxConfig.SYSTEM), wxUserId, tickets.get(i), i);
                }
            }

        }

    }

    @Override
    public void sendMealTicketImg(CpId cpId, Integer appId, String wxUserId, MealTicket mealTicket, Integer num) {
        try {
            if (StrUtil.isBlank(mealTicket.getWxMediaId())) {
                // 生成餐券二维码
                String ticketRemark = mealHelper.getTicketRemark(mealTicket, num);
                BufferedImage bufferedImage = mealHelper.generateTicketQrCode(mealTicket.getTicketCode(), ticketRemark);
                // 上传餐券二维码到企业微信
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", outputStream);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                WxMediaUploadResult uploadResult = wxMediaService.upload(cpId, appId, WxConsts.MediaFileType.IMAGE, inputStream);
                mealTicket.setWxMediaId(uploadResult.getMediaId());
                mealRepository.updateTicketMediaId(mealTicket);
            }
            // 发送餐券二维码到企业微信消息
            WxCpMessage imageMsg = WxCpMessage.IMAGE().mediaId(mealTicket.getWxMediaId()).toUser(wxUserId).build();
            wxMsgService.sendMsg(cpId, appId, imageMsg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMealReportCard(Integer mealType, CpId wxCpId, String wxUserId) {
        SysUser user = userService.getUser(SysUser.builder().wxUserId(wxUserId).mainWxcpId(wxCpId.id()).build());
        if (user == null) {
            throw new BusinessException("系统账号不存在，创建账号请点击'账号-创建账号'。");
        }
        MealReport build = MealReport.builder().mealType(mealType).reportById(user.getUserId()).day(LocalDate.now()).revoked(false).build();
        boolean reportExist = mealRepository.isReportExist(build);
        if (reportExist) {
            String mealName = MealConst.MealType.LUNCH == mealType ? "午餐" : "晚餐";
            throw new BusinessException(mealName + "已报餐，查看餐券请点击'报餐-查看餐券'。");
        }
        MealManageDo mealManage = mealRepository.getMealManage(mealType, LocalDate.now());
        // 停止报餐后可补餐，补餐不做份数限制
        if (mealManage != null && mealManage.getStopped()) {
            additionalReportCard(mealType, wxCpId, wxUserId, user);
        } else {
            selfReportCard(mealType, wxCpId, wxUserId, user);
        }
    }

    /**
     * 发送年夜饭报名卡片
     *
     * @param mealType
     * @param wxCpId
     * @param wxUserId
     */
    @Override
    public void sendFrdReportCard(Integer mealType, CpId wxCpId, String wxUserId) {
        // 截止到24年12月31日
        if (LocalDate.now().getYear() > 2024 && LocalDate.now().getDayOfYear() > 9) {
            throw new BusinessException("年夜饭已停止报名。");
        }
        // 组装选择按钮 吃不吃
        MultipleSelect select1 = MultipleSelect.builder().question_key("eat").selected_id("y").title("年夜饭").build();
        CheckboxOption y = CheckboxOption.builder().id("y").text("吃").build();
        CheckboxOption n = CheckboxOption.builder().id("n").text("不吃").build();
        select1.setOptions(List.of(y, n));
        // 组装选择按钮 数量
        MultipleSelect select2 = MultipleSelect.builder().question_key("num").selected_id("1").title("数量").build();
        CheckboxOption n1 = CheckboxOption.builder().id("1").text("1份").build();
        select2.setOptions(List.of(n1));
        // 组装企业微信消息
        WxCpMessage mealRepostMsg = WxCpMessage.TEMPLATECARD()
                .toUser(wxUserId)
                .agentId(wxConfig.getAgentId(wxCpId.id(), WxConfig.SYSTEM))
                .cardType(WxConsts.TemplateCardType.MULTIPLE_INTERACTION)
                .sourceDesc("年夜饭")
                .mainTitleTitle("2024年夜饭报名")
                .mainTitleDesc("时间：1月11日晚，地点：车间二楼原食堂")
                .taskId(mealHelper.genTaskId(MealConst.MealType.DINNER, MealConst.ReportType.SELF_REPORT, LocalDate.of(2025, 1, 11)))
                .selects(List.of(select1, select2))
                .submitButtonKey("submit")
                .submitButtonText("提交").build();
        wxMsgService.sendMsg(wxCpId, wxConfig.getAgentId(wxCpId.id(), WxConfig.SYSTEM), mealRepostMsg);
    }

    public void selfReportCard(Integer mealType, CpId wxCpId, String wxUserId, SysUser user) {
        boolean stopped = mealRepository.isStopped(mealType, LocalDate.now());
        if (stopped) {
            String content = StrUtil.format("{} 的 {} 已停止报餐。", DateUtil.today(), MealConst.MealType.LUNCH == mealType ? "午餐" : "晚餐");
            throw new BusinessException(content);
        }
        String mealReportTitle = mealHelper.getMealReportTitle(mealType);
        String mealReportTitleDesc = mealHelper.getMealReportTitleDesc(mealType);
        String taskId = mealHelper.genTaskId(mealType, MealConst.ReportType.SELF_REPORT, LocalDate.now());
        String selectTitle1 = MealConst.MealType.LUNCH == mealType ? "午饭" : "晚饭";
        // 组装选择按钮 吃不吃
        MultipleSelect select1 = MultipleSelect.builder().question_key("eat").selected_id("y").title(selectTitle1).build();
        CheckboxOption y = CheckboxOption.builder().id("y").text("吃").build();
        CheckboxOption n = CheckboxOption.builder().id("n").text("不吃").build();
        select1.setOptions(List.of(y, n));
        // 组装选择按钮 数量
        MultipleSelect select2 = MultipleSelect.builder().question_key("num").selected_id("1").title("数量").build();
        CheckboxOption n1 = CheckboxOption.builder().id("1").text("1份").build();
        select2.setOptions(List.of(n1));
        // 组装企业微信消息
        WxCpMessage mealRepostMsg = WxCpMessage.TEMPLATECARD()
                .toUser(wxUserId)
                .agentId(wxConfig.getAgentId(wxCpId.id(), WxConfig.SYSTEM))
                .cardType(WxConsts.TemplateCardType.MULTIPLE_INTERACTION)
                .sourceDesc("报餐")
                .mainTitleTitle(mealReportTitle)
                .mainTitleDesc(mealReportTitleDesc)
                .taskId(taskId)
                .selects(List.of(select1, select2))
                .submitButtonKey("submit")
                .submitButtonText("提交").build();
        wxMsgService.sendMsg(wxCpId, wxConfig.getAgentId(wxCpId.id(), WxConfig.SYSTEM), mealRepostMsg);
    }

    public void additionalReportCard(Integer mealType, CpId wxCpId, String wxUserId, SysUser user) {
        boolean stopped = mealRepository.additionalReportIsStopped(mealType, LocalDate.now());
        if (stopped) {
            String content = StrUtil.format("{} 的 {} 已停止补餐。", DateUtil.today(), MealConst.MealType.LUNCH == mealType ? "午餐" : "晚餐");
            throw new BusinessException(content);
        }
        String mealReportTitle = mealHelper.getMealReportTitle(mealType) + "补报";
        String taskId = mealHelper.genTaskId(mealType, MealConst.ReportType.ADDITIONAL_REPORT, LocalDate.now());
        String selectTitle1 = MealConst.MealType.LUNCH == mealType ? "午饭" : "晚饭";
        // 组装选择按钮 吃
        MultipleSelect select1 = MultipleSelect.builder().question_key("eat").selected_id("y").title(selectTitle1).build();
        CheckboxOption y = CheckboxOption.builder().id("y").text("吃").build();
        select1.setOptions(List.of(y));
        // 组装选择按钮 数量
        MultipleSelect select2 = MultipleSelect.builder().question_key("num").selected_id("1").title("数量").build();
        CheckboxOption n1 = CheckboxOption.builder().id("1").text("1份").build();
        select2.setOptions(List.of(n1));
        // 组装企业微信消息
        WxCpMessage mealRepostMsg = WxCpMessage.TEMPLATECARD().toUser(wxUserId).agentId(wxConfig.getAgentId(wxCpId.id(), WxConfig.SYSTEM)).cardType(WxConsts.TemplateCardType.MULTIPLE_INTERACTION).sourceDesc("报餐").mainTitleTitle(mealReportTitle).taskId(taskId).selects(List.of(select1, select2)).submitButtonKey("submit").submitButtonText("提交").build();
        wxMsgService.sendMsg(wxCpId, wxConfig.getAgentId(wxCpId.id(), WxConfig.SYSTEM), mealRepostMsg);
    }


    private final LocalTime start2 = LocalTime.of(8, 0, 0);
    private final LocalTime end2 = LocalTime.of(12, 45, 0);
    private final LocalTime start3 = LocalTime.of(14, 0, 0);
    private final LocalTime end3 = LocalTime.of(18, 30, 0);

    @Override
    public synchronized TicketUseResult useTicket(String ticketCode) {
        LocalTime now = LocalTime.now();
        if (now.isBefore(start2) || (now.isAfter(end2) && now.isBefore(start3)) || now.isAfter(end3)) {
            return TicketUseResult.builder().msg("用餐时间未开始").build();
        }

        MealTicket ticket = mealRepository.getTicket(ticketCode);
        if (ticket == null) {
            return TicketUseResult.builder().msg("错误的餐券").build();
        }
        if (ticket.getUsed()) {
            return TicketUseResult.builder().msg("餐券不能重复使用").build();
        }
        boolean isLunchTime = LocalDateTime.now().getHour() < 15;
        if (isLunchTime && !(MealConst.MealType.LUNCH == ticket.getMealType())) {
            return TicketUseResult.builder().msg("请使用午餐券").build();
        }
        if (!isLunchTime && MealConst.MealType.LUNCH == ticket.getMealType()) {
            return TicketUseResult.builder().msg("请使用晚餐券").build();
        }
        if (ticket.getDay().isBefore(LocalDate.now())) {
            return TicketUseResult.builder().msg("餐券已过期").build();
        }
        // 2025-01-07 餐券只能当天使用
        if (ticket.getDay().getYear() != LocalDateTime.now().getYear() || ticket.getDay().getDayOfYear() != LocalDateTime.now().getDayOfYear()) {
            return TicketUseResult.builder().msg("餐券只能当天使用").build();
        }
        mealRepository.useTicket(ticket);

        eventPublisher.publishEvent(MealTicketUsedEvent.builder().ticket(ticket).build());

        return TicketUseResult.builder().success(true).build();
    }

    @Override
    public boolean stopReport(StopReportCmd stopReportCmd) {
        boolean stopSuccess = mealRepository.stopReport(stopReportCmd.getMealType(), stopReportCmd.getDay());
        sendStatAfterStopReport();
        return stopSuccess;
    }

    @Override
    public void sendReportStatData() {
        List<String> receiveUserIds = customParamService.fromJsonList(ParamModule.MEAL_REPORT, ParamKey.MealReport.STAT_DATA_RECEIVER); // 拿到设置的需要接收信息的人员
        if (CollUtil.isEmpty(receiveUserIds)) {
            log.info("未设置通知人员，忽略此次通知。");
            return;
        }
        Integer mealType = mealHelper.getMealTypeNow();
        LocalDate day = LocalDate.now();
        recalculate(ReCalcCmd.builder().mealType(mealType).day(day).build());
        MealManageDo mealManage = mealRepository.getMealManage(mealType, day);

        String msgContent = DateUtil.today() + " " + LocalDateTimeUtil.dayOfWeek(day).toChinese("周") + " " + mealHelper.getMealTypeName();
        if (mealManage == null) {
            msgContent += " 无人报餐。";
        } else {
            msgContent += " 报餐 " + mealManage.getMealNum() + " 份。";
        }
        // 发送报餐统计信息到企业微信
        List<Long> userIds = receiveUserIds.stream().map(Long::valueOf).toList();
        List<UserResult> users = userService.userWorkwxInfos(UserQuery.builder().userIds(userIds).build());
        Map<String, List<UserResult>> cpIdMap = users.stream().collect(Collectors.groupingBy(UserResult::getWxCpId));
        for (String cpId : cpIdMap.keySet()) {
            List<UserResult> cpUsers = cpIdMap.get(cpId);
            String receiveUserStr = cpUsers.stream().map(UserResult::getWxUserId).collect(Collectors.joining("|"));
            WxCpMessage msg = WxCpMessage.TEXT().toUser(receiveUserStr).content(msgContent).build();
            wxMsgService.sendMsg(new CpId(cpId), wxConfig.getAgentId(cpId, WxConfig.SYSTEM), msg);
            log.info("已发送通知：" + msgContent + " 给 " + receiveUserStr);
        }
    }

    @Override
    public List<TicketUseStatResult> queryTicketUseStat(TicketUseStatQuery useStatQuery) {
        if (!useStatQuery.getUseTimeStart().toLocalDate().equals(useStatQuery.getUseTimeEnd().toLocalDate())) {
            throw new ServiceException("只允许统计同一天内的时间！");
        }
        List<TicketUseStatResult> useStatResults = new ArrayList<>();
        List<MealTicket> ticketsByUseTime = mealRepository.getTicketsByUseTime(useStatQuery.getUseTimeStart(), useStatQuery.getUseTimeEnd());
        Map<String, Long> groupByUseTime = ticketsByUseTime.stream().collect(Collectors.groupingBy(t -> t.getUseTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                Collectors.counting()));
        groupByUseTime.forEach((useTime, number) -> useStatResults.add(TicketUseStatResult.builder().useTime(useTime).number(number.intValue()).build()));

        return useStatResults;
    }

    @Override
    public void recalculate(ReCalcCmd reCalcCmd) {
        MealReportQuery reportQuery = MealReportQuery.builder().day(reCalcCmd.getDay()).mealType(reCalcCmd.getMealType()).build();
        List<MealReport> mealReports = mealRepository.queryMealReports(reportQuery);

        List<MealTicket> tickets = mealRepository.getTickets(reCalcCmd.getMealType(), null, reCalcCmd.getDay());

        long reportNum = mealReports.stream().mapToLong(MealReport::getReportById).distinct().count();
        Integer mealNum = tickets.size();
        long usedNum = tickets.stream().filter(MealTicket::getUsed).count();
        Integer revokeNum = mealReports.stream().filter(MealReport::getRevoked).mapToInt(MealReport::getMealNum).sum();
        MealManageDo manageDo = MealManageDo.builder().mealType(reCalcCmd.getMealType())
                .day(reCalcCmd.getDay())
                .reportNum((int) reportNum)
                .mealNum(mealNum)
                .mealUsedNum((int) usedNum)
                .mealRevokeNum(revokeNum).build();
        mealRepository.updateManage(manageDo);
    }

    @Override
    public boolean stopReport() {
        Integer mealType = mealHelper.getMealTypeNow();
        StopReportCmd stopReportCmd = StopReportCmd.builder().mealType(mealType).day(LocalDate.now()).build();
        return stopReport(stopReportCmd);
    }

    @Override
    public boolean stopAdditionalReport() {
        Integer mealType = mealHelper.getMealTypeNow();
        return mealRepository.stopAdditionalReport(mealType, LocalDate.now());
    }

    @Override
    public void lunchReportRemind() {
        if (MealParamConst.SEND_LUNCH_REPORT_REMIND) {
            List<String> cpIds = List.of("wx1dee7aa3b2526c66", "ww22045bf5ac4e9de5");
            for (String cpId : cpIds) {
                WxCpMessage msg = WxCpMessage.TEXT().toUser("@all").content(DateUtil.today() + " 报餐已开始，请及时报餐。").build();
                wxMsgService.sendMsg(new CpId(cpId), msg);
            }

        }
    }

    private void sendStatAfterStopReport() {
        if (!MealParamConst.SEND_REPORT_STAT_DATA) {
            log.info("已关闭报餐截止后的通知。");
        } else {
            log.info("报餐截止后通知管理员。");
            sendReportStatData();
        }
    }

    private String getUniqueTicketCode(Long userId) {
        String ticketCode = mealHelper.genTicketCode(userId);
        while (!mealRepository.isUniqueTicket(ticketCode)) {
            ticketCode = mealHelper.genTicketCode(userId);
        }
        return ticketCode;
    }

    private boolean couldReport(MealReport mealReport, SysUser user) {
        // 午餐：在新厂使用蓝卡考勤机打卡才能报餐
        if (MealConst.MealType.LUNCH == mealReport.getMealType()) {
            // 如果配置了不需要打卡，则直接可以报餐
            List<String> excludeUsers = customParamService.fromJsonList(ParamModule.MEAL_REPORT, ParamKey.MealReport.EXCLUDE_LUNCH_CHECK_USER);
            if (excludeUsers.contains(String.valueOf(user.getUserId()))) {
                return true;
            }
            List<String> excludeDepts = customParamService.fromJsonList(ParamModule.MEAL_REPORT, ParamKey.MealReport.EXCLUDE_LUNCH_CHECK_DEPT);
            if (excludeDepts.contains(String.valueOf(user.getDeptId()))) {
                return true;
            }
            List<String> excludeTags = customParamService.fromStringList(ParamModule.MEAL_REPORT, ParamKey.MealReport.EXCLUDE_LUNCH_CHECK_TAG);
            if (CollUtil.isNotEmpty(excludeTags)) {
                List<WxCpTag> wxCpTags = wxTagService.tagList(new CpId(user.getMainWxcpId()));
                List<WxCpTag> existTags = wxCpTags.stream().filter(t -> excludeTags.contains(t.getName())).toList();
                if (CollUtil.isNotEmpty(existTags)) {
                    List<WxCpUser> wxCpUsers = new ArrayList<>();
                    existTags.forEach(t -> wxCpUsers.addAll(wxTagService.tagUsers(new CpId(user.getMainWxcpId()), t.getId())));
                    boolean isExcludeTagUser = wxCpUsers.stream().anyMatch(u -> user.getWxUserId().equals(u.getUserId()));
                    if (isExcludeTagUser) {
                        return true;
                    }
                }
            }

            // 如果在无锡使用蓝牙考勤机打卡，则可以报餐
            List<String> bluetoothNames = customParamService.fromStringList(ParamModule.MEAL_REPORT, ParamKey.MealReport.WUXI_CHECKIN_BLUETOOTH_NAME);
            // 如果没有设置蓝牙考勤机信息，则不校验打卡
            if (CollUtil.isEmpty(bluetoothNames)) {
                return true;
            }
            List<WxCpCheckinData> checkinDataList = wxOaService.listCheckinData(new CpId(user.getMainWxcpId()), DateUtil.beginOfDay(new Date()), new Date(), List.of(user.getWxUserId()));
            for (WxCpCheckinData wxCpCheckinData : checkinDataList) {
                if (bluetoothNames.contains(wxCpCheckinData.getLocationTitle())) {
                    return true;
                }
            }
            throw new BusinessException("午餐报餐前请使用新厂或三厂的蓝牙打卡机打卡。");
        }
        // 晚餐：申请了加班才能报餐
        if (MealConst.MealType.DINNER == mealReport.getMealType()) {
            // 年夜饭都能报餐
            if (mealReport.getDay().equals(LocalDate.of(2025, 1, 11))) {
                return true;
            }
            // 如果配置了不需要加班审批，则直接可以报餐
            List<String> excludeUsers = customParamService.fromJsonList(ParamModule.MEAL_REPORT, ParamKey.MealReport.EXCLUDE_DINNER_CHECK_USER);
            if (excludeUsers.contains(String.valueOf(user.getUserId()))) {
                return true;
            }
            List<String> excludeDepts = customParamService.fromJsonList(ParamModule.MEAL_REPORT, ParamKey.MealReport.EXCLUDE_DINNER_CHECK_DEPT);
            if (excludeDepts.contains(String.valueOf(user.getDeptId()))) {
                return true;
            }
            List<String> excludeTags = customParamService.fromStringList(ParamModule.MEAL_REPORT, ParamKey.MealReport.EXCLUDE_DINNER_CHECK_TAG);
            if (CollUtil.isNotEmpty(excludeTags)) {
                List<WxCpTag> wxCpTags = wxTagService.tagList(new CpId(user.getMainWxcpId()));
                List<WxCpTag> existTags = wxCpTags.stream().filter(t -> excludeTags.contains(t.getName())).toList();
                if (CollUtil.isNotEmpty(existTags)) {
                    List<WxCpUser> wxCpUsers = new ArrayList<>();
                    existTags.forEach(t -> wxCpUsers.addAll(wxTagService.tagUsers(new CpId(user.getMainWxcpId()), t.getId())));
                    boolean isExcludeTagUser = wxCpUsers.stream().anyMatch(u -> user.getWxUserId().equals(u.getUserId()));
                    if (isExcludeTagUser) {
                        return true;
                    }
                }
            }
            long overWorkStart = LocalDateTime.now().withHour(17).withMinute(0).withSecond(0).toEpochSecond(ZoneOffset.ofHours(8));
            long overWorkEnd = LocalDateTime.now().plusDays(1).withHour(8).withMinute(0).withSecond(0).toEpochSecond(ZoneOffset.ofHours(8));
            List<ApprovalOverTime> approvalOverWorkList = approvalWrapper.getOverWorkListNoNeedApplied(overWorkStart, overWorkEnd, user.getWxUserId(), user.getMainWxcpId());
            if (CollUtil.isNotEmpty(approvalOverWorkList)) {
                return true;
            }
            throw new BusinessException("晚餐报餐前请提交当天的加班申请。");
        }
        return true;
    }

    private boolean haveActiveOverworkApproval(LocalDate day, String wxUserId, String cpId) {
        LocalDateTime dateTime = LocalDateTime.of(day, LocalTime.now());
        long overWorkStart = dateTime.withHour(17).withMinute(0).withSecond(0).toEpochSecond(ZoneOffset.ofHours(8));
        long overWorkEnd = dateTime.plusDays(1).withHour(8).withMinute(0).withSecond(0).toEpochSecond(ZoneOffset.ofHours(8));
        List<ApprovalOverTime> approvalOverWorkList = approvalWrapper.getOverWorkListNoNeedApplied(overWorkStart, overWorkEnd, wxUserId, cpId);
        return CollUtil.isNotEmpty(approvalOverWorkList);
    }


    private void reportPreconditionCheck(MealReport mealReport, SysUser user) {
        boolean stopped = mealRepository.isStopped(mealReport.getMealType(), mealReport.getDay());
        if (stopped) {
            boolean additionalReportIsStopped = mealRepository.additionalReportIsStopped(mealReport.getMealType(), mealReport.getDay());
            if (additionalReportIsStopped) {
                String content = StrUtil.format("操作失败，{} 的 {} 已停止报餐和补餐。", DateUtil.today(), mealReport.getMealName());
                throw new BusinessException(content);
            }
            // 补餐前先看有无正常报餐
            MealReport build = MealReport.builder().mealType(mealReport.getMealType()).reportById(mealReport.getReportById()).day(mealReport.getDay()).revoked(false).build();
            boolean reportExist = mealRepository.isReportExist(build);
            if (reportExist) {
                throw new BusinessException(mealReport.getMealName() + "已报餐，查看餐券请点击'报餐-查看餐券'。");
            }
            // 停止报餐后，有补餐数量，直接补餐
            if (mealReport.getReportType() == MealConst.ReportType.SELF_REPORT) {
                mealReport.setReportType(MealConst.ReportType.ADDITIONAL_REPORT);
            }
        }
        // 自主报餐需要前置条件
        couldReport(mealReport, user);
        // 自主报餐不能重复报餐
        MealReport build = MealReport.builder().mealType(mealReport.getMealType()).reportById(mealReport.getReportById()).day(mealReport.getDay()).reportType(mealReport.getReportType()).revoked(false).build();
        boolean reportExist = mealRepository.isReportExist(build);
        if (reportExist) {
            throw new BusinessException(mealReport.getMealName() + "已报餐，查看餐券请点击'报餐-查看餐券'。");
        }

    }
}
