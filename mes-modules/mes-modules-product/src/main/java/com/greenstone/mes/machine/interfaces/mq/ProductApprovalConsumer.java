package com.greenstone.mes.machine.interfaces.mq;

import com.greenstone.mes.machine.application.service.MachineRequirementService;
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
public class ProductApprovalConsumer {
    private final MachineRequirementService requirementService;

//    @KafkaListener(topics = MqConst.Topic.FLOW_APPROVAL_CHANGE, groupId = MqConst.Group.PRODUCT)
    public void onMessage(ApprovalChangeMsg approvalChangeMsg) {
        log.info("Receive msg, topic: {}, content: {}", MqConst.Topic.FLOW_APPROVAL_CHANGE, approvalChangeMsg);
        if (BusinessKey.MACHINING_APPLY.equals(approvalChangeMsg.getBusinessKey())) {
            requirementService.approval(approvalChangeMsg);
        }
    }
}
