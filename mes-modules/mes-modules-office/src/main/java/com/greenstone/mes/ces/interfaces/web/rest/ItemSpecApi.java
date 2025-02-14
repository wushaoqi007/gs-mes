package com.greenstone.mes.ces.interfaces.web.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.ces.application.dto.cmd.ItemSpecAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.ItemSpecEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.ItemSpecRemoveCmd;
import com.greenstone.mes.ces.application.dto.query.ItemSpecFuzzyQuery;
import com.greenstone.mes.ces.application.dto.query.ItemSpecQuery;
import com.greenstone.mes.ces.application.service.ItemSpecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-22-15:11
 */
@Slf4j
@RestController
@RequestMapping("/consumable/item/specification")
public class ItemSpecApi extends BaseController {

    private final ItemSpecService itemSpecService;

    public ItemSpecApi(ItemSpecService itemSpecService) {
        this.itemSpecService = itemSpecService;
    }

    @GetMapping("/list")
    public AjaxResult list(ItemSpecQuery query) {
        return AjaxResult.success(itemSpecService.list(query));
    }

    @GetMapping("/search")
    public AjaxResult search(ItemSpecFuzzyQuery query) {
        List<String> fields = new ArrayList<>();
        fields.add("itemName");
        fields.add("itemCode");
        query.setFields(fields);
        return AjaxResult.success(itemSpecService.search(query));
    }

    @PostMapping
    public AjaxResult add(@Validated @RequestBody ItemSpecAddCmd addCmd) {
        itemSpecService.add(addCmd);
        return AjaxResult.success();
    }

    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ItemSpecEditCmd editCmd) {
        itemSpecService.edit(editCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody ItemSpecRemoveCmd removeCmd) {
        itemSpecService.remove(removeCmd);
        return AjaxResult.success();
    }
}
