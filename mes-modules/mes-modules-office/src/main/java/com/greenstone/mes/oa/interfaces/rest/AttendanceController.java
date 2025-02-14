package com.greenstone.mes.oa.interfaces.rest;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.oa.application.dto.AttendCheatCheckQuery;
import com.greenstone.mes.oa.application.dto.AttendCheatResult;
import com.greenstone.mes.oa.application.dto.attendance.result.AttendanceDetailResult;
import com.greenstone.mes.oa.application.service.AttendanceExportService;
import com.greenstone.mes.oa.application.service.AttendanceService;
import com.greenstone.mes.oa.application.service.WxCheckinDataService;
import com.greenstone.mes.oa.application.service.WxSyncService;
import com.greenstone.mes.oa.dto.AttendanceCalcCommand;
import com.greenstone.mes.oa.infrastructure.enums.WxCp;
import com.greenstone.mes.oa.interfaces.request.*;
import com.greenstone.mes.oa.interfaces.resp.AttendanceMyMonthResult;
import com.greenstone.mes.oa.request.OaSyncApprovalCmd;
import com.greenstone.mes.oa.request.SyncCheckinDataCmd;
import com.greenstone.mes.wxcp.domain.helper.WxDeptService;
import com.greenstone.mes.wxcp.domain.helper.WxUserService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/attendance")
public class AttendanceController extends BaseController {

    @Autowired
    private AttendanceExportService attendanceExportService;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private WxUserService externalWxUserService;
    @Autowired
    private WxDeptService externalWxDeptService;
    @Autowired
    private WxSyncService wxSyncService;
    @Autowired
    private WxCheckinDataService wxCheckinDataService;

    @PostMapping("/export/absent")
    public void exportAbsentStat(HttpServletResponse response, @RequestBody AttnAbsentStatExportCommend exportCommend) {
        log.info("AttnAbsentStatExportCommend params:{}", exportCommend);
        XSSFWorkbook excel = attendanceExportService.makeAbsentStatExcel(exportCommend.getStart(), exportCommend.getEnd(), new CpId(exportCommend.getCpId()));
        ExcelUtil<AttendanceDetailResult> excelUtil = new ExcelUtil<>();
        excelUtil.writeToHttp(response, excel, "缺勤统计");
    }

    @PostMapping("/export/vacation")
    public void exportVacationStat(HttpServletResponse response, @RequestBody AttnVacationStatExportCommend exportCommend) {
        log.info("AttnVacationStatExportCommend params:{}", exportCommend);
        XSSFWorkbook excel = attendanceExportService.makeVacationStatExcel(exportCommend.getStart(), exportCommend.getEnd(), new CpId(exportCommend.getCpId()));
        ExcelUtil<AttendanceDetailResult> excelUtil = new ExcelUtil<>();
        excelUtil.writeToHttp(response, excel, "请假统计");
    }

    @PostMapping("/export/extra-work")
    public void exportExtraWorkStat(HttpServletResponse response, @RequestBody AttnExtraWorkStatExportCommend exportCommend) {
        log.info("AttnExtraWorkStatExportCommend params:{}", exportCommend);
        XSSFWorkbook excel = attendanceExportService.makeExtraWorkStatExcel(exportCommend.getStart(), exportCommend.getEnd(), new CpId(exportCommend.getCpId()));
        ExcelUtil<AttendanceDetailResult> excelUtil = new ExcelUtil<>();
        excelUtil.writeToHttp(response, excel, "加班统计");
    }

    @PostMapping("/export/outsource")
    public void exportOutsourceStat(HttpServletResponse response, @RequestBody AttnOutsourceStatExportCommend exportCommend) {
        log.info("AttnOutsourceStatExportCommend params:{}", exportCommend);
        XSSFWorkbook excel = attendanceExportService.makeOutsourceStatExcel(exportCommend.getStart(), exportCommend.getEnd(), new CpId(exportCommend.getCpId()));
        ExcelUtil<AttendanceDetailResult> excelUtil = new ExcelUtil<>();
        excelUtil.writeToHttp(response, excel, "外包统计表");
    }

    @PostMapping("/export/vacation-record")
    public void exportVacationRecord(HttpServletResponse response, @RequestBody AttnVacationRecordExportCommend exportCommend) {
        log.info("AttnVacationRecordExportCommend params:{}", exportCommend);
        XSSFWorkbook excel = attendanceExportService.makeVacationRecordExcel(exportCommend.getStart(), exportCommend.getEnd(), new CpId(exportCommend.getCpId()));
        ExcelUtil<AttendanceDetailResult> excelUtil = new ExcelUtil<>();
        excelUtil.writeToHttp(response, excel, "请假记录");
    }

