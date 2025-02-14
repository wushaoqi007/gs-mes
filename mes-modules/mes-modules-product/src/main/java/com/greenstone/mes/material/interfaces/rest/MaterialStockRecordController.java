package com.greenstone.mes.material.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.material.application.service.MaterialStockRecordManager;
import com.greenstone.mes.material.domain.service.IMaterialStockRecordService;
import com.greenstone.mes.material.request.StockRecordDetailListReq;
import com.greenstone.mes.material.request.StockRecordMaterialSearchReq;
import com.greenstone.mes.material.request.StockRecordSearchReq;
import com.greenstone.mes.material.response.StockRecordDetailListResp;
import com.greenstone.mes.material.response.StockRecordMaterialSearchResp;
import com.greenstone.mes.material.response.StockRecordSearchResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 物料出入库记录Controller
 *
 * @author gu_renkai
 * @date 2022-02-17
 */
@RestController
@RequestMapping("/stockRecord")
public class MaterialStockRecordController extends BaseController {

    @Autowired
    private IMaterialStockRecordService materialStockRecordService;

    @Autowired
    private MaterialStockRecordManager stockRecordManager;

    /**
     * 按存取记录查询出入库记录
     */
    @GetMapping("/list")
    public TableDataInfo list(StockRecordSearchReq searchReq) {
        startPage();
        List<StockRecordSearchResp> list = materialStockRecordService.listStockRecord(searchReq);
        return getDataTable(list);
    }

    /**
     * 按物料查询出入库记录
     */
    @GetMapping("/list/material")
    public TableDataInfo listByMaterial(StockRecordMaterialSearchReq searchReq) {
        startPage();
        List<StockRecordMaterialSearchResp> list = materialStockRecordService.listStockRecordMaterial(searchReq);
        return getDataTable(list);
    }

    /**
     * 查询出入库记录明细
     */
    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable(value = "id") Long recordId) {
        return AjaxResult.success(stockRecordManager.getRecordDetail(recordId));
    }

    /**
     * 查询出入库记录明细列表
     */
    @GetMapping("/detail/list")
    public TableDataInfo detailList(StockRecordDetailListReq req) {
        startPage();
        List<StockRecordDetailListResp> list = materialStockRecordService.listStockRecordDetail(req);
        return getDataTable(list);
    }

}