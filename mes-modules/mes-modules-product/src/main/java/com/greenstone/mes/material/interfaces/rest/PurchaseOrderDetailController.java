package com.greenstone.mes.material.interfaces.rest;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.material.domain.ProcessOrderDO;
import com.greenstone.mes.material.domain.ProcessOrderDetailDO;
import com.greenstone.mes.material.application.service.WorksheetManager;
import com.greenstone.mes.material.request.PartsBoardListReq;
import com.greenstone.mes.material.request.PurchaseOrderAbandonReq;
import com.greenstone.mes.material.request.PurchaseOrderDetailEditReq;
import com.greenstone.mes.material.request.WorksheetDetailListReq;
import com.greenstone.mes.material.response.PartBoardExportResp;
import com.greenstone.mes.material.domain.service.ExcelBuildService;
import com.greenstone.mes.material.domain.service.WorksheetDetailService;
import com.greenstone.mes.material.domain.service.WorksheetService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 采购单详情接口
 *
 * @author wushaoqi
 * @date 2022-05-17-13:04
 */
@Slf4j
@RestController
@RequestMapping("/purchase/detail")
public class PurchaseOrderDetailController extends BaseController {

    @Autowired
    private WorksheetManager worksheetManager;

    @Autowired
    private WorksheetDetailService worksheetDetailService;

    @Autowired
    private WorksheetService worksheetService;

    @Autowired
    private ExcelBuildService excelBuildService;


    /**
     * 加工单列表
     */
    @GetMapping(value = "/{id}")
    public TableDataInfo purchaseOrderDetail(@PathVariable("id") Long id, @RequestParam(value = "status", required = false) String status
            , @RequestParam(value = "code", required = false) String code, @RequestParam(value = "name", required = false) String name) {
        startPage();
        QueryWrapper<ProcessOrderDetailDO> queryWrapper = Wrappers.query(ProcessOrderDetailDO.builder().processOrderId(id).build());
        if (StrUtil.isNotBlank(status)) {
            queryWrapper.eq("status", status);
        }
        if (StrUtil.isNotBlank(code)) {
            queryWrapper.like("code", code);
        }
        if (StrUtil.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        List<ProcessOrderDetailDO> orderDetailList = worksheetDetailService.list(queryWrapper);
        for (ProcessOrderDetailDO processOrderDetailDO : orderDetailList) {
            processOrderDetailDO.setActualChangeNumber(null);
            processOrderDetailDO.setApplyNumber(null);
            processOrderDetailDO.setApplyReason("");
        }
        return getDataTable(orderDetailList);
    }

    /**
     * 加工单列表
     */
    @GetMapping(value = "/list")
    public TableDataInfo worksheetDetailList(WorksheetDetailListReq detailListReq) {
        startPage();
        return getDataTable(worksheetDetailService.selectWorksheetDetail(detailListReq));
    }

    /**
     * 单个加工单详情
     */
    @GetMapping
    public AjaxResult getPartOrderDetail(@RequestParam("partOrderCode") String partOrderCode,
                                         @RequestParam("componentCode") String componentCode,
                                         @RequestParam("partCode") String partCode,
                                         @RequestParam("partVersion") String partVersion) {
        ProcessOrderDO partOrderSelectEntity = ProcessOrderDO.builder().code(partOrderCode).build();
        ProcessOrderDO existOrder = worksheetService.getOneOnly(partOrderSelectEntity);
        if (existOrder == null) {
            log.error("Part order is not exist, code: {}", partOrderCode);
            throw new ServiceException(StrUtil.format("加工单编号不存在: {}", partOrderCode));
        }

        ProcessOrderDetailDO partOrderDetailSelectEntity = ProcessOrderDetailDO.builder().
                processOrderId(existOrder.getId()).
                componentCode(componentCode).
                code(partCode).
                version(partVersion).build();
        ProcessOrderDetailDO existDetail = worksheetDetailService.getOneOnly(partOrderDetailSelectEntity);
        if (existDetail == null) {
            log.error("Part order detail is not exist, order: {}, componentCode: {}, partCode: {}, partVersion: {}",
                    partOrderCode, componentCode, partCode, partVersion);
            throw new ServiceException(StrUtil.format("加工单零件不存在，订单号：{}，组件号：{}，零件号：{}，零件版本：{}",
                    partOrderCode, componentCode, partCode, partVersion));
        }
        return AjaxResult.success(existDetail);
    }

    /**
     * 加工单详情修改
     */
    @Log(title = "加工单详情修改", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PurchaseOrderDetailEditReq detailEditReq) {
        worksheetDetailService.updateById(ProcessOrderDetailDO.builder().id(detailEditReq.getId()).currentNumber(detailEditReq.getMaterialNumber())
                .provider(detailEditReq.getProvider()).processingTime(detailEditReq.getProcessingTime()).planTime(detailEditReq.getPlanTime()).build());
        return AjaxResult.success("更新成功");
    }

    /**
     * 采购单详情(零件废弃)
     */
    @Log(title = "采购单详情修改", businessType = BusinessType.UPDATE)
    @PutMapping("/abandon")
    public AjaxResult abandon(@RequestBody @Validated PurchaseOrderAbandonReq purchaseOrderAbandonReq) {
        worksheetManager.abandonPurchaseOrderDetail(purchaseOrderAbandonReq);
        return AjaxResult.success("更新成功");
    }

    /**
     * 零件看板列表
     */
    @GetMapping("/board/list")
    public TableDataInfo boardList(PartsBoardListReq partsBoardListReq) {
        startPage();
        List<PartBoardExportResp> list = worksheetManager.listPartBoardExportData(partsBoardListReq);
        return getDataTable(list);
    }

    /**
     * 零件看板导出
     */
    @Log(title = "采购单零件看板导出", businessType = BusinessType.EXPORT)
    @PostMapping("/board/export")
    public void exportData(HttpServletResponse response, @RequestBody PartsBoardListReq partsBoardListReq) {
        List<PartBoardExportResp> partBoardExportRespList = worksheetManager.listPartBoardExportData(partsBoardListReq);
        XSSFWorkbook workbook = excelBuildService.exportPartsBoard(partBoardExportRespList);
        new ExcelUtil<>().writeToHttp(response, workbook, "零件看板导出.xlsx");
    }
}
