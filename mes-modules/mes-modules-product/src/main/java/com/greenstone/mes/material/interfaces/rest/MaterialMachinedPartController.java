package com.greenstone.mes.material.interfaces.rest;

import cn.hutool.core.collection.CollectionUtil;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.material.domain.MaterialMachinedPart;
import com.greenstone.mes.material.dto.MachinedPartImportDto;
import com.greenstone.mes.material.application.service.MachinedPartManager;
import com.greenstone.mes.material.request.MachinedPartExportReq;
import com.greenstone.mes.material.request.MachinedPartsListReq;
import com.greenstone.mes.material.domain.service.IMaterialMachinedPartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 机加工件Controller
 *
 * @author gu_renkai
 * @date 2022-03-08
 */
@Slf4j
@RestController
@RequestMapping("/machinedParts")
public class MaterialMachinedPartController extends BaseController {
    @Autowired
    private IMaterialMachinedPartService materialMachinedPartsService;

    @Autowired
    private MachinedPartManager machinedPartManager;

    /**
     * 查询机加工件列表
     */
    @GetMapping("/list")
    public TableDataInfo list(MachinedPartsListReq req) {
        startPage();
        List<MaterialMachinedPart> list = materialMachinedPartsService.selectMaterialMachinedPartList(req);
        return getDataTable(list);
    }

    @Log(title = "机加工件", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importData(MultipartFile file) throws Exception {
        log.info("receive machinedPart import request");
        ExcelUtil<MachinedPartImportDto> util = new ExcelUtil<>(MachinedPartImportDto.class);
        List<MachinedPartImportDto> partList = util.importExcel(file.getInputStream());
        if (CollectionUtil.isNotEmpty(partList)) {
            machinedPartManager.importData(partList);
            return AjaxResult.success();
        } else {
            return AjaxResult.error("导入的数据不能为空");
        }
    }

    /**
     * 导出加工件采购信息
     */
    @Log(title = "机加工件导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void exportPartData(HttpServletResponse response, @RequestBody @Validated MachinedPartExportReq exportReq) {
        List<MaterialMachinedPart> exportResp = materialMachinedPartsService.exportMachinedPart(exportReq);
        ExcelUtil<MaterialMachinedPart> util = new ExcelUtil<>(MaterialMachinedPart.class);
        util.exportExcel(response, exportResp, "机加工件导出数据");
    }

    /**
     * 获取机加工件详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(materialMachinedPartsService.selectMaterialMachinedPartById(id));
    }

    /**
     * 新增机加工件
     */
    @Log(title = "机加工件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MaterialMachinedPart materialMachinedPart) {
        return toAjax(materialMachinedPartsService.insertMaterialMachinedPart(materialMachinedPart));
    }

    /**
     * 修改机加工件
     */
    @Log(title = "机加工件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MaterialMachinedPart materialMachinedPart) {
        return toAjax(materialMachinedPartsService.updateMaterialMachinedPart(materialMachinedPart));
    }

    /**
     * 删除机加工件
     */
    @Log(title = "机加工件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(materialMachinedPartsService.deleteMaterialMachinedPartByIds(ids));
    }
}