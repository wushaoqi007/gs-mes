package com.greenstone.mes.asset.interfaces.rest;

import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetReqsCreateCmd;
import com.greenstone.mes.asset.application.service.AssetReqsService;
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
@RequestMapping("/asset/requisition")
public class AssetReqsApi extends BaseController {

    private final AssetReqsService assetReqsService;

    public AssetReqsApi(AssetReqsService assetReqsService) {
        this.assetReqsService = assetReqsService;
    }

    @GetMapping
    public TableDataInfo list() {
        startPage();
        return getDataTable(assetReqsService.list());
    }

    @PostMapping
    public AjaxResult create(@Validated @RequestBody AssetReqsCreateCmd createCmd) {
        assetReqsService.create(createCmd);
        return AjaxResult.success();
    }


}
