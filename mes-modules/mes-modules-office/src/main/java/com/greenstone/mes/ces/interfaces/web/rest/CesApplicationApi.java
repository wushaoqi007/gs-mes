package com.greenstone.mes.ces.interfaces.web.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.ces.dto.cmd.AppNoticeCmd;
import com.greenstone.mes.ces.dto.cmd.AppStatusChangeCmd;
import com.greenstone.mes.ces.application.dto.cmd.CesApplicationAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.CesApplicationEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.ApplicationRemoveCmd;
import com.greenstone.mes.ces.application.dto.query.ApplicationFuzzyQuery;
import com.greenstone.mes.ces.application.dto.query.ApplicationQuery;
import com.greenstone.mes.ces.application.dto.result.CesApplicationResult;
import com.greenstone.mes.ces.application.service.CesApplicationService;
import com.greenstone.mes.ces.dto.cmd.StateChangeCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/23 9:29
 */
@Slf4j
@RestController
@RequestMapping("/consumable/application")
public class CesApplicationApi extends BaseController {

    private final CesApplicationService cesApplicationService;

    public CesApplicationApi(CesApplicationService cesApplicationService) {
        this.cesApplicationService = cesApplicationService;
    }

    @PutMapping("/statusChange")
    public AjaxResult statusChange(@Validated @RequestBody AppStatusChangeCmd statusChangeCmd) {
        cesApplicationService.statusChange(statusChangeCmd);
        return AjaxResult.success();
    }

    @GetMapping
    public AjaxResult detail(@Validated ApplicationQuery query) {
        CesApplicationResult detail = cesApplicationService.detail(query.getSerialNo());
        return AjaxResult.success(detail);
    }

    @GetMapping("/list")
    public TableDataInfo list(ApplicationFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(cesApplicationService.list(query));
    }

    @GetMapping("/list/waitHandle")
    public TableDataInfo waitHandle(ApplicationFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(cesApplicationService.waitHandle(query));
    }

    @PostMapping
    public AjaxResult add(@Validated @RequestBody CesApplicationAddCmd addCmd) {
        cesApplicationService.add(addCmd);
        return AjaxResult.success();
    }

    @PutMapping
    public AjaxResult edit(@Validated @RequestBody CesApplicationEditCmd editCmd) {
        cesApplicationService.edit(editCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody ApplicationRemoveCmd removeCmd) {
        cesApplicationService.remove(removeCmd);
        return AjaxResult.success();
    }

    @PostMapping("/changeState")
    public AjaxResult changeState(@Validated @RequestBody StateChangeCmd stateChangeCmd) {
        cesApplicationService.changeState(stateChangeCmd);
        return AjaxResult.success();
    }

    @PostMapping("/notice")
    public AjaxResult notice(@Validated @RequestBody AppNoticeCmd noticeCmd) {
        cesApplicationService.notice(noticeCmd);
        return AjaxResult.success();
    }
}
