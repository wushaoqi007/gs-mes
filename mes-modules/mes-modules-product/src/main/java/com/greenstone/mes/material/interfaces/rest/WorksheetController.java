package com.greenstone.mes.material.interfaces.rest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.material.application.dto.WorkSheetCheckCountQuery;
import com.greenstone.mes.material.application.dto.WorkSheetPlaceOrderQuery;
import com.greenstone.mes.material.application.dto.result.WorksheetCheckCountR;
import com.greenstone.mes.material.application.dto.result.WorksheetPlaceOrderR;
import com.greenstone.mes.material.cqe.command.WorksheetImportCommand;
import com.greenstone.mes.material.cqe.command.WorksheetImportEditCommand;
import com.greenstone.mes.material.domain.PartOrderCompareTemp;
import com.greenstone.mes.material.domain.ProcessOrderDetailDO;
import com.greenstone.mes.material.interfaces.transfer.WorksheetTransfer;
import com.greenstone.mes.material.application.service.WorksheetManager;
import com.greenstone.mes.material.request.*;
import com.greenstone.mes.material.domain.service.PartOrderCompareTempService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 加工单控制类
 *
 * @author gu_renkai
 * @date 2022-08-03
 */
@Slf4j
@RestController
@RequestMapping("/part/order")
public class WorksheetController extends BaseController {

    private final PartOrderCompareTempService partOrderCompareTempService;

    private final WorksheetManager worksheetManager;

    private final WorksheetTransfer worksheetTransfer;

    @Autowired
    public WorksheetController(PartOrderCompareTempService partOrderCompareTempService,
                               WorksheetManager worksheetManager,
                               WorksheetTransfer worksheetTransfer) {
        this.partOrderCompareTempService = partOrderCompareTempService;
        this.worksheetManager = worksheetManager;
        this.worksheetTransfer = worksheetTransfer;
    }

    @PostMapping
    public AjaxResult addPartOrder(@Validated @RequestBody PartOrderAddReq partOrderAddReq) {
        worksheetManager.addPartOrder(partOrderAddReq);
        return AjaxResult.success("新增成功");
    }

    @GetMapping("/{orderId}/compare/temp")
    public AjaxResult getTempCompareResult(@PathVariable Long orderId) {
        PartOrderCompareTemp selectEntity = PartOrderCompareTemp.builder().orderId(orderId).build();
        List<PartOrderCompareTemp> tempList = partOrderCompareTempService.list(new QueryWrapper<>(selectEntity));
        return AjaxResult.success(tempList);
    }

    @PostMapping("/compare/temp")
    public AjaxResult saveTempCompareResult(@RequestBody List<PartOrderCompareTemp> tempList) {
        log.info("save part temp compare data");
        if (CollUtil.isNotEmpty(tempList)) {
            PartOrderCompareTemp removeEntity = PartOrderCompareTemp.builder().orderId(tempList.get(0).getOrderId()).build();
            partOrderCompareTempService.remove(new QueryWrapper<>(removeEntity));
            partOrderCompareTempService.saveBatch(tempList);
        }
        return AjaxResult.success();
    }

