package com.greenstone.mes.bom.controller;

import com.greenstone.mes.bom.manager.BomDetailManager;
import com.greenstone.mes.bom.request.BomDetailEditReq;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Bom详情接口
 */
@Slf4j
@RestController
@RequestMapping("/bom/detail")
public class BomDetailController {


    @Autowired
    private BomDetailManager bomDetailManager;

    /**
     * 修改BOM详情
     */
    @Log(title = "BOM", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated BomDetailEditReq editRequest) {
        bomDetailManager.update(editRequest);
        return AjaxResult.success("修改成功");
    }

    /**
     * 删除BOM详情
     */
    @Log(title = "BOM", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        bomDetailManager.delete(id);
        return AjaxResult.success("删除成功");
    }
}
