package com.greenstone.mes.mq.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

@Data
@Component
public class MqConfig {

    @Value("${spring.application.name}")
    private String appName;

    @Bean
    public CommonErrorHandler commonErrorHandler() {
        BackOff backOff = new FixedBackOff(5000L, 0L);
        return new DefaultErrorHandler(backOff);
    }

}
