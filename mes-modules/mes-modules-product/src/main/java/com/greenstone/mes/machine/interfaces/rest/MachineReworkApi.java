package com.greenstone.mes.machine.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineReworkAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineCheckPartListQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineReworkPartScanQuery;
import com.greenstone.mes.machine.application.dto.result.MachineCheckPartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineReworkResult;
import com.greenstone.mes.machine.application.service.MachineReworkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/rework")
public class MachineReworkApi extends BaseController {

    private final MachineReworkService reworkService;

    @GetMapping("/list")
    public TableDataInfo reworkList(MachineFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(reworkService.selectList(query));
    }


    @GetMapping(value = "/{serialNo}")
    public AjaxResult detail(@PathVariable("serialNo") String serialNo) {
        MachineReworkResult result = reworkService.detail(serialNo);
        return AjaxResult.success(result);
    }

    @GetMapping("/record")
    public TableDataInfo listRecord(MachineRecordFuzzyQuery query) {
        startPage();
        return getDataTable(reworkService.listRecord(query));
    }

    @GetMapping("/part/scan")
    public AjaxResult partScan(@Validated MachineReworkPartScanQuery query) {
        MachineCheckPartStockR partStockR = reworkService.scan(query);
        return AjaxResult.success(partStockR);
    }

    @GetMapping("/part/choose")
    public TableDataInfo partChoose(MachineCheckPartListQuery query) {
        startPage();
        return getDataTable(reworkService.partChoose(query));
    }

    @PostMapping("/draft")
    public AjaxResult draft(@RequestBody @Validated MachineReworkAddCmd addCmd) {
        reworkService.saveDraft(addCmd);
        return AjaxResult.success("保存成功");
    }

    @PostMapping("/commit")
    public AjaxResult commit(@RequestBody @Validated MachineReworkAddCmd addCmd) {
        reworkService.saveCommit(addCmd);
        return AjaxResult.success("提交成功");
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody MachineRemoveCmd removeCmd) {
        reworkService.remove(removeCmd);
        return AjaxResult.success();
    }
}
