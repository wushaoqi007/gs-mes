package com.greenstone.mes.material.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.material.application.service.MaterialTaskManager;
import com.greenstone.mes.material.request.MaterialTaskAddReq;
import com.greenstone.mes.material.request.MaterialTaskEditReq;
import com.greenstone.mes.material.request.MaterialTaskListReq;
import com.greenstone.mes.material.response.MaterialTaskListResp;
import com.greenstone.mes.material.domain.service.IMaterialTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务管控接口
 *
 * @author wushaoqi
 * @date 2022-08-08-10:51
 */
@Slf4j
@RestController
@RequestMapping("/task")
public class MaterialTaskController extends BaseController {

    @Autowired
    private IMaterialTaskService materialTaskService;

    @Autowired
    private MaterialTaskManager materialTaskManager;

    /**
     * 任务列表
     */
    @GetMapping("/list")
    public TableDataInfo taskList(MaterialTaskListReq materialTaskListReq) {
        startPage();
        List<MaterialTaskListResp> list = materialTaskService.selectMaterialTaskList(materialTaskListReq);
        for (MaterialTaskListResp materialTaskListResp : list) {
            List<MaterialTaskAddReq.MemberInfo> memberInfos = materialTaskManager.selectMaterialTaskMemberListById(materialTaskListResp.getId());
            materialTaskListResp.setMemberList(memberInfos);
        }
        return getDataTable(list);
    }

    /**
     * 任务列表（未关闭）
     */
    @GetMapping("/list/notClose")
    public TableDataInfo noCloseList(MaterialTaskListReq materialTaskListReq) {
        startPage();
        List<MaterialTaskListResp> list = materialTaskService.selectMaterialTaskNotCloseList(materialTaskListReq);
        for (MaterialTaskListResp materialTaskListResp : list) {
            List<MaterialTaskAddReq.MemberInfo> memberInfos = materialTaskManager.selectMaterialTaskMemberListById(materialTaskListResp.getId());
            materialTaskListResp.setMemberList(memberInfos);
        }
        return getDataTable(list);
    }

    /**
     * 任务列表（由我参与）
     */
    @GetMapping("/list/my")
    public TableDataInfo myList(MaterialTaskListReq materialTaskListReq) {
        startPage();
        List<MaterialTaskListResp> list = materialTaskService.selectMaterialTaskMyList(materialTaskListReq);
        for (MaterialTaskListResp materialTaskListResp : list) {
            List<MaterialTaskAddReq.MemberInfo> memberInfos = materialTaskManager.selectMaterialTaskMemberListById(materialTaskListResp.getId());
            materialTaskListResp.setMemberList(memberInfos);
        }
        return getDataTable(list);
    }

    /**
     * 新增任务
     */
    @Log(title = "新增任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @Validated MaterialTaskAddReq materialTaskAddReq) {
        materialTaskManager.insertMaterialTask(materialTaskAddReq);
        return AjaxResult.success("新增成功");
    }

    /**
     * 任务详情
     */
    @GetMapping(value = "/{id}")
    public AjaxResult detail(@PathVariable("id") Long id) {
        return AjaxResult.success(materialTaskManager.selectMaterialTaskById(id));
    }

    /**
     * 修改任务
     */
    @Log(title = "修改任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated MaterialTaskEditReq materialTaskEditReq) {
        materialTaskManager.updateMaterialTask(materialTaskEditReq);
        return AjaxResult.success("更新成功");
    }

    /**
     * 修改任务状态
     */
    @Log(title = "修改任务", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public AjaxResult changeStatus(@RequestBody @Validated MaterialTaskEditReq materialTaskEditReq) {
        materialTaskManager.updateMaterialTaskStatus(materialTaskEditReq);
        return AjaxResult.success("更新成功");
    }

    /**
     * 获取任务成员
     */
    @GetMapping(value = "/member/list/{id}")
    public AjaxResult memberList(@PathVariable("id") Long id) {
        return AjaxResult.success(materialTaskManager.selectMaterialTaskMemberListById(id));
    }
}
