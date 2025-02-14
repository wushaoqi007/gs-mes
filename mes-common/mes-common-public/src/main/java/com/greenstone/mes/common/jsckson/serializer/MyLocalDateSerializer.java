package com.greenstone.mes.common.jsckson.serializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.SneakyThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author gu_renkai
 * @date 2023/2/10 16:38
 */

public class MyLocalDateSerializer extends LocalDateSerializer implements ContextualSerializer {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    JsonFormat format;

    @SneakyThrows
    @Override
    public void serialize(LocalDate value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
        if (format != null && format.shape() != null && format.shape().isNumeric()) {
            jsonGenerator.writeNumber(value.toEpochSecond(LocalTime.of(0, 0, 0), ZoneOffset.ofHours(8)));
        } else {
            jsonGenerator.writeString(value.format(formatter));
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        this.format = property.getAnnotation(JsonFormat.class);
        return this;
    }
}