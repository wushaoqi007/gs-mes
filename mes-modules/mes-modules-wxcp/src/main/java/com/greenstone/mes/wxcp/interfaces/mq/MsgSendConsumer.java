package com.greenstone.mes.wxcp.interfaces.mq;

import com.alibaba.fastjson.JSON;
import com.greenstone.mes.mq.consts.MqConst;
import com.greenstone.mes.mq.dto.wxcp.WxcpMsgDto;
import com.greenstone.mes.wxcp.domain.helper.WxMsgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MsgSendConsumer {

    private final WxMsgService wxMsgService;

    @KafkaListener(topics = MqConst.Topic.WX_MSGSEND, groupId = MqConst.GROUP)
    public void onMessage(WxcpMsgDto msgDto) {
        log.info("Receive msg, topic: {}, content: {}", MqConst.Topic.WX_MSGSEND, JSON.toJSONString(msgDto));

        wxMsgService.sendMsg(msgDto.getAgentName(), msgDto.getMessage());

    }

}
