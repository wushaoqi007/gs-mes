package com.greenstone.mes.asset.interfaces.rest;

import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetRevertCreateCmd;
import com.greenstone.mes.asset.application.service.AssetRevertService;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author gu_renkai
 * @date 2023/2/6 15:39
 */
@Slf4j
@RestController
@RequestMapping("/asset/revert")
public class AssetRevertApi extends BaseController {

    private final AssetRevertService assetRevertService;

    public AssetRevertApi(AssetRevertService assetRevertService) {
        this.assetRevertService = assetRevertService;
    }

    @GetMapping
    public TableDataInfo list() {
        startPage();
        return getDataTable(assetRevertService.list());
    }

    @PostMapping
    public AjaxResult create(@Validated @RequestBody AssetRevertCreateCmd createCmd) {
        assetRevertService.create(createCmd);
        return AjaxResult.success();
    }


}
