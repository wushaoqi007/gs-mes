package com.greenstone.mes.ces.interfaces.web.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseIORemoveCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseOutAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseOutEditCmd;
import com.greenstone.mes.ces.application.dto.query.WarehouseIOFuzzyQuery;
import com.greenstone.mes.ces.application.dto.query.WarehouseIOQuery;
import com.greenstone.mes.ces.application.dto.result.WarehouseOutResult;
import com.greenstone.mes.ces.application.service.WarehouseOutService;
import com.greenstone.mes.ces.dto.cmd.WarehouseIOStatusChangeCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/consumable/warehouse/out")
public class WarehouseOutApi extends BaseController {

    private final WarehouseOutService warehouseOutService;

    public WarehouseOutApi(WarehouseOutService warehouseOutService) {
        this.warehouseOutService = warehouseOutService;
    }

    @PutMapping("/statusChange")
    public AjaxResult statusChange(@Validated @RequestBody WarehouseIOStatusChangeCmd statusChangeCmd) {
        warehouseOutService.statusChange(statusChangeCmd);
        return AjaxResult.success();
    }

    @GetMapping
    public AjaxResult detail(@Validated WarehouseIOQuery query) {
        WarehouseOutResult detail = warehouseOutService.detail(query.getSerialNo());
        return AjaxResult.success(detail);
    }

    @GetMapping("/list")
    public TableDataInfo list(WarehouseIOFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("recipientName");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(warehouseOutService.list(query));
    }

    @PostMapping
    public AjaxResult add(@Validated @RequestBody WarehouseOutAddCmd addCmd) {
        warehouseOutService.add(addCmd);
        return AjaxResult.success();
    }

    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WarehouseOutEditCmd editCmd) {
        warehouseOutService.edit(editCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody WarehouseIORemoveCmd removeCmd) {
        warehouseOutService.remove(removeCmd);
        return AjaxResult.success();
    }
}
