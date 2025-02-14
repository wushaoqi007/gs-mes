package com.greenstone.mes.ces.interfaces.web.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseIORemoveCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseInAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseInEditCmd;
import com.greenstone.mes.ces.application.dto.query.WarehouseIOFuzzyQuery;
import com.greenstone.mes.ces.application.dto.query.WarehouseIOQuery;
import com.greenstone.mes.ces.application.dto.result.WarehouseInResult;
import com.greenstone.mes.ces.application.service.WarehouseInService;
import com.greenstone.mes.ces.dto.cmd.WarehouseIOStatusChangeCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/consumable/warehouse/in")
public class WarehouseInApi extends BaseController {

    private final WarehouseInService warehouseInService;

    public WarehouseInApi(WarehouseInService warehouseInService) {
        this.warehouseInService = warehouseInService;
    }

    @PutMapping("/statusChange")
    public AjaxResult statusChange(@Validated @RequestBody WarehouseIOStatusChangeCmd statusChangeCmd) {
        warehouseInService.statusChange(statusChangeCmd);
        return AjaxResult.success();
    }

    @GetMapping
    public AjaxResult detail(@Validated WarehouseIOQuery query) {
        WarehouseInResult detail = warehouseInService.detail(query.getSerialNo());
        return AjaxResult.success(detail);
    }

    @GetMapping("/list")
    public TableDataInfo list(WarehouseIOFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(warehouseInService.list(query));
    }

    @PostMapping
    public AjaxResult add(@Validated @RequestBody WarehouseInAddCmd addCmd) {
        warehouseInService.add(addCmd);
        return AjaxResult.success();
    }

    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WarehouseInEditCmd editCmd) {
        warehouseInService.edit(editCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody WarehouseIORemoveCmd removeCmd) {
        warehouseInService.remove(removeCmd);
        return AjaxResult.success();
    }
}