    @PostMapping("/export/checkin-record")
    public void exportCheckinRecord(HttpServletResponse response, @RequestBody AttnCheckinRecordExportCommend exportCommend) {
        // 导出昨天之前的数据，不需要更新打卡和考勤数据
        Date yesterday = DateUtil.offset(DateUtil.beginOfDay(new Date()), DateField.DAY_OF_MONTH, -1);
        if (exportCommend.getEnd() != null && exportCommend.getEnd().getTime() >= yesterday.getTime()) {
            log.info("同步最新打卡数据");
            wxCheckinDataService.syncCheckData(SyncCheckinDataCmd.builder().cpId(exportCommend.getCpId()).build());
            log.info("更新昨日考勤统计结果");
            attendanceService.calcAndSaveYesterday();
        }
        log.info("开始导出考勤明细");
        XSSFWorkbook excel = attendanceExportService.makeCheckinDetailExcel(exportCommend.getStart(), exportCommend.getEnd(), new CpId(exportCommend.getCpId()));
        ExcelUtil<AttendanceDetailResult> excelUtil = new ExcelUtil<>();
        excelUtil.writeToHttp(response, excel, "明细表");
        log.info("完成考勤明细导出");
    }

    @PostMapping("/export/workTime")
    public void exportProdWorkTime(HttpServletResponse response, @RequestBody AttendWorkTimeStatExportCommend exportCommend) {
        log.info("AttendWorkTimeStatExportCommend params:{}", exportCommend);
        XSSFWorkbook excel = attendanceExportService.makeWorkTimeStatExcel(exportCommend.getStart(), exportCommend.getEnd(), new CpId(exportCommend.getCpId()), exportCommend.isAllDept());
        ExcelUtil<AttendanceDetailResult> excelUtil = new ExcelUtil<>();
        excelUtil.writeToHttp(response, excel, "工时统计");
    }

    @PostMapping("/calc/yesterday")
    public void calcYesterday() {
        log.info("开始异步统计昨日考勤。。。");
        externalWxUserService.refreshUser();
        externalWxDeptService.refreshDept();
        for (WxCp wxCp : WxCp.values()) {
            wxSyncService.syncApproval(OaSyncApprovalCmd.builder().cpId(wxCp.getCpId()).build());
            wxCheckinDataService.syncCheckData(SyncCheckinDataCmd.builder().cpId(wxCp.getCpId()).build());
        }
        attendanceService.calcAndSaveYesterdayAsync();
    }

    @PostMapping("/calc/backend")
    public void calcRange(@RequestBody @Validated AttendanceCalcCommand command) {
        log.info("开始异步统计一段时间内考勤。。。");
        if (command.isRefreshCache()) {
            externalWxUserService.refreshUser();
            externalWxDeptService.refreshDept();
        }
        if (!command.isQuickCalc()) {
            wxSyncService.syncApproval(OaSyncApprovalCmd.builder().cpId(command.getCpId()).build());
            wxCheckinDataService.syncCheckData(SyncCheckinDataCmd.builder().cpId(command.getCpId()).build());
        }
        attendanceService.calcAndSaveAsync(command.getStart(), command.getEnd(), new CpId(command.getCpId()), command.getUserId());
    }

    @GetMapping("/my/month")
    public AjaxResult getMonthAttendanceResult(AttendanceMyMonthQuery query) {
        AttendanceMyMonthResult attendanceMyMonthResult = attendanceService.statMyMonthAttendance(query);
        return AjaxResult.success(attendanceMyMonthResult);
    }

    @PostMapping("/refresh")
    public AjaxResult refresh() {
        externalWxUserService.refreshUser();
        externalWxDeptService.refreshDept();
        return AjaxResult.success("刷新缓存");
    }

    @GetMapping("/cheatCheck")
    public AjaxResult cheatCheck(AttendCheatCheckQuery cheatCheckQuery) {
        List<AttendCheatResult> cheatResults = attendanceService.analyseCheat(DateUtil.parse(cheatCheckQuery.getStartTime(), "yyyy-MM-dd"),
                DateUtil.parse(cheatCheckQuery.getEndTime(), "yyyy-MM-dd"),
                new CpId(cheatCheckQuery.getCpId()));
        return AjaxResult.success(cheatResults);
    }

    @PostMapping("/export/cheatCheck")
    public void exportCheatCheck(HttpServletResponse response, @RequestBody AttendCheatCheckQuery cheatCheckQuery) {
        List<AttendCheatResult> cheatResults = attendanceService.analyseCheat(DateUtil.parse(cheatCheckQuery.getStartTime(), "yyyy-MM-dd"),
                DateUtil.parse(cheatCheckQuery.getEndTime(), "yyyy-MM-dd"),
                new CpId(cheatCheckQuery.getCpId()));
        ExcelUtil<AttendCheatResult> excelUtil = new ExcelUtil<>(AttendCheatResult.class);
        excelUtil.exportExcel(response, cheatResults, "代打卡检查");
    }

}
