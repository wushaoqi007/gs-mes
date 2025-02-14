package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.system.application.dto.cmd.MemberPermissionSaveCmd;
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
@RequestMapping("/members")
public class MemberPermissionApi extends BaseController {
    private final PermissionService permissionService;

    @GetMapping(value = "/{memberId}/permissions")
    public AjaxResult selectMemberPermissions(@PathVariable("memberId") @NotBlank(message = "请指定需要查询的成员") Long memberId) {
        return AjaxResult.success(permissionService.selectMemberPermissions(memberId));
    }

    @ApiLog
    @PostMapping("/{memberId}/{memberType}/setPermissions")
    public AjaxResult setPermissions(@PathVariable("memberId") @NotBlank(message = "请指定成员") Long memberId,
                                     @PathVariable("memberType") @NotBlank(message = "请指定成员类型") String memberType,
                                     @Validated @RequestBody MemberPermissionSaveCmd saveCmd) {
        saveCmd.setMemberId(memberId);
        saveCmd.setMemberType(memberType);
        permissionService.setMemberPermissions(saveCmd);
        return AjaxResult.success("保存成功");
    }

    @ApiLog
    @DeleteMapping("/{memberId}/permissions/{functionPermissionId}")
    public AjaxResult removePermissions(@PathVariable("memberId") @NotBlank(message = "请指定要删除权限的成员") Long memberId,
                                        @PathVariable("functionPermissionId") @NotBlank(message = "请指定要删除的权限") Long functionPermissionId) {
        permissionService.removePermission(memberId, functionPermissionId);
        return AjaxResult.success("删除成功");
    }
}
