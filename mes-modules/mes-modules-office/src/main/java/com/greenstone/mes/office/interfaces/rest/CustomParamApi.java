package com.greenstone.mes.office.interfaces.rest;

import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.office.application.service.CustomParamService;
import com.greenstone.mes.office.application.dto.CustomParamListQuery;
import com.greenstone.mes.office.application.dto.CustomParamQuery;
import com.greenstone.mes.office.application.dto.CustomParamSaveCmd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/custom/param")
public class CustomParamApi {

    private final CustomParamService customParamService;

    @GetMapping("/get")
    public AjaxResult getParam(@Validated CustomParamQuery query) {
        return AjaxResult.success(customParamService.getParam(query));
    }

    @GetMapping("/list")
    public AjaxResult listParam(@Validated CustomParamListQuery query) {
        return AjaxResult.success(customParamService.listParam(query));
    }

    @PostMapping
    public AjaxResult saveParam(@RequestBody @Validated CustomParamSaveCmd saveCmd) {
        customParamService.save(saveCmd);
        return AjaxResult.success();
    }

    @PostMapping("/batch")
    public AjaxResult saveBatch(@RequestBody @Validated List<CustomParamSaveCmd> saveCmd) {
        customParamService.saveBatch(saveCmd);
        return AjaxResult.success();
    }

}
