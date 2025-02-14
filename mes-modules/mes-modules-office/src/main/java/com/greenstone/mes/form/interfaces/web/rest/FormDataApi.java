package com.greenstone.mes.form.interfaces.web.rest;

import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.external.dto.result.ProcessRunResult;
import com.greenstone.mes.form.application.assembler.FormDataAssembler;
import com.greenstone.mes.form.domain.converter.FormDataConverter;
import com.greenstone.mes.form.domain.entity.CustomFormDataEntity;
import com.greenstone.mes.form.domain.service.CustomFormDataService;
import com.greenstone.mes.form.dto.cmd.FormDataRemoveCmd;
import com.greenstone.mes.form.dto.cmd.FormDataRevokeCmd;
import com.greenstone.mes.form.dto.cmd.FormDataSaveCmd;
import com.greenstone.mes.form.dto.query.FormDataQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 表单数据接口
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/form")
public class FormDataApi {

    private final CustomFormDataService customFormDataService;
    private final FormDataAssembler formDataAssembler;
    private final FormDataConverter formDataConverter;

    @PostMapping("/data")
    public AjaxResult list(@RequestBody @Validated FormDataQuery formDataQuery) {
        return AjaxResult.success(customFormDataService.queryPo2Result(formDataQuery, formDataConverter::do2Results));
    }

    /**
     * 保存草稿
     */
    @PostMapping("/draft")
    public AjaxResult draft(@RequestBody @Validated FormDataSaveCmd saveCmd) {
        CustomFormDataEntity dataEntity = customFormDataService.saveDraft(saveCmd, formDataAssembler::saveCmd2Entity);
        return AjaxResult.success(dataEntity);
    }

    /**
     * 提交
     */
    @PostMapping("/commit")
    public AjaxResult commit(@RequestBody @Validated FormDataSaveCmd saveCmd) {
        CustomFormDataEntity dataEntity = customFormDataService.saveCommit(saveCmd, formDataAssembler::saveCmd2Entity);
        return AjaxResult.success(dataEntity);
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    public AjaxResult delete(@RequestBody @Validated FormDataRemoveCmd deleteCmd) {
        customFormDataService.delete(deleteCmd);
        return AjaxResult.success();
    }

    /**
     * 撤销
     */
    @PostMapping("/revoke")
    public AjaxResult revoke(@RequestBody @Validated FormDataRevokeCmd revokeCmd) {
        customFormDataService.revoke(revokeCmd);
        return AjaxResult.success();
    }

}
