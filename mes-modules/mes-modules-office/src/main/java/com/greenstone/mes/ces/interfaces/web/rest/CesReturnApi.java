package com.greenstone.mes.ces.interfaces.web.rest;

import com.greenstone.mes.ces.application.dto.cmd.CesReturnAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.CesReturnRemoveCmd;
import com.greenstone.mes.ces.application.dto.query.CesReturnFuzzyQuery;
import com.greenstone.mes.ces.application.dto.query.CesReturnQuery;
import com.greenstone.mes.ces.application.dto.result.CesReturnResult;
import com.greenstone.mes.ces.application.service.CesReturnService;
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
@RequestMapping("/consumable/return")
public class CesReturnApi extends BaseController {

    private final CesReturnService cesReturnService;

    public CesReturnApi(CesReturnService cesReturnService) {
        this.cesReturnService = cesReturnService;
    }

    @GetMapping
    public AjaxResult detail(@Validated CesReturnQuery query) {
        CesReturnResult detail = cesReturnService.detail(query.getSerialNo());
        return AjaxResult.success(detail);
    }

    @GetMapping("/list")
    public TableDataInfo list(CesReturnFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(cesReturnService.list(query));
    }

    @GetMapping("/item/list")
    public TableDataInfo itemList(CesReturnFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(cesReturnService.itemList(query));
    }

    @PostMapping
    public AjaxResult add(@Validated @RequestBody CesReturnAddCmd addCmd) {
        cesReturnService.add(addCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody CesReturnRemoveCmd removeCmd) {
        cesReturnService.remove(removeCmd);
        return AjaxResult.success();
    }
}
