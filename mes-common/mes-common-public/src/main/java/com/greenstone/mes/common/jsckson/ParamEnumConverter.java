package com.greenstone.mes.common.jsckson;

import com.baomidou.mybatisplus.annotation.EnumValue;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

@Component
public class ParamEnumConverter implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        Set<ConvertiblePair> convertiblePairs = new HashSet<>();
        convertiblePairs.add(new ConvertiblePair(String.class, Enum.class));
        return convertiblePairs;
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        Class<?> target = targetType.getType();
        if (Enum.class.isAssignableFrom(target)) {
            for (Field declaredField : target.getDeclaredFields()) {
                EnumValue annotation = declaredField.getAnnotation(EnumValue.class);
                if (annotation != null) {
                    declaredField.setAccessible(true);
                    for (Object enumConstant : target.getEnumConstants()) {
                        try {
                            if (String.valueOf(declaredField.get(enumConstant)).equals(source)) {
                                return enumConstant;
                            }
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        return null;
    }
}
