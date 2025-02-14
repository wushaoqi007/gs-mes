package com.greenstone.mes.ces.interfaces.web.rest;

import com.greenstone.mes.ces.application.dto.cmd.RequisitionAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.RequisitionEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.RequisitionRemoveCmd;
import com.greenstone.mes.ces.application.dto.query.RequisitionFuzzyQuery;
import com.greenstone.mes.ces.application.dto.query.RequisitionQuery;
import com.greenstone.mes.ces.application.dto.result.RequisitionResult;
import com.greenstone.mes.ces.application.service.RequisitionService;
import com.greenstone.mes.ces.dto.cmd.RequisitionStatusChangeCmd;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/consumable/requisition")
public class RequisitionApi extends BaseController {

    private final RequisitionService requisitionService;

    public RequisitionApi(RequisitionService requisitionService) {
        this.requisitionService = requisitionService;
    }

    @PutMapping("/statusChange")
    public AjaxResult statusChange(@Validated @RequestBody RequisitionStatusChangeCmd statusChangeCmd) {
        requisitionService.statusChange(statusChangeCmd);
        return AjaxResult.success();
    }

    @GetMapping
    public AjaxResult detail(@Validated RequisitionQuery query) {
        RequisitionResult detail = requisitionService.detail(query.getSerialNo());
        return AjaxResult.success(detail);
    }

    @GetMapping("/list")
    public TableDataInfo list(RequisitionFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(requisitionService.list(query));
    }

    @GetMapping("/item/list")
    public TableDataInfo itemList(RequisitionFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(requisitionService.itemList(query));
    }

    @PostMapping
    public AjaxResult add(@Validated @RequestBody RequisitionAddCmd addCmd) {
        requisitionService.add(addCmd);
        return AjaxResult.success();
    }

    @PutMapping
    public AjaxResult edit(@Validated @RequestBody RequisitionEditCmd editCmd) {
        requisitionService.edit(editCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody RequisitionRemoveCmd removeCmd) {
        requisitionService.remove(removeCmd);
        return AjaxResult.success();
    }
}
