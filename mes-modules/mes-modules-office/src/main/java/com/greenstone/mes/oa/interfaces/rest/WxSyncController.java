package com.greenstone.mes.oa.interfaces.rest;

import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.oa.application.service.ApprovalService;
import com.greenstone.mes.oa.application.service.WxCheckinDataService;
import com.greenstone.mes.oa.application.service.WxSyncService;
import com.greenstone.mes.oa.dto.OaWxUserImportDto;
import com.greenstone.mes.oa.interfaces.request.ApprovalCorrectionImportCommand;
import com.greenstone.mes.oa.interfaces.request.ApprovalExtraWorkImportCommand;
import com.greenstone.mes.oa.interfaces.request.ApprovalNightImportCommand;
import com.greenstone.mes.oa.interfaces.request.ApprovalVacationImportCommand;
import com.greenstone.mes.oa.request.ClearDeletedWxUserCmd;
import com.greenstone.mes.oa.request.OaSyncApprovalCmd;
import com.greenstone.mes.oa.request.OaWxSyncImportReq;
import com.greenstone.mes.oa.request.SyncCheckinDataCmd;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.wxcp.domain.types.CpId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2022-06-08-9:48
 */
@Slf4j
@RestController
@RequestMapping("/sync")
public class WxSyncController extends BaseController {

    @Autowired
    private WxSyncService wxSyncService;

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private WxCheckinDataService wxCheckinDataService;


    /**
     * 同步企业微信部门
     */
    @PostMapping("/syncWxDept")
    public AjaxResult syncWxDept() {
        wxSyncService.syncWxDeptWithConfig();
        return AjaxResult.success("同步成功");
    }

    /**
     * 同步企业微信成员
     */
    @PostMapping("/syncWxUser")
    public AjaxResult syncWxUser() {
        wxSyncService.syncWxUserWithConfig();
        return AjaxResult.success("同步成功");
    }

    @PostMapping("/bindWxCpWithPhoneNum")
    public AjaxResult bindWxCpWithPhoneNum() {
        wxSyncService.bindWxCpWithPhoneNum();
        return AjaxResult.success("同步用户的企业微信信息成功");
    }

    @PostMapping("/clearDeletedUser")
    public AjaxResult clearDeletedUser(@RequestBody ClearDeletedWxUserCmd cmd) {
        if (cmd == null || cmd.getCpId() == null) {
            wxSyncService.clearDeletedUser();
        } else {
            wxSyncService.clearDeletedUser(new CpId(cmd.getCpId()));
        }
        return AjaxResult.success("清理用户成功");
    }

    /**
     * 同步企业微信审批数据
     */
    @PostMapping("/approval")
    @Async
    public AjaxResult syncApproval(@RequestBody OaSyncApprovalCmd oaSyncApprovalCmd) {
        log.info("sync approval params:{}", oaSyncApprovalCmd);
        wxSyncService.syncApproval(oaSyncApprovalCmd);
        return AjaxResult.success("同步成功");
    }

    @PostMapping("/approval/auditing")
    @Async
    public AjaxResult syncApprovalOfAuditing(@RequestBody OaSyncApprovalCmd oaSyncApprovalCmd) {
        log.info("sync approval of auditing params:{}", oaSyncApprovalCmd);
        wxSyncService.syncApprovalOfAuditing(oaSyncApprovalCmd);
        return AjaxResult.success("同步成功");
    }

