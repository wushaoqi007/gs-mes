package com.greenstone.mes.meal.application.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.greenstone.mes.common.utils.excel.ExcelDictConvert;
import com.greenstone.mes.common.utils.excel.ExcelDictType;
import com.greenstone.mes.system.consts.DictType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MealReportExcel {

    @ExcelProperty("部门")
    private String deptName;

    @ExcelDictType(DictType.MEAL_REPORT_TYPE)
    @ExcelProperty(value = "报餐类型", converter = ExcelDictConvert.class)
    private Integer reportType;

    @ExcelDictType(DictType.MEAL_TYPE)
    @ExcelProperty(value = "用餐类型", converter = ExcelDictConvert.class)
    private Integer mealType;

    @DateTimeFormat("yyyy-MM-dd")
    @ExcelProperty("日期")
    private LocalDate day;

    @ExcelProperty("报餐人")
    private String reportBy;

    @ExcelProperty("工号")
    private String reportByNo;

    @ExcelProperty("报餐数量")
    private Integer mealNum;

    @ExcelProperty("用餐数量")
    private Integer usedNum;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("报餐时间")
    private LocalDateTime createTime;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("用餐时间")
    private LocalDateTime useTime;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("操作人")
    private String createBy;

}
