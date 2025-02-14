package com.greenstone.mes.asset.interfaces.rest;

import com.greenstone.mes.asset.application.dto.cqe.query.AssetHandleLogQuery;
import com.greenstone.mes.asset.application.service.AssetHandleLogService;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gu_renkai
 * @date 2023/2/10 16:52
 */
@Slf4j
@RestController
@RequestMapping("/asset/handle/log")
public class AssetHandleLogApi extends BaseController {

    private AssetHandleLogService assetHandleLogService;

    public AssetHandleLogApi(AssetHandleLogService assetHandleLogService) {
        this.assetHandleLogService = assetHandleLogService;
    }

    @GetMapping
    public TableDataInfo list(AssetHandleLogQuery query) {
        startPage();
        return getDataTable(assetHandleLogService.list(query));
    }

}
