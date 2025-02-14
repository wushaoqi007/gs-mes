package com.greenstone.mes.wxcp.domain.handler;

import com.greenstone.mes.mq.consts.MqConst;
import com.greenstone.mes.mq.producer.MsgProducer;
import com.greenstone.mes.wxcp.infrastructure.utils.TextBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 通讯录变更事件处理器.
 *
 * @author Binary Wang(https://github.com/binarywang)
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class ContactChangeHandler extends AbstractHandler {

    private final MsgProducer<WxCpXmlMessage> msgProducer;

    @Override
    public WxCpXmlOutMessage handle(WxCpXmlMessage wxMessage, Map<String, Object> context, WxCpService cpService,
                                    WxSessionManager sessionManager) {

        try {
            msgProducer.send(MqConst.Topic.WX_CALLBACK_CONTACT, wxMessage);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return new TextBuilder().build("received wx contact msg", wxMessage, cpService);
    }
}
