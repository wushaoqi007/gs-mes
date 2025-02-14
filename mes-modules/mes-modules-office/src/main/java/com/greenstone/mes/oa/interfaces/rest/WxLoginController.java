package com.greenstone.mes.oa.interfaces.rest;

import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.oa.application.service.WxLoginService;
import com.greenstone.mes.wxcp.cmd.WorkwxOauth2Cmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author wushaoqi
 * @date 2023-07-13-8:57
 */
@Slf4j
@RestController
@RequestMapping("/wx/login")
public class WxLoginController {

    private final WxLoginService wxLoginService;

    public WxLoginController(WxLoginService wxLoginService) {
        this.wxLoginService = wxLoginService;
    }

    @GetMapping("/qrCode/{cpId}")
    public AjaxResult loginQrCode(@PathVariable("cpId") String cpId) {
        return AjaxResult.success(wxLoginService.getLoginQrCode(cpId));
    }

    @PostMapping("/user")
    public AjaxResult qrCodeCallback(@RequestBody WorkwxOauth2Cmd loginCallBackCmd) {
        return AjaxResult.success(wxLoginService.qrCodeLoginCallback(loginCallBackCmd));
    }
}
