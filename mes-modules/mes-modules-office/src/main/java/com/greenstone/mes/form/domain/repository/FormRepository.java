package com.greenstone.mes.form.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.form.domain.converter.FormConverter;
import com.greenstone.mes.form.domain.entity.Form;
import com.greenstone.mes.form.domain.helper.FormHelper;
import com.greenstone.mes.form.dto.cmd.FormModifyCmd;
import com.greenstone.mes.form.infrastructure.mapper.FormDefinitionMapper;
import com.greenstone.mes.form.infrastructure.mapper.FormMapper;
import com.greenstone.mes.form.infrastructure.persistence.FormDefinitionDo;
import com.greenstone.mes.form.infrastructure.persistence.FormDo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FormRepository {

    private final FormDefinitionMapper definitionMapper;
    private final FormMapper formMapper;
    private final FormConverter formConverter;
    private final FormHelper formHelper;

    public List<Form> getNames() {
        LambdaQueryWrapper<FormDo> select = Wrappers.lambdaQuery(FormDo.class).select(FormDo::getFormId, FormDo::getFormName);
        List<FormDo> formDos = formMapper.selectList(select);
        return formConverter.dos2Entities(formDos);
    }

    public void delete(String formId) {
        // 删除表单
        formMapper.deleteById(formId);
        // 删除表单定义
        definitionMapper.delete(FormDefinitionDo.builder().formId(formId).build());
        // 删除数据表
        dropFormDataTable(formId);
    }

    public void createFormDataTable(String tableName, String formName) {
        formMapper.createFormDataTable(tableName, formName);
    }

    public void updateFormDataTableComment(FormModifyCmd form) {
        String tableName = formHelper.getFormDataTableName(form.getFormId());
        formMapper.updateFormDataTableComment(tableName, form.getFormName());
    }

    public void dropFormDataTable(String formId) {
        String tableName = formHelper.getFormDataTableName(formId);
        formMapper.dropFormDataTable(tableName);
    }

}
