package com.greenstone.mes.product.application.dto.result;

import com.alibaba.excel.annotation.ExcelProperty;
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
public class ProductPlanExportR {

    @ExcelProperty("计划状态")
    private String planStatus;

    @ExcelProperty("计划编号")
    private String serialNo;

    @ExcelProperty("项目代码")
    private String projectCode;

    @ExcelProperty("名称")
    private String name;

    @ExcelProperty("类型")
    private String level;

    @ExcelProperty("数量")
    private Integer number;

    @ColumnWidth(25)
    @ExcelProperty("计划开始时间")
    private String planStartTime;

    @ColumnWidth(25)
    @ExcelProperty("计划结束时间")
    private String planEndTime;

    @ExcelProperty("计划周期")
    private String planPeriod;

    @ExcelProperty("完成率")
    private Double completionRate;

    @ColumnWidth(25)
    @ExcelProperty("实际开始时间")
    private String actualStartTime;

    @ColumnWidth(25)
    @ExcelProperty("实际结束时间")
    private String actualEndTime;

    @ExcelProperty("实际周期")
    private String actualPeriod;

    @ExcelProperty("变更类型")
    private String planChangeType;

    @ExcelProperty("变更原因")
    private String reason;

    @ExcelProperty("责任部门")
    private String dept;

}
