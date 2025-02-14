package com.greenstone.mes.mq.producer;

import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.mq.utils.MqUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Slf4j
@Service
public class MsgProducerImpl<V> implements MsgProducer<V> {

    private final KafkaTemplate<String, V> kafkaTemplate;

    @Override
    public void send(String topic, V msg) throws ExecutionException, InterruptedException {
        ProducerRecord<String, V> pr = new ProducerRecord<>(topic, msg);
        pr.headers().add("type", msg.getClass().getName().getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(pr).get();
        log.info("Kafka msg send success: topic: {}, content{}", topic, JSON.toJSONString(msg));
    }

    @Override
    public void send(String topic, String tag, V msg) throws ExecutionException, InterruptedException {
        ProducerRecord<String, V> pr = new ProducerRecord<>(MqUtil.topicTag(topic, tag), msg);
        pr.headers().add("type", msg.getClass().getName().getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(pr).get();
        log.info("Kafka msg send success: topic: {}, content{}", MqUtil.topicTag(topic, tag), JSON.toJSONString(msg));
    }
}
