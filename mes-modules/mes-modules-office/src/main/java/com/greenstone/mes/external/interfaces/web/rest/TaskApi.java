package com.greenstone.mes.external.interfaces.web.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.external.application.dto.cmd.CopyStatusChangeCmd;
import com.greenstone.mes.external.application.dto.result.TaskResult;
import com.greenstone.mes.external.application.service.ProcessTaskService;
import com.greenstone.mes.external.dto.cmd.ProcessCmd;
import com.greenstone.mes.form.domain.service.CustomFormDataService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/27 16:38
 */
@AllArgsConstructor
@RestController
@RequestMapping("/flow/task")
public class TaskApi extends BaseController {

    private final ProcessTaskService processTaskService;
    private final CustomFormDataService customFormDataService;

    @PostMapping("/run")
    public AjaxResult runTask(@RequestBody @Validated ProcessCmd processCmd) {
        if (processCmd.getComment() == null) {
            processCmd.setComment(""); // 避免出现空指针
        }
        customFormDataService.processBatch(processCmd);
        return AjaxResult.success();
    }

    @GetMapping("/pending")
    public TableDataInfo pendingTasks() {
        startPage();
        List<TaskResult> taskResults = processTaskService.pendingTasks();
        return getDataTable(taskResults);
    }

    @GetMapping("/processed")
    public TableDataInfo processedTasks() {
        startPage();
        return getDataTable(processTaskService.processedTasks());
    }


    @GetMapping("/copies")
    public TableDataInfo taskCopies() {
        startPage();
        return getDataTable(processTaskService.currUserCopies());
    }

    @PutMapping("/copy")
    public AjaxResult copyHandleStatusChange(@Validated @RequestBody CopyStatusChangeCmd changeCmd) {
        processTaskService.copyHandleStatusChange(changeCmd);
        return AjaxResult.success();
    }

}
