package com.greenstone.mes.wxcp.domain.service.impl;

import com.greenstone.mes.wxcp.domain.helper.WxcpService;
import com.greenstone.mes.wxcp.domain.service.WxClientService;
import com.greenstone.mes.wxcp.infrastructure.config.WxConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class WxClientServiceImpl implements WxClientService {

    private final WxcpService wxcpService;
    private final WxConfig wxConfig;

    @Override
    public String getJsCpTicket() {
        try {
            return wxcpService.getWxCpService(wxConfig.getDefaultCpId(), wxConfig.getAgentId(WxConfig.SYSTEM)).getJsapiTicket();
        } catch (WxErrorException e) {
            log.error("获取JsTicket失败", e);
            throw new RuntimeException("获取JsTicket失败：" + e.getMessage());
        }
    }

    @Override
    public String getJsAgentTicket() {
        try {
            return wxcpService.getWxCpService(wxConfig.getDefaultCpId(), wxConfig.getAgentId(WxConfig.SYSTEM)).getAgentJsapiTicket();
        } catch (WxErrorException e) {
            log.error("获取JsTicket失败", e);
            throw new RuntimeException("获取JsTicket失败：" + e.getMessage());
        }
    }

}
