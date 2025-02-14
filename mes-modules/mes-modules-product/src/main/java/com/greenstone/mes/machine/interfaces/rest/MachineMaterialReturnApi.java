package com.greenstone.mes.machine.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineMaterialReturnAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartListQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartScanQuery;
import com.greenstone.mes.machine.application.dto.result.MachineOrderPartR;
import com.greenstone.mes.machine.application.dto.result.MachineMaterialReturnResult;
import com.greenstone.mes.machine.application.service.MachineMaterialReturnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/material/return")
public class MachineMaterialReturnApi extends BaseController {

    private final MachineMaterialReturnService materialReturnService;

    @GetMapping("/list")
    public TableDataInfo materialReturnList(MachineFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(materialReturnService.selectList(query));
    }

    @GetMapping("/part/scan")
    public AjaxResult partScan(@Validated MachineOrderPartScanQuery query) {
        MachineOrderPartR part = materialReturnService.scan(query);
        return AjaxResult.success(part);
    }

    @GetMapping("/part/choose")
    public TableDataInfo partChoose(MachineOrderPartListQuery query) {
        startPage();
        return getDataTable(materialReturnService.partChoose(query));
    }

    @GetMapping(value = "/{serialNo}")
    public AjaxResult detail(@PathVariable("serialNo") String serialNo) {
        MachineMaterialReturnResult result = materialReturnService.detail(serialNo);
        return AjaxResult.success(result);
    }

    @PostMapping("/draft")
    public AjaxResult draft(@RequestBody @Validated MachineMaterialReturnAddCmd addCmd) {
        materialReturnService.saveDraft(addCmd);
        return AjaxResult.success("保存成功");
    }

    @PostMapping("/commit")
    public AjaxResult commit(@RequestBody @Validated MachineMaterialReturnAddCmd addCmd) {
        materialReturnService.saveCommit(addCmd);
        return AjaxResult.success("提交成功");
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody MachineRemoveCmd removeCmd) {
        materialReturnService.remove(removeCmd);
        return AjaxResult.success();
    }
}
