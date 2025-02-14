package com.greenstone.mes.wxcp.interfaces.rest;


import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.wxcp.domain.service.WxClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/wxcp/client")
public class WxClientApi {

    private final WxClientService wxClientService;

    @PostMapping("/jsCpTicket")
    public AjaxResult jsCpTicket() {
        return AjaxResult.success(wxClientService.getJsCpTicket());
    }

    @PostMapping("/jsAgentTicket")
    public AjaxResult jsAgentTicket() {
        return AjaxResult.success(wxClientService.getJsAgentTicket());
    }

}
