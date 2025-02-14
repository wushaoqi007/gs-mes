package com.greenstone.mes.bom.controller;

import com.greenstone.mes.bom.request.BomImportRecordReq;
import com.greenstone.mes.bom.response.BomImportRecordListResp;
import com.greenstone.mes.bom.service.IBomImportRecordService;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * BOMImportRecordController
 */
@Slf4j
@RestController
@RequestMapping("/bom/import")
public class BomImportRecordController extends BaseController {

    @Autowired
    private IBomImportRecordService bomImportRecordService;


    /**
     * 查询BOM导入记录列表
     */
    @GetMapping("/list")
    public TableDataInfo bomImportRecordList(BomImportRecordReq bomImportRecordReq) {
        startPage();
        List<BomImportRecordListResp> list = bomImportRecordService.selectBomImportRecordList(bomImportRecordReq);
        return getDataTable(list);
    }


    /**
     * 查询某条BOM导入记录列表
     */
    @GetMapping(value = "/{id}")
    public AjaxResult bomImportRecord(@PathVariable("id") Long recordId) {
        return AjaxResult.success(bomImportRecordService.selectBomImportRecordById(recordId));
    }


}