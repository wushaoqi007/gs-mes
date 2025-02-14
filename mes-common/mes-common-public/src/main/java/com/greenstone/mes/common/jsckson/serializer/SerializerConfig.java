package com.greenstone.mes.common.jsckson.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2023/2/10 16:42
 */
@RequiredArgsConstructor
@Component
public class SerializerConfig implements SmartInitializingSingleton {

    private final ObjectMapper objectMapper;

    @Override
    public void afterSingletonsInstantiated() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Enum.class, new EnumSerializer());
//        simpleModule.addSerializer(LocalDateTime.class, new MyLocalDateTimeSerializer());
//        simpleModule.addSerializer(LocalDate.class, new MyLocalDateSerializer());
        objectMapper.registerModule(simpleModule);
    }
}