    /**
     * 同步人员排班数据
     */
    @PostMapping("/schedule")
    public AjaxResult syncSchedule(@RequestBody OaSyncApprovalCmd oaSyncSearch) {
        // 默认同步当月班次
        if (Objects.isNull(oaSyncSearch.getStartDate()) || Objects.isNull(oaSyncSearch.getEndDate())) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
            int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, firstDay);
            oaSyncSearch.setStartDate(calendar.getTime());
            calendar.set(Calendar.DAY_OF_MONTH, lastDay);
            oaSyncSearch.setEndDate(calendar.getTime());
        }
        wxSyncService.syncSchedule(oaSyncSearch.getStartDate(), oaSyncSearch.getEndDate());
        return AjaxResult.success("同步成功");
    }

    /**
     * 同步企业微信打卡数据
     */
    @PostMapping("/checkinData")
    public AjaxResult syncCheckinData(@RequestBody SyncCheckinDataCmd syncCheckinDataCmd) {
        wxCheckinDataService.syncCheckData(syncCheckinDataCmd);
        return AjaxResult.success("同步成功");
    }

    /**
     * 通过导入同步请假
     */
    @PostMapping("/leave/import")
    public AjaxResult leaveImport(OaWxSyncImportReq importReq) throws Exception {
        log.info("Receive leave sync import request");
        // 将表格内容转为对象
        ExcelUtil<ApprovalVacationImportCommand> util = new ExcelUtil<>(ApprovalVacationImportCommand.class);
        List<ApprovalVacationImportCommand> importReqList = util.importExcel(importReq.getFile().getInputStream());
        approvalService.importVacations(new CpId(importReq.getCpId()), importReqList);
        return AjaxResult.success("后台处理中");
    }

    /**
     * 通过导入同步加班
     */
    @PostMapping("/overtime/import")
    public AjaxResult overTimeImport(OaWxSyncImportReq importReq) throws Exception {
        log.info("Receive overtime sync import request");
        // 将表格内容转为对象
        ExcelUtil<ApprovalExtraWorkImportCommand> util = new ExcelUtil<>(ApprovalExtraWorkImportCommand.class);
        List<ApprovalExtraWorkImportCommand> importReqList = util.importExcel(importReq.getFile().getInputStream());
        approvalService.importExtraWorks(new CpId(importReq.getCpId()), importReqList);
        return AjaxResult.success("后台处理中");
    }

    /**
     * 通过导入同步夜班
     */
    @PostMapping("/night/import")
    public AjaxResult nightImport(OaWxSyncImportReq importReq) throws Exception {
        log.info("Receive night sync import request");
        // 将表格内容转为对象
        ExcelUtil<ApprovalNightImportCommand> util = new ExcelUtil<>(ApprovalNightImportCommand.class);
        List<ApprovalNightImportCommand> importReqList = util.importExcel(importReq.getFile().getInputStream());
        approvalService.importNights(new CpId(importReq.getCpId()), importReqList);
        return AjaxResult.success("后台处理中");
    }

    /**
     * 通过导入同步打卡补卡
     */
    @PostMapping("/punchCorrection/import")
    public AjaxResult punchCorrectionImport(OaWxSyncImportReq importReq) throws Exception {
        log.info("Receive punchCorrection sync import request");
        // 将表格内容转为对象
        ExcelUtil<ApprovalCorrectionImportCommand> util = new ExcelUtil<>(ApprovalCorrectionImportCommand.class);
        List<ApprovalCorrectionImportCommand> importReqList = util.importExcel(importReq.getFile().getInputStream());
        approvalService.importCorrections(new CpId(importReq.getCpId()), importReqList);
        return AjaxResult.success("后台处理中");
    }

    /**
     * 通过导入同步通讯录人员
     */
    @PostMapping("/wxUser/import")
    public AjaxResult wxUserImport(OaWxSyncImportReq importReq) throws Exception {
        log.info("Wx user and dept sync import request");
        // 将表格内容转为对象
        ExcelUtil<OaWxUserImportDto> util = new ExcelUtil<>(OaWxUserImportDto.class);
        List<OaWxUserImportDto> importReqList = util.importExcel(importReq.getFile().getInputStream());
        wxSyncService.importSyncWxUser(importReq.getCpId(), importReqList);
        return AjaxResult.success("导入成功");
    }

    @PostMapping("/wxUser/phone")
    public AjaxResult addUserByPhoneNum(@RequestBody SysUser sysUser) {
        return AjaxResult.success(wxSyncService.addUserByPhoneNum(sysUser.getPhonenumber()));
    }

}
