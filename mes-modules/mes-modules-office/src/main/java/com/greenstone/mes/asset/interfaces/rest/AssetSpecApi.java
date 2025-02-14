package com.greenstone.mes.asset.interfaces.rest;

import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetSpecDeleteCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetSpecInsertCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetSpecUpdateCmd;
import com.greenstone.mes.asset.application.dto.cqe.query.AssetSpecQuery;
import com.greenstone.mes.asset.application.service.AssetSpecService;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author gu_renkai
 * @date 2023/2/6 15:39
 */
@Slf4j
@RestController
@RequestMapping("/asset/specification")
public class AssetSpecApi extends BaseController {

    private final AssetSpecService assetSpecService;

    public AssetSpecApi(AssetSpecService assetSpecService) {
        this.assetSpecService = assetSpecService;
    }

    @GetMapping
    public AjaxResult list(@Validated AssetSpecQuery query) {
        return AjaxResult.success(assetSpecService.list(query));
    }

    @PostMapping
    public AjaxResult insert(@Validated @RequestBody AssetSpecInsertCmd insertCmd) {
        assetSpecService.insert(insertCmd);
        return AjaxResult.success();
    }

    @PutMapping
    public AjaxResult update(@Validated @RequestBody AssetSpecUpdateCmd updateCmd) {
        assetSpecService.update(updateCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult delete(@Validated @RequestBody AssetSpecDeleteCmd deleteCmd) {
        assetSpecService.remove(deleteCmd);
        return AjaxResult.success();
    }

}
