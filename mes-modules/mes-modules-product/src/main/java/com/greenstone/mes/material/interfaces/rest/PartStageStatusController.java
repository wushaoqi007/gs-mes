package com.greenstone.mes.material.interfaces.rest;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.material.application.service.PartStageStatusManager;
import com.greenstone.mes.material.request.PartsReworkReq;
import com.greenstone.mes.material.request.PartsReworkStatReq;
import com.greenstone.mes.material.request.PartsUsedReq;
import com.greenstone.mes.material.response.PartReworkResp;
import com.greenstone.mes.material.response.PartReworkStatResp;
import com.greenstone.mes.material.response.PartUsedResp;
import com.greenstone.mes.material.domain.service.PartStageStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

/**
 * 零件的阶段状态
 *
 * @author wushaoqi
 * @date 2022-12-16-8:12
 */
@Slf4j
@RestController
@RequestMapping("/part/stage")
public class PartStageStatusController extends BaseController {

    private final PartStageStatusService partStageStatusService;
    private final PartStageStatusManager partStageStatusManager;

    @Autowired
    public PartStageStatusController(PartStageStatusService partStageStatusService, PartStageStatusManager partStageStatusManager) {
        this.partStageStatusService = partStageStatusService;
        this.partStageStatusManager = partStageStatusManager;
    }

    /**
     * 已领用零件查询
     */
    @GetMapping("/list/used")
    public TableDataInfo selectPartUsedList(PartsUsedReq partsUsedReq) {
        startPage();
        List<PartUsedResp> partUsedRespList = partStageStatusService.selectPartUsedList(partsUsedReq);
        return getDataTable(partUsedRespList);
    }

    /**
     * 已领用零件导出
     */
    @Log(title = "已领用零件导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export/used")
    public void exportPartUsed(HttpServletResponse response, @RequestBody(required = false) PartsUsedReq partsUsedReq) {
        if (Objects.isNull(partsUsedReq) || (StrUtil.isEmpty(partsUsedReq.getProjectCode()) && (Objects.isNull(partsUsedReq.getStartTime()) || Objects.isNull(partsUsedReq.getEndTime())))) {
            throw new ServiceException(BizError.E30002);
        }
        List<PartUsedResp> partUsedRespList = partStageStatusService.selectPartUsedList(partsUsedReq);
        ExcelUtil<PartUsedResp> util = new ExcelUtil<>(PartUsedResp.class);
        util.exportExcel(response, partUsedRespList, "已领用零件导出");
    }

    /**
     * 返工零件查询
     */
    @GetMapping("/list/rework")
    public TableDataInfo selectPartReworkList(PartsReworkReq partsReworkReq) {
        startPage();
        List<PartReworkResp> partReworkRespList = partStageStatusService.selectPartReworkList(partsReworkReq);
        return getDataTable(partReworkRespList);
    }

    /**
     * 返工零件导出
     */
    @Log(title = "返工零件导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export/rework")
    public void exportPartRework(HttpServletResponse response, @RequestBody(required = false) PartsReworkReq partsReworkReq) {
        if (Objects.isNull(partsReworkReq) || (StrUtil.isEmpty(partsReworkReq.getProjectCode()) && (Objects.isNull(partsReworkReq.getStartTime()) || Objects.isNull(partsReworkReq.getEndTime())))) {
            throw new ServiceException(BizError.E30002);
        }
        List<PartReworkResp> partReworkRespList = partStageStatusService.selectPartReworkList(partsReworkReq);
        ExcelUtil<PartReworkResp> util = new ExcelUtil<>(PartReworkResp.class);
        util.exportExcel(response, partReworkRespList, "返工零件导出");
    }

    /**
     * 月返工率查询
     */
    @GetMapping(value = "/month/rework/stat")
    public AjaxResult reworkStat(PartsReworkStatReq partsReworkStatReq) {
        if (Objects.isNull(partsReworkStatReq) || Objects.isNull(partsReworkStatReq.getStartTime()) || Objects.isNull(partsReworkStatReq.getEndTime())) {
            throw new ServiceException(BizError.E30001);
        }
        List<PartReworkStatResp> partReworkStatRespList = partStageStatusManager.reworkStat(partsReworkStatReq);
        return AjaxResult.success(partReworkStatRespList);
    }

    /**
     * 月返工率导出
     */
    @Log(title = "月返工率导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export/month/rework/stat")
    public void exportMonthReworkStat(HttpServletResponse response, @RequestBody(required = false) PartsReworkStatReq partsReworkStatReq) {
        if (Objects.isNull(partsReworkStatReq) || Objects.isNull(partsReworkStatReq.getStartTime()) || Objects.isNull(partsReworkStatReq.getEndTime())) {
            throw new ServiceException(BizError.E30001);
        }
        List<PartReworkStatResp> partReworkStatRespList = partStageStatusManager.reworkStat(partsReworkStatReq);
        ExcelUtil<PartReworkStatResp> util = new ExcelUtil<>(PartReworkStatResp.class);
        util.exportExcel(response, partReworkStatRespList, "月返工率导出");
    }
}
