package com.greenstone.mes.form.domain.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.form.domain.entity.Form;
import com.greenstone.mes.form.domain.helper.FormHelper;
import com.greenstone.mes.form.domain.repository.FormRepository;
import com.greenstone.mes.form.domain.service.GeneralFormService;
import com.greenstone.mes.form.dto.cmd.FormInitCmd;
import com.greenstone.mes.form.dto.cmd.FormModifyCmd;
import com.greenstone.mes.system.api.RemoteMenuService;
import com.greenstone.mes.system.dto.cmd.CustomFormMenuAddCmd;
import com.greenstone.mes.system.dto.cmd.CustomFormMenuEditCmd;
import com.greenstone.mes.system.dto.result.FormDefinitionVo;
import com.greenstone.mes.system.dto.result.MenuBriefResult;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GeneralFormServiceImpl implements GeneralFormService {

    private final FormRepository formRepository;
    private final RemoteMenuService menuService;
    private final FormHelper formHelper;

    @Override
    public List<Form> getNames() {
        return formRepository.getNames();
    }

    @Override
    public MenuBriefResult getFormBrief(String menuId) {
        return menuService.getBriefForm(menuId);
    }

    @Override
    public FormDefinitionVo getFormDefinition(String formId) {
        return menuService.getFormDefinition(formId);
    }

    @GlobalTransactional
    @Override
    public String initForm(FormInitCmd formInitCmd) {
        String formId = IdUtil.fastUUID();
        String tableName = formHelper.getFormDataTableName(formId);
        // 创建菜单
        addCustomFormMenu(formId, formInitCmd, tableName);
        // 创建表单数据存储表
        formRepository.createFormDataTable(tableName, formInitCmd.getFormName());
        return formId;
    }

    @GlobalTransactional
    @Override
    public Form modifyForm(FormModifyCmd formModifyCmd) {
        // 更新表单数据表的备注
        if (StrUtil.isNotBlank(formModifyCmd.getFormName())) {
            formRepository.updateFormDataTableComment(formModifyCmd);
        }
        // 修改菜单
        editMenu(formModifyCmd);
        return null;
    }

    @GlobalTransactional
    @Override
    public void deleteForm(String formId) {
        // 删除表单
        formRepository.delete(formId);
        // 删除菜单
        deleteMenu(formId);
    }

    private void addCustomFormMenu(String formId, FormInitCmd formInitCmd, String dataTableName) {
        CustomFormMenuAddCmd menuAddCmd = CustomFormMenuAddCmd.builder()
                .menuId(formId)
                .menuName(formInitCmd.getFormName())
                .parentId(formInitCmd.getParentMenuId())
                .icon(formInitCmd.getIcon())
                .dataTableName(dataTableName)
                .usingProcess(formInitCmd.isUsingProcess()).build();
        menuService.addCustomFormMenu(menuAddCmd);
    }

    private void editMenu(FormModifyCmd modifyCmd) {
        CustomFormMenuEditCmd menuEditCmd = CustomFormMenuEditCmd.builder().menuId(modifyCmd.getFormId())
                .menuName(modifyCmd.getFormName())
                .icon(modifyCmd.getIcon())
                .customJson(modifyCmd.getCustomJson())
                .usingProcess(modifyCmd.getUsingProcess()).build();
        menuService.editCustomFormMenu(menuEditCmd);
    }

    private void deleteMenu(String menuId) {
        menuService.deleteMenu(menuId);
    }

}
