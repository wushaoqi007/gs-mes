package com.greenstone.mes.material.interfaces.rest;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.material.application.dto.cmd.WarehouseImportCmd;
import com.greenstone.mes.material.application.dto.cmd.WhQrcodePrintCmd;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.request.*;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.material.domain.service.IBaseWarehouseService;
import com.greenstone.mes.system.api.domain.SysFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * 仓库配置Controller
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
@Slf4j
@RestController
@RequestMapping("/warehouse")
public class BaseWarehouseController extends BaseController {
    @Autowired
    private IBaseWarehouseService baseWarehouseService;

    /**
     * 查询仓库配置列表
     */
    @GetMapping("/list")
    public TableDataInfo list(BaseWarehouse baseWarehouse) {
        startPage();
        List<BaseWarehouse> list = baseWarehouseService.selectBaseWarehouseList(baseWarehouse);
        return getDataTable(list);
    }


    /**
     * 获取仓库配置详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(baseWarehouseService.selectBaseWarehouseById(id));
    }

    @GetMapping(value = "/stage/{stage}")
    public AjaxResult getByStage(@PathVariable("stage") Integer stage) {
        return AjaxResult.success(baseWarehouseService.findOnlyOneByStage(stage));
    }

    /**
     * 获取仓库配置详细信息
     */
    @GetMapping(value = "/query")
    public AjaxResult query(BaseWarehouse warehouse) {
        if(StrUtil.isEmpty(warehouse.getCode())){
            throw new ServiceException("请输入仓库编码查询");
        }
        BaseWarehouse info = baseWarehouseService.queryWarehouseByCode(warehouse);
        if (Objects.isNull(info)) {
            throw new ServiceException(StrUtil.format("仓库不存在：{}", warehouse.getCode()));
        }
        return AjaxResult.success(info);
    }

    /**
     * 获取仓库配置列表信息
     */
    @GetMapping(value = "/query/all")
    public AjaxResult queryAll(BaseWarehouse warehouse) {
        return AjaxResult.success(baseWarehouseService.queryWarehouseList(warehouse));
    }

    /**
     * 新增仓库配置
     */
    @Log(title = "仓库配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @Validated WarehouseAddReq addReq) {
        BaseWarehouse baseWarehouse = BaseWarehouse.builder().code(addReq.getCode()).
                name(addReq.getName()).
                address(addReq.getAddress()).
                stage(addReq.getStage()).
                type(addReq.getType()).
                parentId(addReq.getParentId()).remark(addReq.getRemark()).build();
        return AjaxResult.success(baseWarehouseService.insertBaseWarehouse(baseWarehouse));
    }

    /**
     * 修改仓库配置
     */
    @Log(title = "仓库配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated WarehouseEditReq editReq) {
        BaseWarehouse baseWarehouse = BaseWarehouse.builder().id(editReq.getId()).
                name(editReq.getName()).
                address(editReq.getAddress()).
                stage(editReq.getStage()).
                type(editReq.getType()).
                parentId(editReq.getParentId()).remark(editReq.getRemark()).build();
        return toAjax(baseWarehouseService.updateBaseWarehouse(baseWarehouse));
    }

    /**
     * 删除仓库配置
     */
    @Log(title = "仓库配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(baseWarehouseService.deleteBaseWarehouseByIds(ids));
    }


    /**
     * 存放点绑定
     */
    @Log(title = "存放点绑定", businessType = BusinessType.INSERT)
    @PostMapping("/bind")
    public AjaxResult bind(@RequestBody @Validated WarehouseBindReq bindReq) {
        return AjaxResult.success(baseWarehouseService.bindWarehouse(bindReq));
    }

    /**
     * 存放点解绑
     */
    @Log(title = "存放点解绑", businessType = BusinessType.UPDATE)
    @PutMapping("/unbind")
    public AjaxResult unbind(@RequestBody @Validated WarehouseUnbindReq unbindReq) {
        baseWarehouseService.unBindWarehouse(unbindReq);
        return AjaxResult.success("解绑成功");
    }

    /**
     * 项目绑定
     */
    @Log(title = "项目绑定", businessType = BusinessType.INSERT)
    @PostMapping("/bind/project")
    public AjaxResult bindProject(@RequestBody @Validated WarehouseBindProjectCmd bindProjectCmd) {
        return AjaxResult.success(baseWarehouseService.bindProject(bindProjectCmd));
    }

    /**
     * 项目解绑
     */
    @Log(title = "项目解绑", businessType = BusinessType.UPDATE)
    @PutMapping("/unbind/project")
    public AjaxResult unbindProject(@RequestBody @Validated WarehouseBindProjectCmd bindProjectCmd) {
        baseWarehouseService.unBindProject(bindProjectCmd);
        return AjaxResult.success("解绑成功");
    }

    @PostMapping("/print")
    public AjaxResult printQrCode(@RequestBody WhQrcodePrintCmd printCmd) {
        SysFile file = baseWarehouseService.printQrCode(printCmd);
        return AjaxResult.success(file);
    }

    @Transactional
    @PostMapping("/import")
    public AjaxResult importWarehouse(MultipartFile file) {
        ExcelUtil<WarehouseImportCmd> importObjs = new ExcelUtil<>(WarehouseImportCmd.class);
        List<WarehouseImportCmd> impostList = importObjs.toList(file);
        baseWarehouseService.importWarehouse(impostList);
        return AjaxResult.success("导入完成");
    }
}