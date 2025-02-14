package com.greenstone.mes.material.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.material.application.dto.StockTransferVo;
import com.greenstone.mes.material.interfaces.request.StockMobileTransferNgReq;
import com.greenstone.mes.material.interfaces.transfer.StockTransfer;
import com.greenstone.mes.material.application.service.MaterialStockManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 物料库存Controller
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
@Slf4j
@RestController
@RequestMapping("/stock/mobile")
public class StockMobileController extends BaseController {

    @Autowired
    private MaterialStockManager stockManager;

    @Autowired
    private StockTransfer stockTransfer;

    /**
     * 零件转移
     */
    @PostMapping("/transfer/ng")
    public AjaxResult transfer(@RequestBody StockMobileTransferNgReq req) {
        StockTransferVo command = stockTransfer.transfer(req);
        stockManager.transfer(command);
        return AjaxResult.success();
    }
}
