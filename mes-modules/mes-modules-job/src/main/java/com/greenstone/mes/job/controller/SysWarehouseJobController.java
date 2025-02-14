package com.greenstone.mes.job.controller;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.job.service.ISysWarehouseJobService;
import com.greenstone.mes.job.util.CronUtils;
import com.greenstone.mes.system.domain.SysWarehouseJob;
import com.greenstone.mes.system.dto.cmd.SysWarehouseJobAddReq;
import com.greenstone.mes.system.dto.cmd.SysWarehouseJobEditReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 仓库任务
 *
 * @author wushaoqi
 * @date 2022-11-01-8:56
 */
@Slf4j
@RestController
@RequestMapping("/warehouse/job")
public class SysWarehouseJobController extends BaseController {

    @Autowired
    private ISysWarehouseJobService warehouseJobService;

    /**
     * 查询定时任务列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysWarehouseJob job) {
        startPage();
        List<SysWarehouseJob> list = warehouseJobService.selectJobList(job);
        return getDataTable(list);
    }

    /**
     * 获取任务详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(warehouseJobService.getJobById(id));
    }

    /**
     * 新增任务
     */
    @Log(title = "定时任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @Validated SysWarehouseJobAddReq job) {
        if (!CronUtils.isValid(job.getCron())) {
            return error("新增任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }
        warehouseJobService.insertJob(job);
        return AjaxResult.success("新增成功");
    }

    /**
     * 编辑任务
     */
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated SysWarehouseJobEditReq job) {
        if (!CronUtils.isValid(job.getCron())) {
            return error("更新任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }
        warehouseJobService.updateJob(job);
        return AjaxResult.success("更新成功");
    }

    /**
     * 删除任务
     */
    @Log(title = "定时任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        warehouseJobService.deleteJobById(id);
        return AjaxResult.success();
    }

    /**
     * 任务状态修改
     */
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysWarehouseJob job) {
        warehouseJobService.changeStatus(job);
        return AjaxResult.success("更新成功");
    }

}
