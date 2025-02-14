package com.greenstone.mes.machine.interfaces.mq;

import com.greenstone.mes.external.dto.result.MailSendResult;
import com.greenstone.mes.machine.application.service.MachineRequirementService;
import com.greenstone.mes.mq.consts.MqConst;
import com.greenstone.mes.system.consts.BusinessKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class ProductMailResultConsumer {
    private final MachineRequirementService requirementService;

    @KafkaListener(topics = MqConst.Topic.MAIL_SEND_RESULT, groupId = MqConst.Group.PRODUCT)
    public void onMessage(MailSendResult mailSendResult) {
        log.info("Receive msg, topic: {}, content: {}", MqConst.Topic.MAIL_SEND_RESULT, mailSendResult);
        if (BusinessKey.MACHINING_APPLY.equals(mailSendResult.getBusinessKey())) {
            requirementService.mailResult(mailSendResult);
        }
    }
}
