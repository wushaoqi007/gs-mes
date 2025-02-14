package com.greenstone.mes.common.mybatisplus;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

public class WrappersPlus<T> {

    public static <T> QueryWrapper<T> query(T entity) {
        QueryWrapper<T> queryWrapper = Wrappers.query();
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object v = field.get(entity);
                if (nonNull(v)) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    String fieldName = null;
                    TableField tableField = field.getDeclaredAnnotation(TableField.class);
                    if (tableField != null) {
                        if (!tableField.exist()) {
                            continue;
                        }
                        if (StrUtil.isNotBlank(tableField.value())) {
                            fieldName = tableField.value();
                        }
                    }
                    if (fieldName == null) {
                        fieldName = StrUtil.toUnderlineCase(field.getName());
                    }

                    QueryCompare queryCompare = field.getDeclaredAnnotation(QueryCompare.class);
                    if (queryCompare != null) {
                        if (queryCompare.value() == CompareType.LIKE) {
                            queryWrapper.like(fieldName, v);
                        }
                    } else {
                        queryWrapper.eq(fieldName, v);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return queryWrapper;
    }

    private static boolean nonNull(Object o) {
        if (o instanceof String) {
            return StrUtil.isNotEmpty((String) o);
        }
        return Objects.nonNull(o);
    }

}
