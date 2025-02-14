package com.greenstone.mes.material.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.domain.service.ExcelBuildService;
import com.greenstone.mes.material.response.PartBoardExportResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gu_renkai
 * @date 2022/8/9 9:13
 */

@Slf4j
@Service
public class ExcelBuildServiceImpl implements ExcelBuildService {

    @Override
    public XSSFWorkbook exportPartsBoard(List<PartBoardExportResp> respList) {
        // 排序：入库时间
        respList = respList.stream().sorted(Comparator.comparing(PartBoardExportResp::getIsFast, Comparator.nullsLast(Comparator.naturalOrder())).
                thenComparing(PartBoardExportResp::getReceiveDelay, Comparator.nullsLast(Comparator.naturalOrder())).
                thenComparing(PartBoardExportResp::getInStockDelay, Comparator.nullsLast(Comparator.naturalOrder())).
                thenComparing(PartBoardExportResp::getCreateTime)).collect(Collectors.toList());
        // 创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 创建Sheet页
        XSSFSheet sheet = workbook.createSheet();
        // 表头样式
        XSSFCellStyle fontCellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setBold(true);
        fontCellStyle.setFont(font);
        XSSFCellStyle redCellStyle = workbook.createCellStyle();
        XSSFFont redFont1 = workbook.createFont();
        redFont1.setColor(Font.COLOR_RED);
        redCellStyle.setFont(redFont1);
        // 设置垂直居中
        redCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 第一行为标题
        XSSFRow rowTitle = sheet.createRow(0);
        XSSFCell cellIndex = rowTitle.createCell(0);
        cellIndex.setCellValue("序号");
        cellIndex.setCellStyle(fontCellStyle);
        XSSFCell cellProvider = rowTitle.createCell(1);
        cellProvider.setCellValue("加工单位");
        cellProvider.setCellStyle(fontCellStyle);
        XSSFCell cellProjectCode = rowTitle.createCell(2);
        cellProjectCode.setCellValue("生产代码");
        cellProjectCode.setCellStyle(fontCellStyle);
        XSSFCell cellComponentName = rowTitle.createCell(3);
        cellComponentName.setCellValue("机种名称");
        cellComponentName.setCellStyle(fontCellStyle);
        XSSFCell cellName = rowTitle.createCell(4);
        cellName.setCellValue("零件名称");
        cellName.setCellStyle(fontCellStyle);
        XSSFCell cellRawMaterial = rowTitle.createCell(5);
        cellRawMaterial.setCellValue("材料");
        cellRawMaterial.setCellStyle(fontCellStyle);
        XSSFCell cellSurface = rowTitle.createCell(6);
        cellSurface.setCellValue("表面处理");
        cellSurface.setCellStyle(fontCellStyle);
        XSSFCell cellIsFast = rowTitle.createCell(7);
        cellIsFast.setCellValue("是否加急");
        cellIsFast.setCellStyle(fontCellStyle);
        XSSFCell cellMaterialNumber = rowTitle.createCell(8);
        cellMaterialNumber.setCellValue("订单数量");
        cellMaterialNumber.setCellStyle(fontCellStyle);
        XSSFCell cellGetNumber = rowTitle.createCell(9);
        cellGetNumber.setCellValue("收货数量");
        cellGetNumber.setCellStyle(fontCellStyle);
        XSSFCell cellReceivingTime = rowTitle.createCell(10);
        cellReceivingTime.setCellValue("收货日期");
        cellReceivingTime.setCellStyle(fontCellStyle);
        XSSFCell cellProcessingTime = rowTitle.createCell(11);
        cellProcessingTime.setCellValue("加工纳期");
        cellProcessingTime.setCellStyle(fontCellStyle);
        XSSFCell cellPlanTime = rowTitle.createCell(12);
        cellPlanTime.setCellValue("计划纳期");
        cellPlanTime.setCellStyle(fontCellStyle);
        XSSFCell cellDesigner = rowTitle.createCell(13);
        cellDesigner.setCellValue("设计");
        cellDesigner.setCellStyle(fontCellStyle);
        XSSFCell cellInStockTime = rowTitle.createCell(14);
        cellInStockTime.setCellValue("入库日期");
        cellInStockTime.setCellStyle(fontCellStyle);
        XSSFCell cellInStockNumber = rowTitle.createCell(15);
        cellInStockNumber.setCellValue("入库数量");
        cellInStockNumber.setCellStyle(fontCellStyle);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < respList.size(); i++) {
            // 创建行
            XSSFRow row = sheet.createRow(i + 1);
            XSSFCell index = row.createCell(0);
            index.setCellValue(i + 1);
            XSSFCell provider = row.createCell(1);
            provider.setCellValue(respList.get(i).getProvider());
            XSSFCell projectCode = row.createCell(2);
            projectCode.setCellValue(respList.get(i).getProjectCode());
            XSSFCell componentName = row.createCell(3);
            componentName.setCellValue(respList.get(i).getComponentName());
            XSSFCell name = row.createCell(4);
            name.setCellValue(respList.get(i).getName());
            XSSFCell rawMaterial = row.createCell(5);
            rawMaterial.setCellValue(respList.get(i).getRawMaterial());
            XSSFCell rawSurface = row.createCell(6);
            rawSurface.setCellValue(respList.get(i).getSurfaceTreatment());
            XSSFCell isFast = row.createCell(7);
            isFast.setCellValue(respList.get(i).getIsFast());
            XSSFCell materialNumber = row.createCell(8);
            materialNumber.setCellValue(respList.get(i).getMaterialNumber() == null ? 0L : respList.get(i).getMaterialNumber());
            XSSFCell getNumber = row.createCell(9);
            getNumber.setCellValue(respList.get(i).getGetNumber() == null ? 0L : respList.get(i).getGetNumber());
            XSSFCell receivingTime = row.createCell(10);
            if (respList.get(i).getReceivingTime() != null) {
                receivingTime.setCellValue(format.format(respList.get(i).getReceivingTime()));
            } else {
                receivingTime.setCellValue("未收货");
            }
            if (StrUtil.isNotBlank(respList.get(i).getReceiveDelay()) && "是".equals(respList.get(i).getReceiveDelay())) {
                // 设置超期颜色
                receivingTime.setCellStyle(redCellStyle);
            }

            XSSFCell processingTime = row.createCell(11);
            if (respList.get(i).getProcessingTime() != null) {
                processingTime.setCellValue(format.format(respList.get(i).getProcessingTime()));
            }
            XSSFCell planTime = row.createCell(12);
            if (respList.get(i).getPlanTime() != null) {
                planTime.setCellValue(format.format(respList.get(i).getPlanTime()));
            }
            XSSFCell designer = row.createCell(13);
            designer.setCellValue(respList.get(i).getDesigner() == null ? "" : respList.get(i).getDesigner());

            XSSFCell inStockTime = row.createCell(14);
            if (respList.get(i).getInStockTime() != null) {
                inStockTime.setCellValue(format.format(respList.get(i).getInStockTime()));
            } else {
                inStockTime.setCellValue("未入库");
            }
            if (StrUtil.isNotBlank(respList.get(i).getInStockDelay()) && "是".equals(respList.get(i).getInStockDelay())) {
                // 设置超期颜色
                inStockTime.setCellStyle(redCellStyle);
            }
            XSSFCell inStockNumber = row.createCell(15);
            inStockNumber.setCellValue(respList.get(i).getInStockNumber() == null ? 0L : respList.get(i).getInStockNumber());
        }
        for (int i = 0; i < 16; i++) {
            // 设置列宽
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 15 / 10);
        }
        return workbook;
    }


}
