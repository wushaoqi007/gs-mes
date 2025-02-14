package com.greenstone.mes.ces.interfaces.web.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.ces.application.dto.query.WarehouseStockFuzzyQuery;
import com.greenstone.mes.ces.application.service.WarehouseStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wushaoqi
 * @date 2023-06-08-11:30
 */
@Slf4j
@RestController
@RequestMapping("/consumable/warehouse/stock")
public class WarehouseStockApi extends BaseController {

    private final WarehouseStockService stockService;

    public WarehouseStockApi(WarehouseStockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/check")
    public AjaxResult check() {
        stockService.checkStock();
        return AjaxResult.success();
    }

    @GetMapping("/list")
    public TableDataInfo search(WarehouseStockFuzzyQuery query) {
        startPage();
        return getDataTable(stockService.list(query));
    }
}
