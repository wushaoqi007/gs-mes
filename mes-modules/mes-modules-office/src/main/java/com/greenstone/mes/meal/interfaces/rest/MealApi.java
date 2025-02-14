package com.greenstone.mes.meal.interfaces.rest;

import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.meal.application.assembler.MealAssembler;
import com.greenstone.mes.meal.application.dto.excel.MealReportExcel;
import com.greenstone.mes.meal.application.service.MealService;
import com.greenstone.mes.office.meal.dto.cmd.AdminMealReportCmd;
import com.greenstone.mes.office.meal.dto.cmd.MealRevokeCmd;
import com.greenstone.mes.office.meal.dto.cmd.ReCalcCmd;
import com.greenstone.mes.office.meal.dto.cmd.StopReportCmd;
import com.greenstone.mes.office.meal.dto.query.MealManageQuery;
import com.greenstone.mes.office.meal.dto.query.MealReportQuery;
import com.greenstone.mes.office.meal.dto.query.TicketUseStatQuery;
import com.greenstone.mes.office.meal.dto.result.MealManageResult;
import com.greenstone.mes.office.meal.dto.result.MealReportResult;
import com.greenstone.mes.office.meal.dto.result.TicketUseResult;
import com.greenstone.mes.office.meal.dto.result.TicketUseStatResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/meal")
public class MealApi extends BaseController {

    private final MealService mealService;
    private final MealAssembler mealAssembler;

    @GetMapping("/report")
    public TableDataInfo queryList(MealReportQuery query) {
        startPage();
        List<MealReportResult> mealReportResults = mealService.queryReposts(query);
        return getDataTable(mealReportResults);
    }

    @GetMapping("/report/manage")
    public TableDataInfo manageList(MealManageQuery query) {
        startPage();
        List<MealManageResult> mealManageResults = mealService.queryManages(query);
        return getDataTable(mealManageResults);
    }

    @PostMapping("/report")
    public AjaxResult adminReport(@RequestBody @Validated AdminMealReportCmd reportCmd) {
        mealService.adminReport(reportCmd);
        return AjaxResult.success();
    }

    @PostMapping("/revoke")
    public AjaxResult adminRevoke(@RequestBody @Validated MealRevokeCmd reportCmd) {
        mealService.adminRevoke(reportCmd);
        return AjaxResult.success();
    }

    @PostMapping("/ticket/use/{code}")
    public AjaxResult useTicket(@PathVariable("code") String code) {
        TicketUseResult ticketUseResult = mealService.useTicket(code);
        if (ticketUseResult.isSuccess()) {
            return AjaxResult.success("请用餐");
        } else {
            return AjaxResult.error(ticketUseResult.getMsg());
        }
    }

    @GetMapping("/ticket/use/stat")
    public AjaxResult queryTicketUseStat(@Validated TicketUseStatQuery useStatQuery) {
        List<TicketUseStatResult> statResults = mealService.queryTicketUseStat(useStatQuery);
        return AjaxResult.success(statResults);
    }

    @PostMapping("/report/stop")
    public AjaxResult stopReport(@RequestBody @Validated StopReportCmd stopReportCmd) {
        if (stopReportCmd.getDay() == null) {
            stopReportCmd.setDay(LocalDate.now());
        }
        if (mealService.stopReport(stopReportCmd)) {
            return AjaxResult.success();
        } else {
            return AjaxResult.error("请选择正确的餐次");
        }
    }

    @PostMapping("/report/manage/recalculate")
    public AjaxResult recalculate(@RequestBody @Validated ReCalcCmd reCalcCmd) {
        mealService.recalculate(reCalcCmd);
        return AjaxResult.success();
    }

    @PostMapping("/report/stat/send")
    public AjaxResult sendReportStatData() {
        mealService.sendReportStatData();
        return AjaxResult.success();
    }

    @PostMapping("/report/export")
    public void exportReport(@RequestBody MealReportQuery query, HttpServletResponse response) {
        List<MealReportResult> mealReportResults = mealService.queryReposts(query);
        List<MealReportExcel> mealReportExcels = mealAssembler.results2excels(mealReportResults);

        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = "报餐记录-" + DateUtil.now();
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), MealReportExcel.class).sheet("报餐记录").doWrite(mealReportExcels);
        } catch (IOException e) {
            log.error("饱餐记录导出错误" + e.getMessage());
            throw new RuntimeException("饱餐记录导出错误");
        }
    }

}
