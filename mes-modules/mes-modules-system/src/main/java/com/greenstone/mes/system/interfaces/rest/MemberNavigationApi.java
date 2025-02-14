package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.system.application.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

/**
 * @author wushaoqi
 * @date 2024-10-22-10:20
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberNavigationApi extends BaseController {
    private final PermissionService permissionService;

    @GetMapping(value = "/{memberId}/navigations/tree")
    public AjaxResult selectMemberNavigationTree(@PathVariable("memberId") @NotBlank(message = "请指定需要查询的成员") Long memberId) {
        return AjaxResult.success(permissionService.selectMemberNavigationTree(memberId));
    }
}
