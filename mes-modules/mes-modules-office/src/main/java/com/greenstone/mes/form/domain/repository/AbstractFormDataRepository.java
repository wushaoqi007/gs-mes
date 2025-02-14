package com.greenstone.mes.form.domain.repository;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.form.domain.BaseFormDataEntity;
import com.greenstone.mes.form.domain.entity.FieldCondition;
import com.greenstone.mes.form.domain.helper.FormHelper;
import com.greenstone.mes.form.dto.query.FormDataQuery;
import com.greenstone.mes.form.infrastructure.enums.ConditionType;
import com.greenstone.mes.form.infrastructure.persistence.BaseFormPo;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 默认提供的表单数据仓库类
 *
 * @param <E> 实例类型
 * @param <P> 持久化类型
 * @param <M> Mapper类型
 */
@Slf4j
public abstract class AbstractFormDataRepository<E extends BaseFormDataEntity, P extends BaseFormPo, M extends BaseMapper<P>> {

    private static final String PO_TYPE_NAME = "P";

    private static final String ENTITY_TYPE_NAME = "E";

    private final FormHelper formHelper;

    private final M mapper;

    public AbstractFormDataRepository(FormHelper formHelper, M mapper) {
        this.formHelper = formHelper;
        this.mapper = mapper;
    }

    public E getEntityById(String formId, Serializable id) {
        P po = getPoById(formId, id);
        return BeanUtil.toBean(po, getEntityClass());
    }

    public E getEntityById(String formId, Serializable id, Function<P, E> convert) {
        P po = getPoById(formId, id);
        return convert.apply(po);
    }

    public <R> R getResultById(String formId, Serializable id, Function<P, R> convert) {
        P po = getPoById(formId, id);
        return convert.apply(po);
    }

    public P getPoById(String formId, Serializable id) {
        P p;
        try {
            setTableName(formId);
            p = mapper.selectById(id);
        } finally {
            formHelper.clearFormDataTableName();
        }
        return p;
    }

    public E getEntityBySerialNo(String formId, String serialNo) {
        P po = getPoBySerialNo(formId, serialNo);
        return BeanUtil.toBean(po, getEntityClass());
    }

    public E getEntityBySerialNo(String formId, String serialNo, Function<P, E> convert) {
        P po = getPoBySerialNo(formId, serialNo);
        return convert.apply(po);
    }

    public P getPoBySerialNo(String formId, String serialNo) {
        P p;
        try {
            setTableName(formId);
            QueryWrapper<P> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("serial_no", serialNo);
            p = mapper.selectOne(queryWrapper);
        } finally {
            formHelper.clearFormDataTableName();
        }
        return p;
    }

    public List<E> queryEntityList(FormDataQuery query) {
        List<P> poList = queryPoList(query);
        Class<E> entityClass;
        try {
            entityClass = getEntityClass();
        } catch (Throwable e) {
            log.error("服务器内部错误", e);
            throw new RuntimeException("服务器内部错误");
        }
        return BeanUtil.copyToList(poList, entityClass);
    }

    public List<E> queryEntityList(FormDataQuery query, Function<List<P>, List<E>> convert) {
        List<P> poList = queryPoList(query);
        return convert.apply(poList);
    }

    public <R> List<R> queryResultList(FormDataQuery query, Function<List<P>, List<R>> convert) {
        List<P> poList = queryPoList(query);
        return convert.apply(poList);
    }

