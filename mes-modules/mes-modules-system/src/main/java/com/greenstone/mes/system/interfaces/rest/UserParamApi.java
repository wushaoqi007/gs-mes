package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.system.application.service.UserParamService;
import com.greenstone.mes.system.dto.cmd.UserParamSaveCmd;
import com.greenstone.mes.system.infrastructure.po.UserParamDo;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/param/remembered")
public class UserParamApi extends BaseController {

    private final UserParamService userParamService;

    @GetMapping
    public AjaxResult list(@Validated UserParamDo userParam){
        List<UserParamDo> list = userParamService.list(userParam);
        return AjaxResult.success(list);
    }

    @PostMapping
    public AjaxResult save(@RequestBody @Validated List<UserParamSaveCmd> saveCmdList){
        List<UserParamDo> userParamDos = saveCmdList.stream().map(c -> UserParamDo.builder().billType(c.getBillType())
                .paramKey(c.getParamKey())
                .paramValue(c.getParamValue()).build()).toList();
        userParamService.save(userParamDos);
        return AjaxResult.success();
    }

}