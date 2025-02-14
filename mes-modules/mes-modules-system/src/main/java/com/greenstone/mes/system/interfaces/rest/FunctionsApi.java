package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.system.application.dto.cmd.FunctionAddCmd;
import com.greenstone.mes.system.application.dto.cmd.FunctionMoveCmd;
import com.greenstone.mes.system.dto.result.FunctionResult;
import com.greenstone.mes.system.application.service.FunctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-18-9:22
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/functions")
public class FunctionsApi extends BaseController {
    private final FunctionService functionService;

    /**
     * 获取功能列表
     */
    @GetMapping
    public AjaxResult list() {
        List<FunctionResult> results = functionService.listAll();
        return AjaxResult.success(results);
    }

    /**
     * 根据id获取详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult detail(@PathVariable("id") @NotBlank(message = "请指定需要查询的功能") Long id) {
        return AjaxResult.success(functionService.detail(id));
    }

    @ApiLog
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FunctionAddCmd addCmd) {
        addCmd.setId(null);
        functionService.saveFunction(addCmd);
        return AjaxResult.success("新增成功");
    }

    @ApiLog
    @PutMapping(value = "/{id}")
    public AjaxResult edit(@PathVariable("id") @NotBlank(message = "请指定需要编辑的功能") Long id, @Validated @RequestBody FunctionAddCmd updateCmd) {
        updateCmd.setId(id);
        functionService.updateFunction(updateCmd);
        return AjaxResult.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") @NotBlank(message = "请指定需要删除的功能") Long id) {
        functionService.removeFunction(id);
        return AjaxResult.success("删除成功");
    }

    @ApiLog
    @PutMapping("/move")
    public AjaxResult sort(@Validated @RequestBody FunctionMoveCmd sortCmd) {
        functionService.moveFunction(sortCmd);
        return AjaxResult.success("排序完成");
    }

    @GetMapping("/withPermission")
    public AjaxResult listAllFunctionWithPerm() {
        return AjaxResult.success(functionService.listAllFunctionWithPerm());
    }
}