    /**
     * 机加工单导入(一厂)
     */
    @Log(title = "机加工单", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importPartOrder(MultipartFile file) {
        log.info("Receive process order import request");
        // 将表格转为VO
        List<ProcessOrderF1ImportVO> importVOs = new ExcelUtil<>(ProcessOrderF1ImportVO.class).toList(file);
        log.info("Import content size: {}", importVOs.size());
        // 将序号为空的数据排除(可能有空格等不可见的字符，导致存在数据但是并不需要导入的行)
        importVOs = importVOs.stream().filter(d -> StrUtil.isNotBlank(d.getSeqNum())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(importVOs)) {
            throw new ServiceException("数据序号不能为空。");
        }
        log.info("Import content size after empty filter: {}", importVOs.size());
        // 处理加工单的导入
        List<WorksheetImportCommand.PartImportCommand> partImportCommands = worksheetTransfer.toF1PartImportCommands(importVOs);
        WorksheetImportCommand importCommand = WorksheetImportCommand.builder().company(1).partImportCommands(partImportCommands).build();
        worksheetManager.importWorksheet(importCommand);
        return AjaxResult.success();
    }

    /**
     * 机加工单导入(二厂)
     */
    @Log(title = "机加工单", businessType = BusinessType.IMPORT)
    @PostMapping("/import/secondFactory")
    public AjaxResult importSecondFactoryData(MultipartFile file) {
        log.info("Receive bom import request");
        // 将表格转为VO
        List<ProcessOrderF2ImportVO> importVOs = new ExcelUtil<>(ProcessOrderF2ImportVO.class).toList(file);
        // 将序号为空的数据排除(可能有空格等不可见的字符，导致存在数据但是并不需要导入的行)
        importVOs = importVOs.stream().filter(d -> StrUtil.isNotBlank(d.getSeqNum())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(importVOs)) {
            return AjaxResult.error("数据序号不能为空。");
        }
        // 处理加工单的导入
        List<WorksheetImportCommand.PartImportCommand> partImportCommands = worksheetTransfer.toF2PartImportCommands(importVOs);
        WorksheetImportCommand importCommand = WorksheetImportCommand.builder().company(2).partImportCommands(partImportCommands).build();
        worksheetManager.importWorksheet(importCommand);
        return AjaxResult.success();
    }

    /**
     * 机加工单修改
     */
    @Log(title = "机加工单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated PartOrderEditReq partOrderEditReq) {
        worksheetManager.editPurchaseOrder(partOrderEditReq);
        return AjaxResult.success("更新成功");
    }

    /**
     * 机加工单变更申请
     */
    @Log(title = "机加工单", businessType = BusinessType.UPDATE)
    @PutMapping("/change/apply")
    public AjaxResult changeApply(@RequestBody @Validated PurchaseOrderChangeApplyReq purchaseOrderChangeApplyReq) {
        worksheetManager.changeApplyPurchaseOrder(purchaseOrderChangeApplyReq);
        return AjaxResult.success("变更申请成功");
    }

    /**
     * 机加工单变更确认
     */
    @Log(title = "机加工单", businessType = BusinessType.UPDATE)
    @PutMapping("/change/confirm")
    public AjaxResult changeConfirm(@RequestBody @Validated PurchaseOrderChangeConfirmReq purchaseOrderChangeConfirmReq) {
        worksheetManager.changeConfirmPurchaseOrder(purchaseOrderChangeConfirmReq);
        return AjaxResult.success("变更确认成功");
    }

    @GetMapping("/change/detail/{orderId}")
    public AjaxResult getChangeDetail(@PathVariable Long orderId) {
        List<ProcessOrderDetailDO> orderDetailList = worksheetManager.getChangeDetail(orderId);
        for (ProcessOrderDetailDO processOrderDetailDO : orderDetailList) {
            processOrderDetailDO.setActualChangeNumber(null);
        }
        return AjaxResult.success(orderDetailList);
    }

    /**
     * 零件信息录入
     */
    @Log(title = "零件信息录入", businessType = BusinessType.UPDATE)
    @PutMapping("/detail/list")
    public AjaxResult infoEdit(@RequestBody @Validated List<PartOrderInfoEdit> partOrderInfoEditList) {
        worksheetManager.updatePartOrderInfo(partOrderInfoEditList);
        return AjaxResult.success("更新成功");
    }

    /**
     * 机加工单导入批量修改
     */
    @Log(title = "机加工单", businessType = BusinessType.IMPORT)
    @PutMapping("/import")
    public AjaxResult editByImportWorksheet(MultipartFile file) {
        log.info("Receive worksheet import to edit request");
        // 将表格转为VO
        List<WorksheetImportEditVO> importVOs = new ExcelUtil<>(WorksheetImportEditVO.class).toList(file);
        log.info("Import content size: {}", importVOs.size());
        // 处理加工单的导入
        List<WorksheetImportEditCommand.PartImportEditCommand> partImportEditCommands = worksheetTransfer.toPartEditImportCommands(importVOs);
        WorksheetImportEditCommand importEditCommand = WorksheetImportEditCommand.builder().partImportEditCommands(partImportEditCommands).build();
        worksheetManager.importEditWorksheet(importEditCommand);
        return AjaxResult.success();
    }

    /**
     * 删除加工单
     */
    @Log(title = "加工单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") Long id) {
        worksheetManager.removeWorksheetById(id);
        return AjaxResult.success("删除成功");
    }

    /**
     * 设计下单
     */
    @GetMapping("/designer/place")
    public AjaxResult placeOrderList(@Validated WorkSheetPlaceOrderQuery placeOrderQuery) {
        List<WorksheetPlaceOrderR> list = worksheetManager.selectWorksheetPlaceOrderList(placeOrderQuery);
        return AjaxResult.success(list);
    }

    /**
     * 检验数量
     */
    @GetMapping("/check/count")
    public AjaxResult checkCountList(@Validated WorkSheetCheckCountQuery checkCountQuery) {
        List<WorksheetCheckCountR> list = worksheetManager.selectWorksheetCheckCountList(checkCountQuery);
        return AjaxResult.success(list);
    }

    /**
     * 检验数量导出
     */
    @Log(title = "检验数量导出", businessType = BusinessType.EXPORT)
    @PostMapping("/check/count/export")
    public void exportData(HttpServletResponse response, @RequestBody @Validated WorkSheetCheckCountQuery checkCountQuery) {
        List<WorksheetCheckCountR> list = worksheetManager.selectWorksheetCheckCountList(checkCountQuery);
        ExcelUtil<WorksheetCheckCountR> util = new ExcelUtil<>(WorksheetCheckCountR.class);
        util.exportExcel(response, list, "检验数量导出");
    }
}