package com.greenstone.mes.bom.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.bom.domain.BomImportDetail;
import com.greenstone.mes.bom.service.BomImportDetailService;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * BOMImportDetailController
 */
@Slf4j
@RestController
@RequestMapping("/bom/import/detail")
public class BomImportDetailController extends BaseController {


    @Autowired
    private BomImportDetailService bomImportDetailService;

    /**
     * 查询BOM导入详情
     */
    @GetMapping(value = "/{id}")
    public AjaxResult bomImportDetail(@PathVariable("id") Long recordId) {
        List<BomImportDetail> list = bomImportDetailService.list(Wrappers.query(BomImportDetail.builder().recordId(recordId).build()));
        return AjaxResult.success(list);
    }

}