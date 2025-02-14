package com.greenstone.mes.asset.interfaces.rest;

import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetTypeDeleteCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetTypeSaveCmd;
import com.greenstone.mes.asset.application.service.AssetTypeService;
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
@RequestMapping("/asset/type")
public class AssetTypeApi extends BaseController {

    private final AssetTypeService assetTypeService;

    public AssetTypeApi(AssetTypeService assetTypeService) {
        this.assetTypeService = assetTypeService;
    }

    @GetMapping("/list")
    public AjaxResult list() {
        return AjaxResult.success(assetTypeService.list());
    }

    @PostMapping
    public AjaxResult insert(@Validated @RequestBody AssetTypeSaveCmd saveCmd) {
        assetTypeService.save(saveCmd);
        return AjaxResult.success();
    }

    @PutMapping
    public AjaxResult update(@Validated @RequestBody AssetTypeSaveCmd saveCmd) {
        assetTypeService.save(saveCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult delete(@Validated @RequestBody AssetTypeDeleteCmd deleteCmd) {
        assetTypeService.remove(deleteCmd.getTypeCode());
        return AjaxResult.success();
    }

}
