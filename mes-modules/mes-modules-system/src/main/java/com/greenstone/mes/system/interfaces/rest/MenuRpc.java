package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.system.domain.service.MenuService;
import com.greenstone.mes.system.dto.cmd.CustomFormMenuAddCmd;
import com.greenstone.mes.system.dto.cmd.CustomFormMenuEditCmd;
import com.greenstone.mes.system.dto.result.FormDefinitionVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 菜单信息
 *
 * @author ruoyi
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/menu")
public class MenuRpc extends BaseController {

    private final MenuService menuService;

    @PostMapping("/form/custom")
    public AjaxResult addCustomFormMenu(@RequestBody CustomFormMenuAddCmd addCmd) {
        menuService.addCustomMenu(addCmd);
        return AjaxResult.success();
    }

    @GetMapping("/{menuId}/form/definition")
    public AjaxResult getFormDefinition(@PathVariable("menuId") String menuId) {
        FormDefinitionVo formDefinition = menuService.getFormDefinition(menuId);
        return AjaxResult.success(formDefinition);
    }

    @PutMapping("/form/custom")
    public AjaxResult editCustomFormMenu(@RequestBody CustomFormMenuEditCmd editCmd) {
        menuService.editCustomMenu(editCmd);
        return AjaxResult.success();
    }

}