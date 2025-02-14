package com.greenstone.mes.oa.interfaces.rest;

import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.oa.application.service.WxMsgSendService;
import com.greenstone.mes.oa.request.WxMsgSendCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/wx/cp/msg")
public class WxMsgController {


    private final WxMsgSendService wxMsgSendService;

    @Autowired
    public WxMsgController(WxMsgSendService wxMsgSendService) {
        this.wxMsgSendService = wxMsgSendService;
    }

    /**
     * 企业微信消息发送
     */
    @PostMapping("/send")
    public AjaxResult send(@RequestBody WxMsgSendCmd msgSendReq) {
        log.info("send msg to wx start");
        wxMsgSendService.sendMsgToWx(msgSendReq);
        return AjaxResult.success("发送成功");
    }

}
