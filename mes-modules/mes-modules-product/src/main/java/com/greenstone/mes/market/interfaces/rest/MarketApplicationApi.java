package com.greenstone.mes.market.interfaces.rest;

import com.greenstone.mes.ces.dto.cmd.AppStatusChangeCmd;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.datascope.annotation.DataScope;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.market.application.dto.MarketAppRemoveCmd;
import com.greenstone.mes.market.application.dto.MarketAppSaveCmd;
import com.greenstone.mes.market.application.dto.query.MarketAppFuzzyQuery;
import com.greenstone.mes.market.application.dto.result.MarketAppResult;
import com.greenstone.mes.market.application.service.MarketAppDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/23 9:29
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/application")
public class MarketApplicationApi extends BaseController {

    private final MarketAppDataService marketAppService;

    @PutMapping("/statusChange")
    public AjaxResult statusChange(@Validated @RequestBody AppStatusChangeCmd statusChangeCmd) {
        marketAppService.changeStatus(statusChangeCmd);
        return AjaxResult.success();
    }

    @GetMapping("/{id}")
    public AjaxResult getById(@PathVariable("id") @NotEmpty(message = "请选择单据") String id) {
        MarketAppResult marketAppResult = marketAppService.getById(id);
        return AjaxResult.success(marketAppResult);
    }

    @DataScope(userField = "applied_by", pageable = true)
    @GetMapping
    public TableDataInfo list(MarketAppFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("title");
        fields.add("serialNo");
        fields.add("content");
        fields.add("appliedByName");
        query.setFields(fields);
        return getDataTable(marketAppService.list(query));
    }

    @ApiLog
    @PostMapping("/draft")
    public AjaxResult draft(@Validated @RequestBody MarketAppSaveCmd addCmd) {
        log.info("Market application: receive add command, {}", addCmd);
        marketAppService.saveDraft(addCmd);
        return AjaxResult.success();
    }

    @ApiLog
    @PostMapping("/commit")
    public AjaxResult commit(@Validated @RequestBody MarketAppSaveCmd editCmd) {
        marketAppService.saveCommit(editCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody MarketAppRemoveCmd removeCmd) {
        marketAppService.delete(removeCmd.getSerialNos());
        return AjaxResult.success();
    }

}
