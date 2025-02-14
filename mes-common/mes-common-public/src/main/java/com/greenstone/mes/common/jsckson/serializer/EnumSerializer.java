package com.greenstone.mes.common.jsckson.serializer;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

/**
 * @author gu_renkai
 * @date 2023/2/10 16:38
 */

public class EnumSerializer extends JsonSerializer<Enum> {
    @SneakyThrows
    @Override
    public void serialize(Enum ienum, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
        for (Field declaredField : ienum.getClass().getDeclaredFields()) {
            EnumValue annotation = declaredField.getAnnotation(EnumValue.class);
            if (annotation != null) {
                declaredField.setAccessible(true);
                jsonGenerator.writeNumber(declaredField.getInt(ienum));
            }
        }
    }
}