package com.greenstone.mes.ces.interfaces.web.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.ces.application.dto.cmd.ItemTypeAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.ItemTypeRemoveCmd;
import com.greenstone.mes.ces.application.service.ItemTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author wushaoqi
 * @date 2023-05-22-10:27
 */
@Slf4j
@RestController
@RequestMapping("/consumable/item/type")
public class ItemTypeApi extends BaseController {
    private final ItemTypeService itemTypeService;

    public ItemTypeApi(ItemTypeService itemTypeService) {
        this.itemTypeService = itemTypeService;
    }
    
    @GetMapping("/list")
    public AjaxResult list() {
        return AjaxResult.success(itemTypeService.list());
    }

    @PostMapping
    public AjaxResult add(@Validated @RequestBody ItemTypeAddCmd addCmd) {
        itemTypeService.add(addCmd);
        return AjaxResult.success();
    }

    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ItemTypeAddCmd addCmd) {
        itemTypeService.add(addCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody ItemTypeRemoveCmd removeCmd) {
        itemTypeService.remove(removeCmd.getTypeCode());
        return AjaxResult.success();
    }
}
