package com.greenstone.mes.form.interfaces.web.rest;

import com.alibaba.fastjson2.JSONObject;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.form.domain.entity.Form;
import com.greenstone.mes.form.domain.service.GeneralFormService;
import com.greenstone.mes.form.dto.cmd.FormInitCmd;
import com.greenstone.mes.form.dto.cmd.FormModifyCmd;
import com.greenstone.mes.system.dto.result.FormDefinitionVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

/**
 * 表单定义接口
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/form")
public class FormDefinitionApi {

    private final GeneralFormService generalFormService;

    @GetMapping("/definition/{formId}")
    public AjaxResult get(@PathVariable("formId") @NotBlank(message = "请指定需要查询的表单") String formId) {
        FormDefinitionVo formDefinition = generalFormService.getFormDefinition(formId);
        return AjaxResult.success(formDefinition);
    }

    @GetMapping("/definition/names")
    public AjaxResult names() {
        return AjaxResult.success(generalFormService.getNames());
    }

    @PostMapping("/definition")
    public AjaxResult newForm(@RequestBody @Validated FormInitCmd formInitCmd) {
        String formId = generalFormService.initForm(formInitCmd);
        return AjaxResult.success(JSONObject.of("formId", formId));
    }

    @PutMapping("/definition/{formId}")
    public AjaxResult modifyForm(@PathVariable("formId") @NotBlank(message = "请指定需要修改的表单") String formId,
                                 @RequestBody @Validated FormModifyCmd formModifyCmd) {
        Form form = generalFormService.modifyForm(formModifyCmd);
        return AjaxResult.success(form);
    }

    @DeleteMapping("/definition/{formId}")
    public AjaxResult deleteForm(@PathVariable("formId") @NotBlank(message = "请指定需要删除的表单") String formId) {
        generalFormService.deleteForm(formId);
        return AjaxResult.success();
    }
}
