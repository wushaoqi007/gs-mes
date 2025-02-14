package com.greenstone.mes.wxcp.domain.handler;

import com.alibaba.fastjson2.JSON;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LogHandler extends AbstractHandler {
    @Override
    public WxCpXmlOutMessage handle(WxCpXmlMessage wxMessage, Map<String, Object> context, WxCpService cpService,
                                    WxSessionManager sessionManager) {
        this.logger.info("接收到请求消息，内容：\n{}", JSON.toJSONString(wxMessage));
        return null;
    }

}
