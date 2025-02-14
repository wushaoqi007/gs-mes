package com.greenstone.mes.oa.interfaces.rest;

import com.greenstone.mes.common.core.utils.poi.MultiExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.oa.application.service.DormService;
import com.greenstone.mes.oa.dto.cmd.DormMemberOperationCmd;
import com.greenstone.mes.oa.dto.cmd.DormSaveCmd;
import com.greenstone.mes.oa.dto.cmd.DormUpdateCmd;
import com.greenstone.mes.oa.dto.query.DormListQuery;
import com.greenstone.mes.oa.dto.query.DormRecordQuery;
import com.greenstone.mes.oa.dto.result.DormExportResult;
import com.greenstone.mes.oa.dto.result.DormRecordResult;
import com.greenstone.mes.oa.dto.result.DormResult;
import com.greenstone.mes.oa.enums.DormCityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/dorm")
public class DormApi extends BaseController {

    private final DormService dormService;

    @GetMapping("/{dormNo}")
    public AjaxResult detail(@PathVariable("dormNo") String dormNo) {
        return AjaxResult.success(dormService.detail(dormNo));
    }

    @GetMapping
    public TableDataInfo list(DormListQuery query) {
        startPage();
        List<DormResult> dormResults = dormService.list(query);
        return getDataTable(dormResults);
    }

    @GetMapping("/cities")
    public TableDataInfo cities() {
        startPage();
        List<DormResult> dormResults = dormService.cities();
        return getDataTable(dormResults);
    }

    @GetMapping("/details")
    public TableDataInfo detailList(DormListQuery query) {
        startPage();
        List<DormResult> dormResults = dormService.detailList(query);
        return getDataTable(dormResults);
    }

    @GetMapping("record")
    public TableDataInfo records(DormRecordQuery query) {
        startPage();
        List<DormRecordResult> recordResults = dormService.records(query);
        return getDataTable(recordResults);
    }

    @GetMapping("/tree")
    public AjaxResult tree() {
        return AjaxResult.success(dormService.tree());
    }

    @GetMapping("/member/{employeeId}")
    public AjaxResult getMember(@PathVariable("employeeId") Long employeeId) {
        return AjaxResult.success(dormService.getDormMember(employeeId));
    }

    @PostMapping
    public AjaxResult add(@RequestBody @Validated DormSaveCmd saveCmd) {
        return AjaxResult.success(dormService.add(saveCmd));
    }

    @PutMapping
    public AjaxResult update(@RequestBody @Validated DormUpdateCmd updateCmd) {
        return AjaxResult.success(dormService.update(updateCmd));
    }

    @PostMapping("/member")
    public AjaxResult memberOperation(@RequestBody @Validated DormMemberOperationCmd operationCmd) {
        dormService.dormOperation(operationCmd);
        return AjaxResult.success();
    }

    @DeleteMapping("/{dormNo}")
    public AjaxResult delete(@PathVariable("dormNo") String dormNo) {
        dormService.remove(dormNo);
        return AjaxResult.success();
    }

    @PostMapping("/export")
    public void importDorm(HttpServletResponse response) {
        List<DormExportResult> wuxiDorms = dormService.exportDorm(DormCityType.WUXI);
        List<DormExportResult> otherDorms = dormService.exportDorm(DormCityType.OTHER);
        MultiExcelUtil<DormExportResult> excelUtil = new MultiExcelUtil<>(DormExportResult.class);
        excelUtil.exportInit();
        excelUtil.addSheet(wuxiDorms, "无锡宿舍");
        excelUtil.addSheet(otherDorms, "驻外宿舍");
        excelUtil.exportExcel(response);
    }

}
