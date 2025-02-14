package com.greenstone.mes.machine.interfaces.rest;

import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.ValidationUtils;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.datascope.annotation.DataScope;
import com.greenstone.mes.machine.application.assemble.MachineCalculateAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.*;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineCalculateHistoryQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.result.MachineCalculateResult;
import com.greenstone.mes.machine.application.service.MachineCalculateService;
import com.greenstone.mes.machine.domain.entity.MachineCalculateHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/calculate")
public class MachineCalculateApi extends BaseController {

    private final MachineCalculateService calculateService;
    private final MachineCalculateAssemble calculateAssemble;

    /**
     * 核价单导入
     */
    @PostMapping("/import")
    public AjaxResult importCalculate(MultipartFile file) {
        log.info("Receive machine calculate import request");
        // 将表格转为VO
        List<MachineCalculateImportVO> importVOs = new ExcelUtil<>(MachineCalculateImportVO.class).toList(file);
        // 校验表格数据
        String validateResult = ValidationUtils.validate(importVOs);
        if (Objects.nonNull(validateResult)) {
            log.error(validateResult);
            throw new ServiceException(validateResult);
        }
        log.info("Import content size after empty filter: {}", importVOs.size());
        // 处理加工单的导入
        MachineCalculateImportCmd importCommand = MachineCalculateImportCmd.builder().parts(calculateAssemble.toPartImportCommands(importVOs)).build();
        calculateService.importCalculate(importCommand);
        return AjaxResult.success();
    }

    @DataScope(userField = "calculate_by_id", suitRoleKeys = "calculate,calculateAdmin", pageable = true)
    @GetMapping("/list")
    public TableDataInfo calculateList(MachineFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        query.setFields(fields);
        List<MachineCalculateResult> list = calculateService.selectList(query);
        return getDataTable(list);
    }

    @GetMapping(value = "/history")
    public AjaxResult history(MachineCalculateHistoryQuery query) {
        startPage();
        List<MachineCalculateHistory> historyList = calculateService.selectHistory(query);
        return AjaxResult.success(historyList);
    }

    @GetMapping(value = "/{serialNo}")
    public AjaxResult detail(@PathVariable("serialNo") String serialNo) {
        startPage();
        MachineCalculateResult result = calculateService.detail(serialNo);
        return AjaxResult.success(result);
    }

    @PutMapping
    public AjaxResult calculate(@RequestBody @Validated MachineCalculateDetailEditCmd editCmd) {
        calculateService.calculate(editCmd);
        return AjaxResult.success("核价已记录");
    }

    @PutMapping("/statusChange")
    public AjaxResult statusChange(@Validated @RequestBody MachineStatusChangeCmd statusChangeCmd) {
        calculateService.statusChange(statusChangeCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult remove(@Valid @RequestBody MachineRemoveCmd removeCmd) {
        calculateService.remove(removeCmd);
        return AjaxResult.success();
    }
}
