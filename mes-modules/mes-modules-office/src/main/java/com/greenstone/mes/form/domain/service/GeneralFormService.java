package com.greenstone.mes.form.domain.service;

import com.greenstone.mes.form.domain.entity.Form;
import com.greenstone.mes.form.dto.cmd.FormInitCmd;
import com.greenstone.mes.form.dto.cmd.FormModifyCmd;
import com.greenstone.mes.system.dto.result.FormDefinitionVo;
import com.greenstone.mes.system.dto.result.MenuBriefResult;

import java.util.List;

public interface GeneralFormService {

    List<Form> getNames();

    MenuBriefResult getFormBrief(String menuId);

    FormDefinitionVo getFormDefinition(String formId);

    String initForm(FormInitCmd formInitCmd);

    Form modifyForm(FormModifyCmd formModifyCmd);

    void deleteForm(String formId);
}
