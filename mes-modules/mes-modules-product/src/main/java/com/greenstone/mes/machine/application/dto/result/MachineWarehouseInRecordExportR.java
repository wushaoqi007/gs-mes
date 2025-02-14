package com.greenstone.mes.machine.application.dto.result;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.enums.poi.BorderStyleEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ContentRowHeight(16)
@HeadRowHeight(20)
@ColumnWidth(15)
@ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER,
        borderLeft = BorderStyleEnum.THIN,
        borderRight = BorderStyleEnum.THIN,
        borderTop = BorderStyleEnum.THIN,
        borderBottom = BorderStyleEnum.THIN)
public class MachineWarehouseInRecordExportR {
    @ExcelProperty("项目代码")
    private String projectCode;

    @ExcelProperty("零件号")
    private String partCode;

    @ExcelProperty("零件版本")
    private String partVersion;

    @ExcelProperty("零件名称")
    private String partName;

    @ExcelProperty("数量")
    private Long inStockNumber;

    @ExcelProperty("经手人")
    private String sponsor;

    @ExcelProperty("送料人")
    private String applicant;

    @ColumnWidth(25)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("入库时间")
    private LocalDateTime inStockTime;

    @ExcelProperty("库位")
    private String warehouseCode;

    @ColumnWidth(25)
    @ExcelProperty("入库单号")
    private String serialNo;
}
