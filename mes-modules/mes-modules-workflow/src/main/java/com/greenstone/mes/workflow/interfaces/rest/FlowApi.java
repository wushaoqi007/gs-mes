package com.greenstone.mes.workflow.interfaces.rest;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.workflow.cmd.FlowCommitCmd;
import com.greenstone.mes.workflow.domain.service.FlowService;
import com.greenstone.mes.workflow.resp.FlowCommitResp;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@RestController
@RequestMapping("/workflow/processes")
public class FlowApi {

    private final FlowService flowService;

    @Transactional
    @ApiLog
    @PostMapping("/commit")
    public AjaxResult commit(@RequestBody @Validated FlowCommitCmd commitCmd) {
        FlowCommitResp flowCommitResp = flowService.commit(commitCmd);
        return AjaxResult.success(flowCommitResp);
    }

    @ApiLog
    @GetMapping("/todoTasks")
    public AjaxResult todoTasks() {
        return AjaxResult.success(flowService.todoTasks());
    }

    @ApiLog
    @GetMapping("/hisTask")
    public AjaxResult hisTask(String instanceNo) {
        if (StrUtil.isBlank(instanceNo)){
            throw new RuntimeException("请指定流程实例编号");
        }
        return AjaxResult.success(flowService.hisTask(instanceNo));
    }
}
