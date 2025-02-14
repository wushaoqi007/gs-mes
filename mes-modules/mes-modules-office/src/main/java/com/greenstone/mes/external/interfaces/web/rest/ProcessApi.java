package com.greenstone.mes.external.interfaces.web.rest;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.external.application.dto.cmd.ProcessSaveCmd;
import com.greenstone.mes.external.application.dto.query.ProcDefQuery;
import com.greenstone.mes.external.application.service.ProcessDefinitionService;
import com.greenstone.mes.external.application.service.ProcessInstanceService;
import com.greenstone.mes.external.dto.cmd.ProcessCmd;
import com.greenstone.mes.external.dto.cmd.ProcessRevokeCmd;
import com.greenstone.mes.external.dto.cmd.ProcessStartCmd;
import com.greenstone.mes.form.domain.service.CustomFormDataService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author gu_renkai
 * @date 2023/2/27 16:38
 */
@AllArgsConstructor
@RestController
@RequestMapping("/flow/process")
public class ProcessApi {

    private final ProcessDefinitionService processDefinitionService;

    private final ProcessInstanceService processInstanceService;

    private final CustomFormDataService customFormDataService;

    @GetMapping
    public AjaxResult get(ProcDefQuery procDefQuery) {
        return AjaxResult.success(processDefinitionService.get(procDefQuery));
    }

    @GetMapping("/definition/exist")
    public AjaxResult getDefinitionId(@RequestParam("formId") String formId) {
        return AjaxResult.success(processDefinitionService.getDefinitionId(formId));
    }

    @PostMapping
    public AjaxResult save(@RequestBody @Validated ProcessSaveCmd saveCmd) {
        return AjaxResult.success(processDefinitionService.save(saveCmd));
    }

    @ApiLog
    @PostMapping("/start")
    public AjaxResult startProcess(@RequestBody @Validated ProcessStartCmd startCmd) {
        if (startCmd.getComment() == null) {
            startCmd.setComment("");
        }
        // TODO 需要传递表单名称，如果为空需要设置为对应的菜单名称
        if (StrUtil.isEmpty(startCmd.getFormName())) {
            startCmd.setFormName("");
        }
        return AjaxResult.success(processInstanceService.createAndRun(startCmd));
    }

    @PostMapping("/run")
    public AjaxResult runTask(@RequestBody @Validated ProcessCmd processCmd) {
        if (processCmd.getComment() == null) {
            processCmd.setComment("");
        }
        customFormDataService.processBatch(processCmd);
        return AjaxResult.success();
    }

    @PostMapping("/revoke")
    public AjaxResult revoke(@RequestBody @Validated ProcessRevokeCmd revokeCmd) {
        processInstanceService.revokeProcess(revokeCmd);
        return AjaxResult.success();
    }

}
