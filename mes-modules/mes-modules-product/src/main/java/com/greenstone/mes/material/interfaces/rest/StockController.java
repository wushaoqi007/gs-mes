package com.greenstone.mes.material.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.material.application.dto.PartStockNumberEditCmd;
import com.greenstone.mes.material.application.dto.StockTransferVo;
import com.greenstone.mes.material.application.dto.StockUpdateQuery;
import com.greenstone.mes.material.application.dto.result.StockUpdateR;
import com.greenstone.mes.material.interfaces.request.StockTransferNgReq;
import com.greenstone.mes.material.interfaces.request.StockTransferReq;
import com.greenstone.mes.material.interfaces.transfer.StockTransfer;
import com.greenstone.mes.material.application.service.MaterialStockManager;
import com.greenstone.mes.material.request.*;
import com.greenstone.mes.material.response.StockListResp;
import com.greenstone.mes.material.response.StockSearchResp;
import com.greenstone.mes.material.response.StockTimeOutListResp;
import com.greenstone.mes.material.response.StockTotalListResp;
import com.greenstone.mes.material.domain.service.MaterialStockService;
import com.greenstone.mes.material.domain.service.StockSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物料库存Controller
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
@Slf4j
@RestController
@RequestMapping("/stock")
public class StockController extends BaseController {

    @Autowired
    private MaterialStockService materialStockService;

    @Autowired
    private StockSearchService stockSearchService;

    @Autowired
    private MaterialStockManager stockManager;

    @Autowired
    private StockTransfer stockTransfer;

    /**
     * 查询物料库存信息
     */
    @GetMapping("/search")
    public TableDataInfo search(StockSearchReq searchRequest) {
        startPage();
        List<StockSearchResp> list = stockSearchService.searchMaterialInStock(searchRequest);
        return getDataTable(list);
    }

    /**
     * 查询物料总库存
     */
    @GetMapping("/list/total")
    public TableDataInfo searchMaterialTotal(StockTotalListReq searchRequest) {
        startPage();
        List<StockTotalListResp> list = materialStockService.listStockTotal(searchRequest);
        return getDataTable(list);
    }


    /**
     * 查询物料库存
     */
    @GetMapping("/list")
    public TableDataInfo list(StockListReq req) {
        startPage();
        List<StockListResp> list = materialStockService.listStock(req);
        return getDataTable(list);
    }

    /**
     * 查询所有物料库存（无分页）
     */
    @GetMapping("/list/all")
    public AjaxResult listAllStock(StockListReq req) {
        List<StockListResp> list = materialStockService.listStock(req);
        return AjaxResult.success(list);
    }

    /**
     * 物料入库
     */
    @Log(title = "物料入库", businessType = BusinessType.INSERT)
    @PostMapping("/in")
    public AjaxResult in(@RequestBody @Validated MaterialInStockReq inStockRequest) {
        log.info("Request data: {}", inStockRequest);
//        StockOperationCommand stockOperationCommand = stockManager.checkAndGetInStockData(inStockRequest);
//        stockManager.inStock(stockOperationCommand);
        return AjaxResult.error("不支持的操作");
    }

    /**
     * 物料出库
     */
    @Log(title = "物料出库", businessType = BusinessType.DELETE)
    @PostMapping("/out")
    public AjaxResult out(@RequestBody @Validated MaterialOutStockReq outStockRequest) {
        log.info("Request data: {}", outStockRequest);
//        StockOperationCommand stockOperationCommand = stockManager.checkAndGetOutStockData(outStockRequest);
//        stockManager.outStock(stockOperationCommand);
        return AjaxResult.error("不支持的操作");
    }


    /**
     * 获取物料库存详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(materialStockService.getById(id));
    }


    /**
     * 查询物料滞留库存
     */
    @GetMapping("/list/timeout")
    public TableDataInfo searchTimeout(@Validated StockTimeoutSearchReq searchReq) {
        startPage();
        List<StockTimeOutListResp> list = materialStockService.listStockTimeout(searchReq);
        return getDataTable(list);
    }

    /**
     * 仓库任务调用：查询物料所有滞留库存（无分页）
     */
    @GetMapping("/list/all/timeout")
    public AjaxResult searchAllTimeout(StockTimeoutSearchReq searchReq) {
        List<StockTimeOutListResp> list = materialStockService.listStockTimeout(searchReq);
        return AjaxResult.success(list);
    }

    /**
     * 零件转移
     */
    @PostMapping("/transfer")
    public AjaxResult transfer(@RequestBody StockTransferReq req) {
        StockTransferVo command = stockTransfer.transfer(req);
        stockManager.transfer(command);
        return AjaxResult.success();
    }

    @PostMapping("/transfer/ng")
    public AjaxResult transferNg(StockTransferNgReq req) {
        StockTransferVo command = stockTransfer.transfer(req);
        stockManager.transfer(command);
        return AjaxResult.success();
    }

    @PutMapping("/number")
    public AjaxResult updateStockNumber(@RequestBody @Validated PartStockNumberEditCmd editCmd) {
        stockManager.updateStockNumber(editCmd);
        return AjaxResult.success();
    }

    @GetMapping("/update/list")
    public AjaxResult listStockForUpdate(StockUpdateQuery query) {
        List<StockUpdateR> list = materialStockService.listStockForUpdate(query);
        return AjaxResult.success(list);
    }

}
