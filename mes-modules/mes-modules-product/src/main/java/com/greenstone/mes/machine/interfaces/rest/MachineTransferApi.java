package com.greenstone.mes.machine.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineTransferAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineStockAllQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineStockScanQuery;
import com.greenstone.mes.machine.application.dto.result.MachinePartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineTransferResult;
import com.greenstone.mes.machine.application.service.MachineTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/transfer")
public class MachineTransferApi extends BaseController {

    private final MachineTransferService transferService;

    @GetMapping("/list")
    public TableDataInfo transferList(MachineFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(transferService.selectList(query));
    }

    @GetMapping("/part/scan")
    public AjaxResult partScan(@Validated MachineStockScanQuery query) {
        MachinePartStockR stockR = transferService.scan(query);
        return AjaxResult.success(stockR);
    }

    @GetMapping("/stock/all")
    public AjaxResult stockAll(@Validated MachineStockAllQuery query) {
        List<MachinePartStockR> stockRList = transferService.stockAll(query);
        return AjaxResult.success(stockRList);
    }

    @GetMapping(value = "/{serialNo}")
    public AjaxResult detail(@PathVariable("serialNo") String serialNo) {
        MachineTransferResult result = transferService.detail(serialNo);
        return AjaxResult.success(result);
    }

    @PostMapping("/draft")
    public AjaxResult draft(@RequestBody @Validated MachineTransferAddCmd addCmd) {
        transferService.saveDraft(addCmd);
        return AjaxResult.success("保存成功");
    }

    @PostMapping("/commit")
    public AjaxResult commit(@RequestBody @Validated MachineTransferAddCmd addCmd) {
        transferService.saveCommit(addCmd);
        return AjaxResult.success("提交成功");
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody MachineRemoveCmd removeCmd) {
        transferService.remove(removeCmd);
        return AjaxResult.success();
    }
}