    /**
     * 查询自定义表
     */
    public List<P> queryPoList(FormDataQuery query) {
        QueryWrapper<P> queryWrapper = new QueryWrapper<>();
        // 排序
        {
            String orderBy = query.getOrderBy();
            if (StrUtil.isBlank(orderBy) || "-".equals(orderBy)) {
                orderBy = "-createTime";
            }
            boolean isDesc = orderBy.startsWith("-");
            if (isDesc) {
                orderBy = orderBy.substring(1);
            }
            boolean isExtField = isExtField(orderBy);
            String field = isExtField ? "JSON_EXTRACT(data_json, \"$." + orderBy + "\")" : StrUtil.toUnderlineCase(orderBy);
            if (isDesc) {
                queryWrapper.orderByDesc(field);
            } else {
                queryWrapper.orderByAsc(field);
            }
        }
        // 模糊查询
        if (StrUtil.isNotBlank(query.getFuzzyFields()) && StrUtil.isNotBlank(query.getFuzzyKeyword())) {
            String[] fuzzyFields = query.getFuzzyFields().split(",");
            queryWrapper.and(wrapper -> {
                for (String fuzzyField : fuzzyFields) {
                    if (StrUtil.isNotBlank(fuzzyField)) {
                        boolean isExtField = isExtField(fuzzyField);
                        String field = isExtField ? fuzzyField : StrUtil.toUnderlineCase(fuzzyField);
                        if (isExtField) {
                            wrapper.or().like("data_json -> '$." + field + "'", query.getFuzzyKeyword());
                        } else {
                            wrapper.or().like(field, query.getFuzzyKeyword());
                        }
                    }

                }
            });
        }
        // 高级查询
        if (StrUtil.isNotBlank(query.getConditions())) {
            List<FieldCondition> fieldConditions = JSON.parseArray(query.getConditions(), FieldCondition.class);
            for (FieldCondition condition : fieldConditions) {
                ConditionType type;
                try {
                    type = ConditionType.getType(condition.getType());
                    boolean isExtField = isExtField(condition.getField());
                    String field = isExtField ? condition.getField() : StrUtil.toUnderlineCase(condition.getField());
                    String express = type.queryCondition(field, condition.getType(), condition.getValue(), isExtField);
                    if (StrUtil.isNotBlank(express)) {
                        queryWrapper.apply(express);
                    }
                } catch (IllegalArgumentException e) {
                    log.error(StrUtil.format("无法根据字段'{}'进行过滤", condition.getLabel()));
                }
            }
        }
        // 设置动态表名，执行查询
        List<P> queryData;
        try {
            setTableName(query.getFormId());
            queryData = mapper.selectList(queryWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            formHelper.clearFormDataTableName();
        }
        return queryData;
    }

    public P saveEntity(E entity) {
        try {
            Class<P> poClass = getPoClass();
            P po = poClass.getDeclaredConstructor().newInstance();
            TableName tableName = poClass.getAnnotation(TableName.class);
            if (tableName == null) {
                formHelper.setFormDataTableName(entity.getFormId());
            }
            // 更新单据
            if (StrUtil.isNotBlank(entity.getId())) {
                QueryWrapper<P> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("id", entity.getId());
                boolean exists = mapper.exists(queryWrapper);
                if (!exists) {
                    throw new RuntimeException("选择的单据不存在");
                } else {
                    setPoFieldValue(po, entity);
                    mapper.updateById(po);
                }
            } else {
                // 新增单据
                setPoFieldValue(po, entity);
                mapper.insert(po);
            }
            return po;
        } catch (Exception e) {
            log.error("内部错误", e);
            throw new RuntimeException("内部错误，请重试。");
        } finally {
            formHelper.clearFormDataTableName();
        }
    }

    public void changeStatusBySerialNo(String serialNo, ProcessStatus status) {
        // TODO 待校验
        UpdateWrapper<P> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", status);
        updateWrapper.eq("serial_no", serialNo);
        mapper.update(null, updateWrapper);
    }

    public void deleteByIds(String formId, List<String> ids) {
        autoSwitchTable(formId, () -> {
            QueryWrapper<P> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", ids);
            queryWrapper.ne("status", ProcessStatus.DRAFT);
            if (mapper.exists(queryWrapper)) {
                throw new RuntimeException("只能删除草稿状态的单据。");
            }
            return mapper.deleteBatchIds(ids);
        });
    }

    public void revokeByIds(String formId, List<String> ids) {
        autoSwitchTable(formId, () -> {
            QueryWrapper<P> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", ids);
            queryWrapper.eq("status", ProcessStatus.APPROVED);
            if (mapper.exists(queryWrapper)) {
                throw new RuntimeException("不能撤回已审批的单据。");
            }
            UpdateWrapper<P> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("status", ProcessStatus.REVOKED);
            updateWrapper.in("id", ids);
            return mapper.update(null, updateWrapper);
        });
    }

    private void setPoFieldValue(P po, E entity) {
        Field[] entityFields = entity.getClass().getDeclaredFields();
        Field[] poFields = po.getClass().getDeclaredFields();
        for (Field entityField : entityFields) {
            Arrays.stream(poFields).filter(poField -> poField.getName().equals(entityField.getName())).findFirst()
                    .ifPresent(field -> {
                        field.setAccessible(true);
                        try {
                            field.set(po, entityField.get(entity));
                        } catch (IllegalAccessException e) {
                            log.error("系统内部错误", e);
                            throw new RuntimeException("系统内部错误");
                        }
                    });
        }
    }

    @SuppressWarnings("unchecked")
    private Class<P> getPoClass() {
        Class<P> poClass = null;
        try {
            poClass = (Class<P>) TypeUtil.getTypeMap(this.getClass()).entrySet().stream()
                    .filter(entry -> entry.getKey().getTypeName().equals(PO_TYPE_NAME))
                    .map(Map.Entry::getValue).findFirst().orElseThrow((Supplier<Throwable>) () -> new RuntimeException("无法获取DataRepository的持久化对象类型"));
        } catch (Throwable e) {
            log.error("服务器内部错误", e);
            throw new RuntimeException("内部错误，请稍后重试");
        }
        return poClass;
    }

    @SuppressWarnings("unchecked")
    private Class<E> getEntityClass() {
        Class<E> eneityClass;
        try {
            eneityClass = (Class<E>) TypeUtil.getTypeMap(this.getClass()).entrySet().stream()
                    .filter(entry -> entry.getKey().getTypeName().equals(ENTITY_TYPE_NAME))
                    .map(Map.Entry::getValue).findFirst().orElseThrow((Supplier<Throwable>) () -> new RuntimeException("无法获取DataRepository的持久化对象类型"));
        } catch (Throwable e) {
            log.error("服务器内部错误", e);
            throw new RuntimeException("内部错误，请稍后重试");
        }
        return eneityClass;
    }

    private boolean isExtField(String fieldName) {
        return !TypeUtil.getTypeMap(this.getClass()).entrySet().stream()
                .filter(entry -> entry.getKey().getTypeName().equals(PO_TYPE_NAME))
                .map(Map.Entry::getValue).findFirst()
                .map(t -> ReflectUtil.hasField((Class<?>) t, fieldName)).orElse(false);
    }

    private void autoSwitchTable(String formId, Supplier<?> supplier) {
        try {
            setTableName(formId);
            supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            formHelper.clearFormDataTableName();
        }
    }

    private void setTableName(String formId) {
        // TODO 加缓存
        try {
            Class<P> poClass = getPoClass();
            TableName tableName = poClass.getAnnotation(TableName.class);
            if (tableName == null) {
                formHelper.setFormDataTableName(formId);
            }
        } catch (Exception e) {
            log.error("内部错误: ", e);
            throw new RuntimeException("系统内部错误，请稍后重试");
        }

    }
}
