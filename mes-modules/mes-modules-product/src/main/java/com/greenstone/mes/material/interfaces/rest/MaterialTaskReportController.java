package com.greenstone.mes.material.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.material.domain.MaterialTaskProblemReport;
import com.greenstone.mes.material.domain.MaterialTaskProgressReport;
import com.greenstone.mes.material.domain.MaterialTaskWorkReport;
import com.greenstone.mes.material.application.service.MaterialTaskReportManager;
import com.greenstone.mes.material.request.MaterialTaskProblemReportAddReq;
import com.greenstone.mes.material.request.MaterialTaskProgressReportAddReq;
import com.greenstone.mes.material.request.MaterialTaskReportListReq;
import com.greenstone.mes.material.request.MaterialTaskWorkReportAddReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务上报接口
 *
 * @author wushaoqi
 * @date 2022-08-09-9:13
 */
@Slf4j
@RestController
@RequestMapping("/taskReport")
public class MaterialTaskReportController extends BaseController {

    @Autowired
    private MaterialTaskReportManager reportManager;

    /**
     * 进度报告(PC)
     */
    @Log(title = "任务上报", businessType = BusinessType.INSERT)
    @PostMapping("/progress")
    public AjaxResult progressReport(MaterialTaskProgressReportAddReq progressReportAddReq) {
        reportManager.progressReport(progressReportAddReq.getFile(), progressReportAddReq);
        return AjaxResult.success("新增成功");
    }

    /**
     * 进度报告(小程序)
     */
    @Log(title = "任务上报", businessType = BusinessType.INSERT)
    @PostMapping("/progress/addFromApp")
    public AjaxResult progressReportAddFromApp(@RequestBody MaterialTaskProgressReportAddReq progressReportAddReq) {
        reportManager.progressReport(progressReportAddReq.getFile(), progressReportAddReq);
        return AjaxResult.success("新增成功");
    }

    /**
     * 问题报告(PC)
     */
    @Log(title = "任务上报", businessType = BusinessType.INSERT)
    @PostMapping("/problem")
    public AjaxResult problemReport(MaterialTaskProblemReportAddReq problemReportAddReq) {
        reportManager.problemReport(problemReportAddReq.getFile(), problemReportAddReq);
        return AjaxResult.success("新增成功");
    }

    /**
     * 问题报告(小程序)
     */
    @Log(title = "任务上报", businessType = BusinessType.INSERT)
    @PostMapping("/problem/addFromApp")
    public AjaxResult problemReportAddFromApp(@RequestBody MaterialTaskProblemReportAddReq problemReportAddReq) {
        reportManager.problemReport(problemReportAddReq.getFile(), problemReportAddReq);
        return AjaxResult.success("新增成功");
    }

    /**
     * 工作报告
     */
    @Log(title = "任务上报", businessType = BusinessType.INSERT)
    @PostMapping("/work")
    public AjaxResult progressReport(@RequestBody MaterialTaskWorkReportAddReq workReportAddReq) {
        reportManager.workReport(workReportAddReq);
        return AjaxResult.success("新增成功");
    }

    /**
     * 工作报告查询
     */
    @GetMapping("/work/list")
    public TableDataInfo workList(MaterialTaskReportListReq reportListReq) {
        startPage();
        List<MaterialTaskWorkReport> list = reportManager.selectWorkList(reportListReq);
        return getDataTable(list);
    }

    /**
     * 问题报告查询
     */
    @GetMapping("/problem/list")
    public TableDataInfo problemList(MaterialTaskReportListReq reportListReq) {
        startPage();
        List<MaterialTaskProblemReport> list = reportManager.selectProblemList(reportListReq);
        return getDataTable(list);
    }

    /**
     * 进度报告查询
     */
    @GetMapping("/progress/list")
    public TableDataInfo progressList(MaterialTaskReportListReq reportListReq) {
        startPage();
        List<MaterialTaskProgressReport> list = reportManager.selectProgressList(reportListReq);
        return getDataTable(list);
    }


}
