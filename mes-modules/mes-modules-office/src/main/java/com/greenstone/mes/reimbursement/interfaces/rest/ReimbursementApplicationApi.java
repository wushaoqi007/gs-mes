package com.greenstone.mes.reimbursement.interfaces.rest;

import com.greenstone.mes.ces.application.dto.cmd.ApplicationRemoveCmd;
import com.greenstone.mes.ces.dto.cmd.AppStatusChangeCmd;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.datascope.annotation.DataScope;
import com.greenstone.mes.reimbursement.application.assembler.ReimbursementAppAssembler;
import com.greenstone.mes.reimbursement.application.dto.ReimbursementAppFuzzyQuery;
import com.greenstone.mes.reimbursement.application.dto.ReimbursementAppSaveCmd;
import com.greenstone.mes.reimbursement.application.dto.result.ReimbursementAppResult;
import com.greenstone.mes.reimbursement.application.service.ReimbursementAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/application")
public class ReimbursementApplicationApi extends BaseController {

    private final ReimbursementAppService reimbursementAppService;
    private final ReimbursementAppAssembler assembler;

    @GetMapping("/{serialNo}")
    public AjaxResult detail(@PathVariable("serialNo") @NotEmpty(message = "请选择单据") String serialNo) {
        ReimbursementAppResult reimbursementAppResult = reimbursementAppService.detail(serialNo);
        return AjaxResult.success(reimbursementAppResult);
    }

    @DataScope(userField = "applied_by", pageable = true)
    @GetMapping
    public TableDataInfo list(ReimbursementAppFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("reason");
        fields.add("appliedBy");
        query.setFields(fields);
        return getDataTable(reimbursementAppService.list(query));
    }

    @PostMapping("/draft")
    public AjaxResult draft(@Validated @RequestBody ReimbursementAppSaveCmd addCmd) {
        log.info("reimbursement application: receive draft command, {}", addCmd);
        reimbursementAppService.saveDraft(addCmd, assembler::fromReimbursementAppSaveCmd);
        return AjaxResult.success();
    }

    @PostMapping("/commit")
    public AjaxResult commit(@Validated @RequestBody ReimbursementAppSaveCmd addCmd) {
        log.info("reimbursement application: receive commit command, {}", addCmd);
        reimbursementAppService.saveCommit(addCmd, assembler::fromReimbursementAppSaveCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody ApplicationRemoveCmd removeCmd) {
        reimbursementAppService.delete(removeCmd.getSerialNos());
        return AjaxResult.success();
    }

    @PutMapping("/statusChange")
    public AjaxResult statusChange(@Validated @RequestBody AppStatusChangeCmd statusChangeCmd) {
        reimbursementAppService.changeStatus(statusChangeCmd);
        return AjaxResult.success();
    }

}
