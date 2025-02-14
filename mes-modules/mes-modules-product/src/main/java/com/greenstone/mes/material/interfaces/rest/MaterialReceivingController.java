package com.greenstone.mes.material.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.material.application.service.MaterialReceivingManager;
import com.greenstone.mes.material.request.MaterialReceivingAddReq;
import com.greenstone.mes.material.request.MaterialReceivingEditReq;
import com.greenstone.mes.material.request.MaterialReceivingListReq;
import com.greenstone.mes.material.response.MaterialReceivingDetailResp;
import com.greenstone.mes.material.response.MaterialReceivingListResp;
import com.greenstone.mes.material.domain.service.IMaterialReceivingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 领料单接口
 *
 * @author wushaoqi
 * @date 2022-08-15-8:34
 */
@Slf4j
@RestController
@RequestMapping("/receiving")
public class MaterialReceivingController extends BaseController {

    @Autowired
    private MaterialReceivingManager receivingManager;

    @Autowired
    private IMaterialReceivingService receivingService;


    /**
     * 领料单列表
     */
    @GetMapping("/list")
    public TableDataInfo list(MaterialReceivingListReq receivingListReq) {
        startPage();
        List<MaterialReceivingListResp> list = receivingService.selectMaterialReceivingList(receivingListReq);
        return getDataTable(list);
    }

    /**
     * 新增领料单
     */
    @Log(title = "新增领料单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @Validated MaterialReceivingAddReq receivingAddReq) {
        receivingManager.insertMaterialReceiving(receivingAddReq);
        return AjaxResult.success("新增成功");
    }

    /**
     * 领料单详情
     */
    @GetMapping(value = "/{id}")
    public TableDataInfo detail(@PathVariable("id") Long id) {
        startPage();
        List<MaterialReceivingDetailResp> list = receivingService.selectMaterialReceivingDetailById(id);
        List<MaterialReceivingDetailResp> respList = receivingManager.completeReceivingDetail(list);
        return getDataTable(respList);
    }

    /**
     * 备料
     */
    @GetMapping(value = "/preparing/{id}")
    public AjaxResult prepare(@PathVariable("id") Long id) {
        List<MaterialReceivingDetailResp> respList = receivingManager.prepare(id);
        return AjaxResult.success(respList);
    }

    /**
     * 修改领料单状态
     */
    @Log(title = "修改领料单", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public AjaxResult changeStatus(@RequestBody @Validated MaterialReceivingEditReq materialReceivingEditReq) {
        receivingManager.updateMaterialReceivingStatus(materialReceivingEditReq);
        return AjaxResult.success("更新成功");
    }

    /**
     * 接收领料单
     */
    @Log(title = "修改领料单", businessType = BusinessType.UPDATE)
    @PutMapping("/status/receiving")
    public AjaxResult receiving(@RequestBody @Validated MaterialReceivingEditReq materialReceivingEditReq) {
        receivingManager.receiveMaterialReceiving(materialReceivingEditReq);
        return AjaxResult.success("更新成功");
    }

    /**
     * 获取领料单状态列表
     */
    @GetMapping(value = "/status/list/{id}")
    public AjaxResult getStatusInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(receivingManager.getStatusListById(id));
    }

    /**
     * 删除领料单
     */
    @Log(title = "领料单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") Long id) {
        receivingService.removeById(id);
        return AjaxResult.success("删除成功");
    }

}
