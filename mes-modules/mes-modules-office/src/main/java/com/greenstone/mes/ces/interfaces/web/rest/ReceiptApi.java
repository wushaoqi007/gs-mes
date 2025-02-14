package com.greenstone.mes.ces.interfaces.web.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.ces.application.dto.cmd.ReceiptAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.ReceiptEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.ReceiptRemoveCmd;
import com.greenstone.mes.ces.application.dto.query.ReceiptFuzzyQuery;
import com.greenstone.mes.ces.application.dto.query.ReceiptQuery;
import com.greenstone.mes.ces.application.dto.result.ReceiptResult;
import com.greenstone.mes.ces.application.service.ReceiptService;
import com.greenstone.mes.ces.dto.cmd.ReceiptStatusChangeCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/consumable/receipt")
public class ReceiptApi extends BaseController {

    private final ReceiptService receiptService;

    public ReceiptApi(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @PutMapping("/statusChange")
    public AjaxResult statusChange(@Validated @RequestBody ReceiptStatusChangeCmd statusChangeCmd) {
        receiptService.statusChange(statusChangeCmd);
        return AjaxResult.success();
    }

    @GetMapping
    public AjaxResult detail(@Validated ReceiptQuery query) {
        ReceiptResult detail = receiptService.detail(query.getSerialNo());
        return AjaxResult.success(detail);
    }

    @GetMapping("/list")
    public TableDataInfo list(ReceiptFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(receiptService.list(query));
    }

    @PostMapping
    public AjaxResult add(@Validated @RequestBody ReceiptAddCmd addCmd) {
        receiptService.add(addCmd);
        return AjaxResult.success();
    }

    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ReceiptEditCmd editCmd) {
        receiptService.edit(editCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody ReceiptRemoveCmd removeCmd) {
        receiptService.remove(removeCmd);
        return AjaxResult.success();
    }
}
