package com.greenstone.mes.ces.interfaces.web.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.ces.application.dto.cmd.OrderAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.OrderEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.OrderRemoveCmd;
import com.greenstone.mes.ces.application.dto.query.OrderFuzzyQuery;
import com.greenstone.mes.ces.application.dto.query.OrderQuery;
import com.greenstone.mes.ces.application.dto.result.OrderResult;
import com.greenstone.mes.ces.application.service.OrderService;
import com.greenstone.mes.ces.dto.cmd.OrderStatusChangeCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/consumable/order")
public class OrderApi extends BaseController {

    private final OrderService orderService;

    public OrderApi(OrderService orderService) {
        this.orderService = orderService;
    }

    @PutMapping("/statusChange")
    public AjaxResult statusChange(@Validated @RequestBody OrderStatusChangeCmd statusChangeCmd) {
        orderService.statusChange(statusChangeCmd);
        return AjaxResult.success();
    }

    @GetMapping
    public AjaxResult detail(@Validated OrderQuery query) {
        OrderResult detail = orderService.detail(query.getSerialNo());
        return AjaxResult.success(detail);
    }

    @GetMapping("/list")
    public TableDataInfo list(OrderFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(orderService.list(query));
    }

    @PostMapping
    public AjaxResult add(@Validated @RequestBody OrderAddCmd addCmd) {
        orderService.add(addCmd);
        return AjaxResult.success();
    }

    @PutMapping
    public AjaxResult edit(@Validated @RequestBody OrderEditCmd editCmd) {
        orderService.edit(editCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody OrderRemoveCmd removeCmd) {
        orderService.remove(removeCmd);
        return AjaxResult.success();
    }
}
