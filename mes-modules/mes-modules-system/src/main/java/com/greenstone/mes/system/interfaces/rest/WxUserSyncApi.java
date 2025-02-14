package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.system.domain.service.SysUserService;
import com.greenstone.mes.system.interfaces.rest.cmd.WxOauth2SyncCmd;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/wxcp")
public class WxUserSyncApi {

    private final SysUserService sysUserService;

    @ApiLog
    @PostMapping("/oauth2/sync")
    public AjaxResult commit(@RequestBody @Validated WxOauth2SyncCmd wxOauth2SyncCmd) {
        sysUserService.updateUserByWxOauth2(wxOauth2SyncCmd.getCode(), wxOauth2SyncCmd.getState());
        return AjaxResult.success();
    }

}
