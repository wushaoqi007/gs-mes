package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.system.domain.BillParam;
import com.greenstone.mes.system.application.service.BillParamService;
import com.greenstone.mes.system.dto.cmd.BillParamSaveCmd;
import com.greenstone.mes.system.infrastructure.po.BillParamDo;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/param/bill")
public class BillParamApi extends BaseController {

    private final BillParamService billParamService;

    @GetMapping
    public AjaxResult list(@Validated BillParamDo billParam) {
        List<BillParam> list = billParamService.list(billParam);
        return AjaxResult.success(list);
    }

    @PostMapping
    public AjaxResult save(@RequestBody @Validated List<BillParamSaveCmd> saveCmdList) {
        List<BillParamDo> billParamDos = saveCmdList.stream().map(c -> BillParamDo.builder().billType(c.getBillType())
                .paramKey(c.getParamKey())
                .paramValue(c.getParamValue()).build()).toList();
        billParamService.save(billParamDos);
        return AjaxResult.success();
    }

}