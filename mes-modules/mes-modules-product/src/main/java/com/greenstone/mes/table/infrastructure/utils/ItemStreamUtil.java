package com.greenstone.mes.table.infrastructure.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.infrastructure.annotation.StreamField;
import com.greenstone.mes.table.infrastructure.constant.TableConst;
import com.greenstone.mes.table.infrastructure.persistence.ItemStream;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ItemStreamUtil {

    public static <E extends TableEntity> boolean equals(E entity, E oldEntity) {
        if (entity == null || oldEntity == null) return false;
        if (entity == oldEntity) return true;
        if (entity.getClass() != oldEntity.getClass()) return false;
        Field[] fields = ReflectUtil.getFields(entity.getClass());
        for (Field field : fields) {
            StreamField streamField = field.getDeclaredAnnotation(StreamField.class);
            if (streamField != null) {
                Object value = ReflectUtil.getFieldValue(entity, field);
                Object oldValue = ReflectUtil.getFieldValue(oldEntity, field);
                if (value == null && oldValue == null) continue;
                if (value == null || oldValue == null) return false;
                if (value instanceof List<?> list && oldValue instanceof List<?> oldList) {
                    if (list.size() != oldList.size()) return false;
                    if (list.isEmpty()) continue;
                    if (list.get(0) instanceof TableEntity) {
                        if (list.stream().anyMatch(e -> ((TableEntity) e).getId() == null)) return false;
                        for (Object o : list) {
                            TableEntity o1 = (TableEntity) o;
                            TableEntity o2 = (TableEntity) oldList.stream().filter(e -> ((TableEntity) e).getId().equals(o1.getId())).findFirst().orElse(null);
                            if (o2 == null) return false;
                            if (!equals(o1, o2)) return false;
                        }
                    } else {
                        if (!value.equals(oldValue)) return false;
                    }

                }
                if (!value.equals(oldValue)) return false;
            }
        }
        return true;
    }

    @SuppressWarnings({"unchecked"})
    public static <E extends TableEntity> List<ItemStream.Container> findDiffs(E entity, E oldEntity) {
        if (entity == null || oldEntity == null) {
            throw new RuntimeException("变更记录的对象不能为空");
        }
        List<ItemStream.Container> itemDiffs = new ArrayList<>();
        List<Field> annotatedFields = getAnnotatedFields(entity.getClass());
        for (Field annotatedField : annotatedFields) {
            Object value = ReflectUtil.getFieldValue(entity, annotatedField);
            Object oldValue = ReflectUtil.getFieldValue(oldEntity, annotatedField);
            if (value == null && oldValue == null) {
                continue;
            }
            if (value != null && value.equals(oldValue)) {
                continue;
            }
            if (ClassUtil.isBasicType(annotatedField.getType()) || annotatedField.getType() == String.class) {
                itemDiffs.add(diffField(value, oldValue, annotatedField));
            } else if (value != null ? value instanceof Collection<?> : oldValue instanceof Collection<?>) {
                itemDiffs.add(diffTable((Collection<E>) value, (Collection<E>) oldValue, annotatedField));
            } else {
                itemDiffs.add(diffField(value, oldValue, annotatedField));
            }
        }
        return itemDiffs;
    }

    private static <E extends TableEntity> ItemStream.Table diffTable(Collection<E> entities, Collection<E> oldEntities, Field field) {
        if (CollUtil.isNotEmpty(entities) && entities.stream().anyMatch(e -> e.getId() == null)) {
            throw new RuntimeException("新子表单 " + field.getAnnotation(StreamField.class).value() + " 中不能有id为空的数据");
        }
        if (CollUtil.isNotEmpty(oldEntities) && oldEntities.stream().anyMatch(e -> e.getId() == null)) {
            throw new RuntimeException("旧子表单 " + field.getAnnotation(StreamField.class).value() + " 中不能有id为空的数据");
        }

        Set<Serializable> ids = CollUtil.isEmpty(entities) ? new HashSet<>() : entities.stream().map(E::getId).collect(Collectors.toSet());
        Set<Serializable> oldIds = CollUtil.isEmpty(oldEntities) ? new HashSet<>() : oldEntities.stream().map(E::getId).collect(Collectors.toSet());
        List<E> newEntities = entities.stream().filter(e -> !oldIds.contains(e.getId())).toList();
        List<E> deleteEntities = oldEntities.stream().filter(e -> !ids.contains(e.getId())).toList();
        List<E> updateEntities = entities.stream().filter(e -> oldIds.contains(e.getId())).toList();

        List<ItemStream.Row> rows = new ArrayList<>();

        for (E newEntity : newEntities) {
            rows.add(newRow(newEntity, field));
        }

        for (E deleteEntity : deleteEntities) {
            rows.add(deleteRow(deleteEntity, field));
        }

        for (E updateEntity : updateEntities) {
            E oldEntity = oldEntities.stream().filter(old -> old.getId().equals(updateEntity.getId())).findFirst().orElse(null);
            rows.add(updateRow(updateEntity, oldEntity, field));
        }

        return ItemStream.Table.builder().rows(rows).label(field.getAnnotation(StreamField.class).value()).build();
    }

    private static <E extends TableEntity> ItemStream.Row newRow(E newEntity, Field field) {
        ItemStream.Row row = new ItemStream.Row();
        ItemStream.Fields siblingFields = siblingFields(newEntity, field);
        ItemStream.Fields diffFields = diffFields(newEntity, null, field);
        row.setDiffFields(diffFields);
        row.setSiblingFields(siblingFields);
        row.setItemId(newEntity.getId());
        row.setAction(TableConst.Rights.CREATE);
        return row;
    }

    private static <E extends TableEntity> ItemStream.Row deleteRow(E deleteEntity, Field field) {
        ItemStream.Row row = new ItemStream.Row();
        ItemStream.Fields siblingFields = siblingFields(deleteEntity, field);
        ItemStream.Fields diffFields = diffFields(null, deleteEntity, field);
        row.setDiffFields(diffFields);
        row.setSiblingFields(siblingFields);
        row.setItemId(deleteEntity.getId());
        row.setAction(TableConst.Rights.DELETE);
        return row;
    }

    private static <E extends TableEntity> ItemStream.Row updateRow(E entity, E oldEntity, Field field) {
        ItemStream.Row row = new ItemStream.Row();
        ItemStream.Fields siblingFields = siblingFields(entity, field);
        ItemStream.Fields diffFields = diffFields(entity, oldEntity, field);
        row.setDiffFields(diffFields);
        row.setSiblingFields(siblingFields);
        row.setItemId(entity.getId());
        row.setAction(TableConst.Rights.UPDATE);
        return row;
    }

    private static <E extends TableEntity> ItemStream.Fields siblingFields(E supportEntity, Field field) {
        return diffFields(supportEntity, null, field);
    }

    private static <E extends TableEntity> ItemStream.Fields diffFields(E entity, E oldEntity, Field field) {
        Class<?> clazz = entity == null ? oldEntity.getClass() : entity.getClass();
        List<ItemStream.Field> fields = new ArrayList<>();
        List<Field> annotatedFields = getAnnotatedFields(clazz);
        for (Field annotatedField : annotatedFields) {
            Object value = entity == null ? null : ReflectUtil.getFieldValue(entity, annotatedField);
            Object oldValue = oldEntity == null ? null : ReflectUtil.getFieldValue(oldEntity, annotatedField);
            if ((value != null && !value.equals(oldValue)) || (oldValue != null && !oldValue.equals(value))) {
                fields.add(diffField(value, oldValue, annotatedField));
            }
        }
        return ItemStream.Fields.builder().fields(fields).label(field.getAnnotation(StreamField.class).value()).build();
    }

    private static ItemStream.Field diffField(Object value, Object oldValue, Field annotatedField) {
        StreamField tableField = annotatedField.getAnnotation(StreamField.class);
        return ItemStream.Field.builder().value(value == null ? "" : getFieldStrValue(annotatedField, value))
                .oldValue(oldValue == null ? "" : getFieldStrValue(annotatedField, oldValue))
                .label(tableField.value()).build();
    }


    private static List<Field> getAnnotatedFields(Class<?> clazz) {
        List<Field> annotatedFields = new ArrayList<>();
        Field[] fields = ReflectUtil.getFields(clazz);
        for (Field field : fields) {
            if (field.isAnnotationPresent(StreamField.class)) {
                annotatedFields.add(field);
            }
        }
        return annotatedFields;
    }

    private static String getFieldStrValue(Field field, Object o) {
        if (o instanceof String s) {
            return s;
        } else if (o instanceof Iterable<?> iterable) {
            return CollUtil.join(iterable, ",");
        } else if (o instanceof LocalDateTime dateTime) {
            JsonFormat format = field.getDeclaredAnnotation(JsonFormat.class);
            return LocalDateTimeUtil.format(dateTime, StrUtil.isBlank(format.pattern()) ? "yyyy-MM-dd HH:mm:ss" : format.pattern());
        } else if (o instanceof LocalDate date) {
            JsonFormat format = field.getDeclaredAnnotation(JsonFormat.class);
            return LocalDateTimeUtil.format(date, StrUtil.isBlank(format.pattern()) ? "yyyy-MM-dd" : format.pattern());
        } else if (o instanceof Date date) {
            JsonFormat format = field.getDeclaredAnnotation(JsonFormat.class);
            return DateUtil.format(date, StrUtil.isBlank(format.pattern()) ? "yyyy-MM-dd HH:mm:ss" : format.pattern());
        } else {
            return String.valueOf(o);
        }
    }

}
