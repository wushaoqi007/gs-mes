package com.greenstone.mes.material.interfaces.rest;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.material.domain.MaterialQualityInspectionRecord;
import com.greenstone.mes.material.application.service.MaterialStockManager;
import com.greenstone.mes.material.request.MaterialComplainStatisticsReq;
import com.greenstone.mes.material.request.MaterialQualityInspectionInStockReq;
import com.greenstone.mes.material.request.MaterialQualityInspectionListReq;
import com.greenstone.mes.material.request.MaterialQualityStatisticsReq;
import com.greenstone.mes.material.domain.service.IMaterialQualityInspectionRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 质检
 *
 * @author wushaoqi
 * @date 2022-10-24-10:21
 */
@Slf4j
@RestController
@RequestMapping("/quality")
public class MaterialQualityInspectionController extends BaseController {

    @Autowired
    private IMaterialQualityInspectionRecordService qualityInspectionRecordService;

    @Autowired
    private MaterialStockManager stockManager;

    /**
     * 质检入库（PC）
     */
    @Log(title = "质检入库", businessType = BusinessType.INSERT)
    @PostMapping("/inspection/in/addFromPc")
    public AjaxResult inspectionInPc(@Validated MaterialQualityInspectionInStockReq inStockRequest) {
        log.info("Request data: {}", inStockRequest);
        // 组件号处理：如果是两位数字需要拼接项目代码-组件号
        if (StrUtil.isNumeric(inStockRequest.getComponentCode()) && inStockRequest.getComponentCode().length() == 2) {
            inStockRequest.setComponentCode(inStockRequest.getProjectCode() + "-" + inStockRequest.getComponentCode());
        }
//        stockManager.qualityInspectionInStock(inStockRequest);
        return AjaxResult.success();
    }

    /**
     * 质检入库(小程序)
     */
    @Log(title = "质检入库", businessType = BusinessType.INSERT)
    @PostMapping("/inspection/in")
    public AjaxResult inspectionInApp(@RequestBody @Validated MaterialQualityInspectionInStockReq inStockRequest) {
        log.info("Request data: {}", inStockRequest);
        // 组件号处理：如果是两位数字需要拼接项目代码-组件号
        if (StrUtil.isNumeric(inStockRequest.getComponentCode()) && inStockRequest.getComponentCode().length() == 2) {
            inStockRequest.setComponentCode(inStockRequest.getProjectCode() + "-" + inStockRequest.getComponentCode());
        }
//        stockManager.qualityInspectionInStock(inStockRequest);
        return AjaxResult.success();
    }

    /**
     * 质检记录列表查询
     */
    @GetMapping("/list")
    public TableDataInfo selectComplaintRecordList(MaterialQualityInspectionListReq qualityInspectionRecord) {
        startPage();
        List<MaterialQualityInspectionRecord> list = qualityInspectionRecordService.getQualityInspectionList(qualityInspectionRecord);
        return getDataTable(list);
    }

    /**
     * 查询加工单位的返工率
     */
    @GetMapping("/statistics/rework/provider")
    public AjaxResult statisticsRework(@Validated MaterialComplainStatisticsReq statisticsReq) {
        return AjaxResult.success(qualityInspectionRecordService.selectReworkStatistics(statisticsReq));
    }

    /**
     * 查部门（问题环节）投诉率统计
     */
    @GetMapping("/statistics/complaint")
    public AjaxResult statisticsComplaint(@Validated MaterialComplainStatisticsReq statisticsReq) {
        return AjaxResult.success(qualityInspectionRecordService.selectComplaintStatistics(statisticsReq));
    }

    /**
     * 查询当天质检检验数量统计
     */
    @GetMapping("/statistics/hour")
    public AjaxResult statisticsHour(@Validated MaterialQualityStatisticsReq qualityStatisticsReq) {
        return AjaxResult.success(qualityInspectionRecordService.selectHourStatistics(qualityStatisticsReq));
    }

}
