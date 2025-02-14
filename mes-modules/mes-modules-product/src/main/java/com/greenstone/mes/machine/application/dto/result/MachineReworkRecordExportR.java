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
public class MachineReworkRecordExportR {
    @ExcelProperty("供应商")
    private String provider;

    @ExcelProperty("项目代码")
    private String projectCode;

    @ExcelProperty("零件号")
    private String partCode;

    @ExcelProperty("零件版本")
    private String partVersion;

    @ExcelProperty("零件名称")
    private String partName;

    @ExcelProperty("数量")
    private Long checkedNumber;

    @ColumnWidth(25)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("返工时间")
    private LocalDateTime createTime;

    @ExcelProperty("NG大类")
    private String ngType;

    @ExcelProperty("NG小类")
    private String subNgType;

    @ExcelProperty("经手人")
    private String checkBy;

    @ColumnWidth(25)
    @ExcelProperty("返工单号")
    private String checkSerialNo;
}
