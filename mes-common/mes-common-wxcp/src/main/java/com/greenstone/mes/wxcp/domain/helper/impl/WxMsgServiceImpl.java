package com.greenstone.mes.wxcp.domain.helper.impl;

import com.greenstone.mes.wxcp.domain.helper.WxMsgService;
import com.greenstone.mes.wxcp.domain.helper.WxcpService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.infrastructure.config.WxConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpMessageService;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.springframework.stereotype.Service;

/**
 * @author gu_renkai
 * @date 2022/10/26 9:10
 */

@RequiredArgsConstructor
@Slf4j
@Service
public class WxMsgServiceImpl implements WxMsgService {

    private final WxcpService wxcpService;
    private final WxConfig wxConfig;

    @Override
    public void sendMsg(String agentName, WxCpMessage message) {
        try {
            Integer agentId = wxConfig.getAgentId(agentName);
            message.setAgentId(agentId);
            wxcpService.getWxCpService(agentName).getMessageService().send(message);
        } catch (WxErrorException e2) {
            log.error("send wx message error", e2);
        }
    }

    @Override
    public void sendMsg(CpId cpId, WxCpMessage message) {
        try {
            wxcpService.getMsgService(cpId).send(message);
        } catch (WxErrorException e2) {
            log.error("send wx message error", e2);
        }

    }

    @Override
    public void sendMsg(CpId cpId, Integer appId, WxCpMessage message) {
        try {
            me.chanjar.weixin.cp.api.WxCpService wxCpService = wxcpService.getWxCpService(cpId.id(), appId);
            WxCpMessageService messageService = wxCpService.getMessageService();
            messageService.send(message);
        } catch (WxErrorException e) {
            log.error("send wx message error", e);
        }

    }

    @Override
    public void updateTemplateCard(CpId cpId, Integer appId, String message) {
        me.chanjar.weixin.cp.api.WxCpService wxCpService = wxcpService.getWxCpService(cpId.id(), appId);
        try {
            wxCpService.post("https://qyapi.weixin.qq.com/cgi-bin/message/update_template_card", message);
        } catch (WxErrorException e) {
            log.error("updateTemplateCard error", e);
        }
    }

}
