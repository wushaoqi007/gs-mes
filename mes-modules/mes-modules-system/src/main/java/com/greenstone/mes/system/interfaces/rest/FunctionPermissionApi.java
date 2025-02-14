package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.system.application.dto.cmd.FunctionPermissionSaveCmd;
import com.greenstone.mes.system.application.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

/**
 * @author wushaoqi
 * @date 2024-10-22-10:20
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/functions")
public class FunctionPermissionApi extends BaseController {
    private final PermissionService permissionService;

    @GetMapping(value = "/{functionId}/permissions/withMembers")
    public AjaxResult selectFunctionPermissionsWithMembers(@PathVariable("functionId") @NotBlank(message = "请指定需要查询的功能") Long functionId) {
        return AjaxResult.success(permissionService.selectFunctionPermissionsWithMembers(functionId));
    }

    @ApiLog
    @PostMapping("/permissions/{functionPermissionId}/setPermissions")
    public AjaxResult setPermissions(@PathVariable("functionPermissionId") @NotBlank(message = "请指定权限组") Long functionPermissionId,
                                     @Validated @RequestBody FunctionPermissionSaveCmd saveCmd) {
        saveCmd.setFunctionPermissionId(functionPermissionId);
        permissionService.setFunctionPermissions(saveCmd);
        return AjaxResult.success("保存成功");
    }

    @PostMapping("/permissions/init")
    public AjaxResult initFunctionPerm() {
        permissionService.initFunctionPerm();
        return AjaxResult.success("初始化成功");
    }
}
