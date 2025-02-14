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

import java.util.Date;

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
public class MachineRequirementExportR {

    @ColumnWidth(30)
    @ExcelProperty("申请单号")
    private String serialNo;
    @ExcelProperty("项目代码")
    private String projectCode;
    @ExcelProperty("组件号")
    private String componentCode;
    @ExcelProperty("机种名称")
    private String hierarchy;
    @ExcelProperty("零件号/版本")
    private String partCodeAndVersion;
    @ExcelProperty("零件名称")
    private String partName;
    @ExcelProperty("数量")
    private Long processNumber;
    @ExcelProperty("设计")
    private String designer;
    @ExcelProperty("材料")
    private String rawMaterial;
    @ExcelProperty("表面处理")
    private String surfaceTreatment;
    @ExcelProperty("质量g")
    private String weight;
    @ExcelProperty("备注")
    private String remark;
    @ExcelProperty("加工单位")
    private String provider;
    @ColumnWidth(25)
    @DateTimeFormat("yyyy-MM-dd")
    @ExcelProperty("加工纳期")
    private Date processDeadline;
    @ColumnWidth(25)
    @DateTimeFormat("yyyy-MM-dd")
    @ExcelProperty("计划纳期")
    private Date planDeadline;


}
