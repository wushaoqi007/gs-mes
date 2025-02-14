package com.greenstone.mes.bom.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.greenstone.mes.bom.domain.BomImportDetail;
import com.greenstone.mes.bom.domain.BomImportRecord;
import com.greenstone.mes.bom.domain.ComparisonDetail;
import com.greenstone.mes.bom.domain.DrawingComparison;
import com.greenstone.mes.bom.manager.BomManager;
import com.greenstone.mes.bom.request.ComparisonAddReq;
import com.greenstone.mes.bom.service.BomImportDetailService;
import com.greenstone.mes.bom.service.IBomImportRecordService;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-05-16-13:17
 */
@Slf4j
@RestController
@RequestMapping("/comparison")
public class DrawingComparisonController extends BaseController {

    @Autowired
    private BomManager bomManager;

    @Autowired
    private IBomImportRecordService bomImportRecordService;

    @Autowired
    private BomImportDetailService bomImportDetailService;

    /**
     * 新增图纸比对
     */
    @Log(title = "BOM", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @Validated ComparisonAddReq addRequest) {
        BomImportRecord bomImportRecord = bomImportRecordService.selectBomImportRecordById(addRequest.getImportRecordId());
        if (bomImportRecord == null) {
            log.error("未找到导入记录");
            throw new ServiceException("未找到导入记录");
        }
        DrawingComparison drawingComparison = DrawingComparison.builder().bomImportRecordId(addRequest.getImportRecordId()).build();

        // 计算百分比
        double percentage = 0.0;
        // 零件总数量
        double total = 0;
        QueryWrapper<BomImportDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("material_number", 0);
        queryWrapper.eq("record_id", addRequest.getImportRecordId());
        List<BomImportDetail> list = bomImportDetailService.list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            total = list.size();
        }
        // 已完成数量
        double finish = 0;
        List<ComparisonDetail> comparisonDetails = new ArrayList<>();
        for (ComparisonAddReq.Result result : addRequest.getResults()) {
            if (result.getImportDetailId() != null) {
                BomImportDetail bomImportDetail = bomImportDetailService.getById(result.getImportDetailId());
                if (bomImportDetail == null) {
                    log.error("未找到导入记录详情，详情id:" + result.getImportDetailId());
                    throw new ServiceException("未找到导入记录详情，详情id:" + result.getImportDetailId());
                }
                if (bomImportDetail.getMaterialNumber() != null && bomImportDetail.getMaterialNumber().intValue() != 0) {
                    if (bomImportDetail.getPaperNumber().intValue() == result.getScanNumber().intValue()) {
                        finish++;
                    }
                }
                ComparisonDetail comparisonDetail = ComparisonDetail.builder().
                        bomImportDetailId(result.getImportDetailId()).
                        result(result.getResult()).
                        scanNumber(result.getScanNumber()).build();
                comparisonDetails.add(comparisonDetail);
            }

        }
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2);
        percentage = Double.parseDouble(format.format(finish / total * 100));
        drawingComparison.setPercentage(percentage);
        bomManager.addComparisonResultAndDetail(drawingComparison, comparisonDetails);
        return AjaxResult.success("新增成功");
    }
}
