package com.greenstone.mes.oa.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.oa.application.service.CustomShiftService;
import com.greenstone.mes.oa.domain.entity.CustomShift;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/shift")
public class CustomShiftApi extends BaseController {

    private final CustomShiftService shiftService;

    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable("id") Long id) {
        return AjaxResult.success(shiftService.detail(id));
    }

    @GetMapping
    public TableDataInfo list(CustomShift customShift) {
        startPage();
        List<CustomShift> customShifts = shiftService.list(customShift);
        return getDataTable(customShifts);
    }


    @PostMapping
    public AjaxResult add(@RequestBody @Validated CustomShift customShift) {
        shiftService.add(customShift);
        return AjaxResult.success();
    }

    @PutMapping
    public AjaxResult update(@RequestBody @Validated CustomShift customShift) {
        shiftService.update(customShift);
        return AjaxResult.success();
    }

    @DeleteMapping("/{ids}")
    public AjaxResult deleteBatch(@PathVariable Long[] ids) {
        shiftService.deleteBatch(ids);
        return AjaxResult.success();
    }

}
