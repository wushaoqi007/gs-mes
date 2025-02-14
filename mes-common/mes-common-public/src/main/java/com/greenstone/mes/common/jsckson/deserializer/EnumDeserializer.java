package com.greenstone.mes.common.jsckson.deserializer;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author gu_renkai
 * @date 2023/2/10 16:18
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class EnumDeserializer extends JsonDeserializer<Enum<?>> implements ContextualDeserializer {

    private Class<?> target;

    @SneakyThrows
    @SuppressWarnings("all")
    @Override
    public Enum<?> deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
        if (!StringUtils.hasText(jsonParser.getText())) {
            return null;
        }
        if (Enum.class.isAssignableFrom(target)) {
            for (Field declaredField : target.getDeclaredFields()) {
                EnumValue annotation = declaredField.getAnnotation(EnumValue.class);
                if (annotation != null) {
                    declaredField.setAccessible(true);
                    for (Object enumConstant : target.getEnumConstants()) {
                        if (String.valueOf(declaredField.get(enumConstant)).equals(jsonParser.getText())) {
                            return (Enum) enumConstant;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param ctx      ctx
     * @param property property
     */
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctx, BeanProperty property) {
        Class<?> rawCls = ctx.getContextualType().getRawClass();
        EnumDeserializer enumDeserializer = new EnumDeserializer();
        enumDeserializer.setTarget(rawCls);
        return enumDeserializer;
    }

}