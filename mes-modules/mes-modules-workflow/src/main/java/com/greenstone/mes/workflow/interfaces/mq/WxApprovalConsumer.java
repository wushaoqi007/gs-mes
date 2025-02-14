package com.greenstone.mes.workflow.interfaces.mq;

import com.alibaba.fastjson.JSON;
import com.greenstone.mes.mq.consts.MqConst;
import com.greenstone.mes.workflow.cache.FlowCache;
import com.greenstone.mes.workflow.domain.service.FlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class WxApprovalConsumer {

    private final FlowService flowService;
    private final FlowCache flowCache;

    @Transactional
    @KafkaListener(topics = MqConst.Topic.WX_CALLBACK_APPROVAL, groupId = MqConst.Group.WORKFLOW)
    public void onMessage(WxCpXmlMessage cpXmlMessage) {
        log.info("Receive msg, topic: {}, content: {}", MqConst.Topic.WX_CALLBACK_APPROVAL, JSON.toJSONString(cpXmlMessage));
        if (flowCache.getProcess().stream().anyMatch(p -> p.getProcessKey().equals(cpXmlMessage.getApprovalInfo().getTemplateId()))) {
            flowService.wxApprovalChange(cpXmlMessage);
        }
    }
}