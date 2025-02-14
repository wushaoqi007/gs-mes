package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.system.application.dto.cmd.NavigationAddCmd;
import com.greenstone.mes.system.application.dto.cmd.NavigationMoveCmd;
import com.greenstone.mes.system.application.dto.result.NavigationResult;
import com.greenstone.mes.system.application.service.NavigationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-18-15:22
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/navigations")
public class NavigationsApi extends BaseController {
    private final NavigationService navigationService;

    /**
     * 获取导航列表
     */
    @GetMapping
    public AjaxResult list() {
        List<NavigationResult> results = navigationService.listAll();
        return AjaxResult.success(results);
    }

    /**
     * 获取导航树
     */
    @GetMapping("/tree")
    public AjaxResult tree() {
        List<NavigationResult> results = navigationService.listAll();
        return AjaxResult.success(navigationService.buildNavigationTree(results));
    }

    /**
     * 根据id获取详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult detail(@PathVariable("id") @NotBlank(message = "请指定需要查询的导航") Long id) {
        return AjaxResult.success(navigationService.detail(id));
    }

    @GetMapping(value = "/byFunction/{functionId}")
    public AjaxResult selectByFunctionId(@PathVariable("functionId") @NotBlank(message = "请指定需要查询的功能") Long functionId) {
        return AjaxResult.success(navigationService.selectByFunctionId(functionId));
    }

    @ApiLog
    @PostMapping
    public AjaxResult add(@Validated @RequestBody NavigationAddCmd addCmd) {
        addCmd.setId(null);
        navigationService.saveNavigation(addCmd);
        return AjaxResult.success("新增成功");
    }

    @ApiLog
    @PutMapping(value = "/{id}")
    public AjaxResult edit(@PathVariable("id") @NotBlank(message = "请指定需要编辑的导航") Long id, @Validated @RequestBody NavigationAddCmd updateCmd) {
        updateCmd.setId(id);
        navigationService.updateNavigation(updateCmd);
        return AjaxResult.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") @NotBlank(message = "请指定需要删除的导航") Long id) {
        navigationService.removeNavigation(id);
        return AjaxResult.success("删除成功");
    }

    @ApiLog
    @PutMapping("/move")
    public AjaxResult sort(@Validated @RequestBody NavigationMoveCmd sortCmd) {
        navigationService.moveNavigation(sortCmd);
        return AjaxResult.success("排序完成");
    }
}
