package com.greenstone.mes.material.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.material.domain.MaterialSupplier;
import com.greenstone.mes.material.request.MaterialSupplierAddReq;
import com.greenstone.mes.material.request.MaterialSupplierEditReq;
import com.greenstone.mes.material.request.MaterialSupplierListReq;
import com.greenstone.mes.material.domain.service.IMaterialSupplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 供应商管理接口
 *
 * @author wushaoqi
 * @date 2022-09-26-15:23
 */
@Slf4j
@RestController
@RequestMapping("/supplier")
public class MaterialSupplierController extends BaseController {

    @Autowired
    private IMaterialSupplierService supplierService;

    /**
     * 供应商列表
     */
    @GetMapping("/list")
    public TableDataInfo supplierList(MaterialSupplierListReq supplierListReq) {
        startPage();
        List<MaterialSupplier> list = supplierService.selectMaterialSupplierList(supplierListReq);
        return getDataTable(list);
    }

    /**
     * 获取供应商详情
     */
    @GetMapping(value = "/{id}")
    public AjaxResult memberList(@PathVariable("id") Long id) {
        return AjaxResult.success(supplierService.selectMaterialSupplierById(id));
    }

    /**
     * 新增
     */
    @Log(title = "新增供应商", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @Validated MaterialSupplierAddReq supplierAddReq) {
        supplierService.insertMaterialSupplier(supplierAddReq);
        return AjaxResult.success("新增成功");
    }

    /**
     * 修改
     */
    @Log(title = "修改供应商", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated MaterialSupplierEditReq supplierEditReq) {
        supplierService.updateMaterialSupplier(supplierEditReq);
        return AjaxResult.success("更新成功");
    }

    /**
     * 删除
     */
    @Log(title = "删除供应商", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") Long id) {
        supplierService.removeById(id);
        return AjaxResult.success("删除成功");
    }

}
