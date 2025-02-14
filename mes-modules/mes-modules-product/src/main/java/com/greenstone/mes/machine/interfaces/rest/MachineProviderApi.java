package com.greenstone.mes.machine.interfaces.rest;

import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineProviderAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineProviderImportCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineProviderRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.service.MachineProviderService;
import com.greenstone.mes.machine.domain.entity.MachineProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/provider")
public class MachineProviderApi extends BaseController {

    private final MachineProviderService providerService;

    @PostMapping("/import")
    public AjaxResult importProvider(MultipartFile file) {
        log.info("Receive machine provider import request");
        // 将表格转为VO
        List<MachineProviderImportCmd> importVOs = new ExcelUtil<>(MachineProviderImportCmd.class).toList(file);
        providerService.importProviders(importVOs);
        return AjaxResult.success();
    }

    @GetMapping("/list")
    public TableDataInfo list(MachineFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("name");
        fields.add("fullName");
        fields.add("abbrName");
        fields.add("contactName");
        query.setFields(fields);
        List<MachineProvider> list = providerService.list(query);
        return getDataTable(list);
    }

    @GetMapping(value = "/{id}")
    public AjaxResult detail(@PathVariable("id") String id) {
        startPage();
        MachineProvider provider = providerService.detail(id);
        return AjaxResult.success(provider);
    }

    @PostMapping
    public AjaxResult add(@Validated @RequestBody MachineProviderAddCmd addCmd) {
        providerService.add(addCmd);
        return AjaxResult.success("新增成功");
    }

    @PutMapping
    public AjaxResult edit(@RequestBody @Validated MachineProviderAddCmd editCmd) {
        providerService.edit(editCmd);
        return AjaxResult.success("更新成功");
    }

    @DeleteMapping
    public AjaxResult remove(@Valid @RequestBody MachineProviderRemoveCmd removeCmd) {
        providerService.delete(removeCmd.getIds());
        return AjaxResult.success();
    }

}
