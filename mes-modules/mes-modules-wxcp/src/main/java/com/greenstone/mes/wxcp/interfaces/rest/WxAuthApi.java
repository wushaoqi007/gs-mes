package com.greenstone.mes.wxcp.interfaces.rest;

import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.wxcp.cmd.WorkwxOauth2Cmd;
import com.greenstone.mes.wxcp.domain.service.WxAuthService;
import com.greenstone.mes.wxcp.infrastructure.config.WxConfig;
import com.greenstone.mes.wxcp.interfaces.rest.resp.WxOauth2Config;
import com.greenstone.mes.wxcp.resp.WxOauth2Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/wxcp/oauth2")
public class WxAuthApi {

    private final WxAuthService wxAuthService;

    private final WxConfig wxConfig;

    @PostMapping
    public AjaxResult getUserByOauth2(@RequestBody WorkwxOauth2Cmd oauth2Cmd) {
        WxOauth2Resp oauth2User = wxAuthService.getOauth2User(oauth2Cmd);
        return AjaxResult.success(oauth2User);
    }

    @GetMapping("/defaultWxcpInfo")
    public AjaxResult config() {
        WxOauth2Config config = WxOauth2Config.builder().cpId(wxConfig.getDefaultCpId())
                .agentId(wxConfig.getDefaultAgentId())
                .state(wxConfig.getDefaultCpId() + "-" + wxConfig.getDefaultAgentId() + "-" + "web_login")
                .redirectUrl(wxConfig.getQrLoginRedirectUri()).build();
        return AjaxResult.success(config);
    }

}
