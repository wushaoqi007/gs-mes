package com.greenstone.mes.wxcp.domain.handler;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.redis.service.RedisLock;
import com.greenstone.mes.mq.consts.MqConst;
import com.greenstone.mes.mq.producer.MsgProducer;
import com.greenstone.mes.wxcp.domain.helper.WxMsgService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class MenuHandler extends AbstractHandler {

    private final MsgProducer<WxCpXmlMessage> msgProducer;
    private final RedisLock redisLock;
    private final WxMsgService wxMsgService;

    @Override
    public WxCpXmlOutMessage handle(WxCpXmlMessage wxMessage, Map<String, Object> context, WxCpService cpService,
                                    WxSessionManager sessionManager) {
        // 处理菜单点击事件
        // 所有的事件都有eventKey，格式为 消息类型@业务数据
        // eventKey 样例：meal_report@lunch
        // 其中 业务类型（meal_report）作为消息的tag

        // taskId不能是空的
        if (StrUtil.isBlank(wxMessage.getEventKey())) {
            log.error("Click menu eventKey can not be empty");
            return null;
        }

        // taskId 必须包含@来分割业务类型
        if (!wxMessage.getEventKey().contains("@")) {
            log.error("Click menu eventKey is incorrect: {}", wxMessage.getEventKey());
            return null;
        }

        String lockKey = wxMessage.getEventKey() + wxMessage.getFromUserName();
        if (!redisLock.lock(lockKey, 10)) {
            CpId cpId = new CpId(wxMessage.getToUserName());
            Integer agentId = Integer.valueOf(wxMessage.getAgentId());
            wxMsgService.sendMsg(cpId, agentId, WxCpMessage.TEXT().toUser(wxMessage.getFromUserName()).content("处理中，请稍后。").build());
            return WxCpXmlOutMessage.TEXT().content(null)
                    .fromUser(wxMessage.getToUserName()).toUser(wxMessage.getFromUserName())
                    .build();
        }

        String tag = wxMessage.getEventKey().substring(0, wxMessage.getEventKey().indexOf("@"));
        try {
            msgProducer.send(MqConst.TopicPrefix.WXCP_CALLBACK_TOPIC, tag, wxMessage);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }


}
