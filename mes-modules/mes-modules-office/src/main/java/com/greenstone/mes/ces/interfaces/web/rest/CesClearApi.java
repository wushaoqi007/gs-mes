package com.greenstone.mes.ces.interfaces.web.rest;

import com.greenstone.mes.ces.application.dto.cmd.CesClearAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.CesClearEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.CesClearRemoveCmd;
import com.greenstone.mes.ces.application.dto.query.CesClearFuzzyQuery;
import com.greenstone.mes.ces.application.dto.query.CesClearQuery;
import com.greenstone.mes.ces.application.dto.result.CesClearResult;
import com.greenstone.mes.ces.application.service.CesClearService;
import com.greenstone.mes.ces.dto.cmd.CesClearStatusChangeCmd;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/consumable/clear")
public class CesClearApi extends BaseController {

    private final CesClearService cesClearService;

    public CesClearApi(CesClearService cesClearService) {
        this.cesClearService = cesClearService;
    }

    @PutMapping("/statusChange")
    public AjaxResult statusChange(@Validated @RequestBody CesClearStatusChangeCmd statusChangeCmd) {
        cesClearService.statusChange(statusChangeCmd);
        return AjaxResult.success();
    }

    @GetMapping
    public AjaxResult detail(@Validated CesClearQuery query) {
        CesClearResult detail = cesClearService.detail(query.getSerialNo());
        return AjaxResult.success(detail);
    }

    @GetMapping("/list")
    public TableDataInfo list(CesClearFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(cesClearService.list(query));
    }

    @PostMapping
    public AjaxResult add(@Validated @RequestBody CesClearAddCmd addCmd) {
        cesClearService.add(addCmd);
        return AjaxResult.success();
    }

    @PutMapping
    public AjaxResult edit(@Validated @RequestBody CesClearEditCmd editCmd) {
        cesClearService.edit(editCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody CesClearRemoveCmd removeCmd) {
        cesClearService.remove(removeCmd);
        return AjaxResult.success();
    }
}
