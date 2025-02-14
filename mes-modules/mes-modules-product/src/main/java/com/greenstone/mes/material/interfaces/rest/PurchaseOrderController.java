package com.greenstone.mes.material.interfaces.rest;

import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.material.application.service.WorksheetManager;
import com.greenstone.mes.material.request.*;
import com.greenstone.mes.material.response.PurchaseOrderExportResp;
import com.greenstone.mes.material.response.PurchaseOrderListResp;
import com.greenstone.mes.material.domain.service.WorksheetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 采购单接口
 *
 * @author wushaoqi
 * @date 2022-05-17-13:04
 */
@Slf4j
@RestController
@RequestMapping("/purchase")
public class PurchaseOrderController extends BaseController {


    @Autowired
    private WorksheetManager worksheetManager;

    @Autowired
    private WorksheetService worksheetService;

    /**
     * 生成采购单
     */
    @Log(title = "生成采购单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody PurchaseOrderAddReq purchaseOrderAddReq) {
        String projectCode = null;
        String partOrderCode = null;
        for (PurchaseOrderAddReq.PurchaseOrderInfo purchaseOrderInfo : purchaseOrderAddReq.getList()) {
            if (projectCode == null) {
                projectCode = purchaseOrderInfo.getProjectCode();
            } else if (!projectCode.equals(purchaseOrderInfo.getProjectCode())) {
                log.error("More than one project code in one part order");
                throw new ServiceException("一个机加工单中的零件必须属于同一个项目代码");
            }

            if (partOrderCode == null) {
                partOrderCode = purchaseOrderInfo.getPartOrderCode();
            } else if (!partOrderCode.equals(purchaseOrderInfo.getPartOrderCode())) {
                log.error("More than one part order code in one part order");
                throw new ServiceException("一个机加工单的单号必须一致");
            }
        }

        worksheetManager.purchaseOrderAdd(purchaseOrderAddReq);
        return AjaxResult.success("新增成功");
    }


    /**
     * 采购单列表查询
     */
    @GetMapping("/list")
    public TableDataInfo purchaseOrderList(PurchaseOrderListReq purchaseOrderListReq) {
        startPage();
        List<PurchaseOrderListResp> list = worksheetService.selectPurchaseOrderList(purchaseOrderListReq);
        return getDataTable(list);
    }

    /**
     * 废弃采购单
     */
    @Log(title = "采购单废弃", businessType = BusinessType.UPDATE)
    @PutMapping("/abandon")
    public AjaxResult abandon(@RequestBody PurchaseOrderEditReq purchaseOrderEditReq) {
        worksheetManager.giveUpPurchaseOrder(purchaseOrderEditReq);
        return AjaxResult.success("更新成功");
    }

    /**
     * 确认采购单
     */
    @Log(title = "采购单确认", businessType = BusinessType.UPDATE)
    @PutMapping("/confirm")
    public AjaxResult confirm(@RequestBody @Validated PurchaseOrderConfirmReq purchaseOrderConfirmReq) {
        worksheetManager.confirmPurchaseOrder(purchaseOrderConfirmReq);
        return AjaxResult.success("更新成功");
    }

    /**
     * 采购单导出
     */
    @Log(title = "采购单导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void exportData(HttpServletResponse response, @RequestBody @Validated PurchaseOrderExportReq exportReq) {
        List<PurchaseOrderExportResp> purchaseOrderExportResps = worksheetManager.exportPurchaseOrder(exportReq.getId());
        ExcelUtil<PurchaseOrderExportResp> util = new ExcelUtil<>(PurchaseOrderExportResp.class);
        util.exportExcel(response, purchaseOrderExportResps, "采购单导出");
    }

}
