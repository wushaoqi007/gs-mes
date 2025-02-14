package com.greenstone.mes.common.jsckson.deserializer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author gu_renkai
 * @date 2023/2/10 16:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MyLocalDateTimeDeserializer extends LocalDateTimeDeserializer implements ContextualDeserializer {

    @SneakyThrows
    @SuppressWarnings("all")
    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
        if (_formatter != DateTimeFormatter.ISO_LOCAL_DATE_TIME) {
            return LocalDateTime.parse(jsonParser.getText(), _formatter);
        }
        if (StrUtil.isNumeric(jsonParser.getText())) {
            return Instant.ofEpochMilli(jsonParser.getLongValue() * 1000).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        }
        if (jsonParser.getText().length() == 19){
            return LocalDateTimeUtil.parse(jsonParser.getText(), "yyyy-MM-dd HH:mm:ss");
        }
        if (jsonParser.getText().length() == 10){
            return LocalDateTimeUtil.parse(jsonParser.getText(), "yyyy-MM-dd");
        }
        return super.deserialize(jsonParser, ctx);
    }

    /**
     * @param ctx      ctx
     * @param property property
     */
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctx, BeanProperty property) throws JsonMappingException {
        return super.createContextual(ctx, property);
    }

}