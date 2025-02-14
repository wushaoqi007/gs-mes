package com.greenstone.mes.material.interfaces.rest;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.request.MaterialAddReq;
import com.greenstone.mes.material.request.MaterialEditReq;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.material.domain.service.IBaseMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 物料配置Controller
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
@RestController
@RequestMapping("/material")
public class BaseMaterialController extends BaseController {
    @Autowired
    private IBaseMaterialService baseMaterialService;

    /**
     * 查询物料配置列表
     */
    @GetMapping("/list")
    public TableDataInfo list(BaseMaterial baseMaterial) {
        startPage();
        List<BaseMaterial> list = baseMaterialService.selectBaseMaterialList(baseMaterial);
        return getDataTable(list);
    }

    /**
     * 获取物料配置详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(baseMaterialService.getById(id));
    }

    /**
     * 获取物料配置详细信息
     */
    @GetMapping(value = "/query")
    public AjaxResult getInfoByCode(BaseMaterial material) {
        BaseMaterial baseMaterial = baseMaterialService.queryBaseMaterial(material);
        if (Objects.isNull(baseMaterial)) {
            throw new ServiceException(MachineError.E200005, StrUtil.format("物料编码/版本：{}/{}", material.getCode(), material.getVersion()));
        }
        return AjaxResult.success(baseMaterial);
    }

    /**
     * 新增物料配置
     */
    @Log(title = "物料配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @Validated MaterialAddReq addReq) {
        BaseMaterial baseMaterial = BaseMaterial.builder().code(addReq.getCode()).
                name(addReq.getName()).
                version(addReq.getVersion()).
                unit(addReq.getUnit()).
                rawMaterial(addReq.getRawMaterial()).
                surfaceTreatment(addReq.getSurfaceTreatment()).
                weight(addReq.getWeight()).
                type(addReq.getType()).build();

        return AjaxResult.success(baseMaterialService.insertBaseMaterial(baseMaterial, addReq.isUpdateSupport()));
    }

    /**
     * 修改物料配置
     */
    @Log(title = "物料配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated MaterialEditReq editReq) {
        BaseMaterial baseMaterial = BaseMaterial.builder().id(editReq.getId()).
                name(editReq.getName()).
                unit(editReq.getUnit()).
                type(editReq.getType()).build();
        return toAjax(baseMaterialService.updateBaseMaterial(baseMaterial));
    }

    /**
     * 删除物料配置
     */
    @Log(title = "物料配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(baseMaterialService.deleteBaseMaterialByIds(ids));
    }
}