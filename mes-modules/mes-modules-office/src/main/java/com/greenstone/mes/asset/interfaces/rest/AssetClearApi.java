package com.greenstone.mes.asset.interfaces.rest;

import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetClearCreateCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetClearRestoreCmd;
import com.greenstone.mes.asset.application.service.AssetClearService;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author gu_renkai
 * @date 2023/2/6 15:39
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/asset/clear")
public class AssetClearApi extends BaseController {

    private final AssetClearService assetClearService;

    @GetMapping
    public TableDataInfo clears() {
        startPage();
        return getDataTable(assetClearService.clears());
    }

    @PostMapping
    public AjaxResult save(@RequestBody @Validated AssetClearCreateCmd createCmd){
        assetClearService.save(createCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult restore(@RequestBody @Validated AssetClearRestoreCmd restoreCmd){
        assetClearService.restore(restoreCmd);
        return AjaxResult.success();
    }
}
