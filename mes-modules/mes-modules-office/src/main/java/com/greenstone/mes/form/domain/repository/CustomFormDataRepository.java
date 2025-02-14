package com.greenstone.mes.form.domain.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.form.domain.converter.FormDataConverter;
import com.greenstone.mes.form.domain.entity.CustomFormDataEntity;
import com.greenstone.mes.form.domain.helper.FormHelper;
import com.greenstone.mes.form.dto.query.FormDataQuery;
import com.greenstone.mes.form.dto.result.BaseFormDataDataResult;
import com.greenstone.mes.form.infrastructure.mapper.FormDataMapper;
import com.greenstone.mes.form.infrastructure.persistence.FormDataPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

/**
 * 自定义表单的仓库类
 */
@Slf4j
@Service
public class CustomFormDataRepository extends AbstractFormDataRepository<CustomFormDataEntity, FormDataPo, FormDataMapper> {

    private final FormDataMapper formDataMapper;
    private final FormDataConverter formDataConverter;
    private final FormHelper formHelper;

    public CustomFormDataRepository(FormDataMapper formDataMapper, FormDataConverter formDataConverter, FormHelper formHelper) {
        super(formHelper, formDataMapper);
        this.formDataMapper = formDataMapper;
        this.formDataConverter = formDataConverter;
        this.formHelper = formHelper;
    }

    /**
     * 保存表单
     */
    public CustomFormDataEntity saveFormData(CustomFormDataEntity formDataEntity) {
        String id;
        try {
            formHelper.setFormDataTableName(formDataEntity.getFormId());
            // 更新单据
            if (StrUtil.isNotBlank(formDataEntity.getId())) {
                boolean exists = formDataMapper.exists(FormDataPo.builder().id(formDataEntity.getId()).build());
                if (!exists) {
                    throw new RuntimeException("选择的单据不存在");
                } else {
                    FormDataPo formDataDo = formDataConverter.entity2Do(formDataEntity);
                    formDataMapper.updateById(formDataDo);
                }
                id = formDataEntity.getId();
            } else if (StrUtil.isNotBlank(formDataEntity.getSerialNo())) {
                boolean exists = formDataMapper.exists(FormDataPo.builder().serialNo(formDataEntity.getSerialNo()).build());
                FormDataPo formDataDo = formDataConverter.entity2Do(formDataEntity);
                if (!exists) {
                    formDataMapper.insert(formDataDo);
                    id = formDataDo.getId();
                } else {
                    LambdaUpdateWrapper<FormDataPo> updateWrapper = Wrappers.lambdaUpdate(FormDataPo.class)
                            .eq(FormDataPo::getSerialNo, formDataDo.getSerialNo());
                    formDataMapper.update(formDataDo, updateWrapper);
                    id = formDataEntity.getId();
                }
            } else {
                // 新增单据
                FormDataPo formDataDo = formDataConverter.entity2Do(formDataEntity);
                formDataMapper.insert(formDataDo);
                id = formDataDo.getId();
            }
            FormDataPo formDataDo = formDataMapper.selectById(id);
            return formDataConverter.do2entity(formDataDo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            formHelper.clearFormDataTableName();
        }
    }


    /**
     * 查询自定义表
     */
    public List<BaseFormDataDataResult> formDataQuery(FormDataQuery query) {
        List<FormDataPo> formDataDos = super.queryPoList(query);
        return formDataConverter.do2Results(formDataDos);
    }

    public CustomFormDataEntity getById(String formId, String id) {
        CustomFormDataEntity formDataEntity;
        try {
            formHelper.setFormDataTableName(formId);
            LambdaQueryWrapper<FormDataPo> queryWrapper = Wrappers.lambdaQuery(FormDataPo.class)
                    .select(FormDataPo::getSerialNo)
                    .eq(FormDataPo::getId, id);
            FormDataPo formDataDo = formDataMapper.selectOne(queryWrapper);
            formDataEntity = formDataConverter.do2entity(formDataDo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            formHelper.clearFormDataTableName();
        }
        return formDataEntity;
    }

    public List<FormDataPo> getByIds(String formId, List<String> ids) {
        return getDos(formId, () -> formDataMapper.selectBatchIds(ids));
    }

    public void deleteByIds(String formId, List<String> ids) {
        doSomething(formId, () -> formDataMapper.deleteBatchIds(ids));
    }

    public void revokeByIds(String formId, List<String> ids) {
        doSomething(formId, () -> {
            LambdaUpdateWrapper<FormDataPo> updateWrapper = Wrappers.lambdaUpdate(FormDataPo.class)
                    .set(FormDataPo::getStatus, ProcessStatus.REVOKED)
                    .in(FormDataPo::getId, ids);
            return formDataMapper.update(updateWrapper);
        });
    }

    private List<FormDataPo> getDos(String formId, Supplier<List<FormDataPo>> supplier) {
        try {
            formHelper.setFormDataTableName(formId);
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            formHelper.clearFormDataTableName();
        }
    }

    private void doSomething(String formId, Supplier<?> supplier) {
        try {
            formHelper.setFormDataTableName(formId);
            supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            formHelper.clearFormDataTableName();
        }
    }

}
