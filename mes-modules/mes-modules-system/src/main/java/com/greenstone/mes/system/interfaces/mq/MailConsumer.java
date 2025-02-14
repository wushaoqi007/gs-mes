package com.greenstone.mes.system.interfaces.mq;

import com.alibaba.fastjson.JSON;
import com.greenstone.mes.mq.consts.MqConst;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MailConsumer {

    private final UserService userService;

    @KafkaListener(topics = MqConst.Topic.MAIL_CREATE, groupId = MqConst.Group.SYSTEM)
    public void onMessage(User user) {
        log.info("Receive msg, topic: {}, content: {}", MqConst.Topic.MAIL_CREATE, JSON.toJSONString(user));
        userService.updateEmail(user);
    }
}
