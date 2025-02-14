package com.greenstone.mes.common.jsckson.serializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gu_renkai
 * @date 2023/2/10 16:38
 */

@Slf4j
public class MyLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> implements ContextualSerializer {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Map<String, Map<String, JsonFormat>> formatMap = new HashMap<>();

    @SneakyThrows
    @Override
    public void serialize(LocalDateTime value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
        JsonFormat jsonFormat = null;
        if (serializerProvider.getGenerator().getCurrentValue() instanceof AjaxResult currentValue) {
            Map<String, JsonFormat> jsonFormatMap = formatMap.get(currentValue.get("data").getClass().getName());
            if (jsonFormatMap != null) {
                jsonFormat = jsonFormatMap.get(serializerProvider.getGenerator().getOutputContext().getCurrentName());
            }
        }
        if (jsonFormat != null && jsonFormat.shape().isNumeric()) {
            jsonGenerator.writeNumber(value.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() / 1000);
        } else if (jsonFormat != null && jsonFormat.pattern() != null) {
            jsonGenerator.writeString(value.format(DateTimeFormatter.ofPattern(jsonFormat.pattern())));
        } else {
            jsonGenerator.writeString(value.format(formatter));
        }
    }

    /**
     * @param prov     prov
     * @param property property
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        JsonFormat jsonFormat = property.getAnnotation(JsonFormat.class);
        if (jsonFormat != null) {
            if (prov.getGenerator().getCurrentValue() instanceof AjaxResult currentValue) {
                Map<String, JsonFormat> formatMap1 = formatMap.computeIfAbsent(currentValue.get("data").getClass().getName(),
                        aClass -> new HashMap<>());
                formatMap1.put(prov.getGenerator().getOutputContext().getCurrentName(), jsonFormat);
            }
        }
        return this;
    }
}