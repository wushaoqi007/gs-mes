package com.greenstone.mes.market.interfaces.mq;

import com.greenstone.mes.market.application.service.MarketAppDataService;
import com.greenstone.mes.mq.consts.MqConst;
import com.greenstone.mes.system.consts.BusinessKey;
import com.greenstone.mes.workflow.mq.ApprovalChangeMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author wushaoqi
 * @date 2024-09-12-14:57
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MarketApprovalConsumer {
    private final MarketAppDataService marketAppDataService;

    @KafkaListener(topics = MqConst.Topic.FLOW_APPROVAL_CHANGE, groupId = MqConst.Group.OFFICE)
    public void onMessage(ApprovalChangeMsg approvalChangeMsg) {
        log.info("Receive msg, topic: {}, content: {}", MqConst.Topic.FLOW_APPROVAL_CHANGE, approvalChangeMsg);
        if (BusinessKey.MARKET_APPLY.equals(approvalChangeMsg.getBusinessKey())) {
            marketAppDataService.approval(approvalChangeMsg);
        }
    }
}
