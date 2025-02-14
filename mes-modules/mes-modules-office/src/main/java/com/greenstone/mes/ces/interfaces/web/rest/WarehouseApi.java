package com.greenstone.mes.ces.interfaces.web.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseRemoveCmd;
import com.greenstone.mes.ces.application.dto.query.WarehouseFuzzyQuery;
import com.greenstone.mes.ces.application.service.WarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-06-01-15:11
 */
@Slf4j
@RestController
@RequestMapping("/consumable/warehouse")
public class WarehouseApi extends BaseController {

    private final WarehouseService warehouseService;

    public WarehouseApi(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }


    @GetMapping("/list")
    public AjaxResult search(WarehouseFuzzyQuery query) {
        List<String> fields = new ArrayList<>();
        fields.add("warehouseName");
        fields.add("warehouseCode");
        query.setFields(fields);
        return AjaxResult.success(warehouseService.search(query));
    }

    @PostMapping
    public AjaxResult add(@Validated @RequestBody WarehouseAddCmd addCmd) {
        warehouseService.add(addCmd);
        return AjaxResult.success();
    }

    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WarehouseEditCmd editCmd) {
        warehouseService.edit(editCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody WarehouseRemoveCmd removeCmd) {
        warehouseService.remove(removeCmd.getWarehouseCode());
        return AjaxResult.success();
    }
}
