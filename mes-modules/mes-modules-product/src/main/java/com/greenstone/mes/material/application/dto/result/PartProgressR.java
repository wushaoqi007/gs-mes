package com.greenstone.mes.material.application.dto.result;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

/**
 * @author wushaoqi
 * @date 2023-06-26-15:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PartProgressR {

    @ExcelProperty({"项目代码"})
    @ColumnWidth(25)
    private String projectCode;
    @ExcelIgnore
    private String componentCode;
    @ExcelIgnore
    private String componentName;
    @ColumnWidth(25)
    @ExcelProperty({"零件号"})
    private String partCode;
    @ExcelProperty({"零件版本"})
    private String partVersion;
    @ExcelProperty({"零件名称"})
    @ColumnWidth(25)
    private String partName;
    @ExcelProperty({"已采购"})
    private Integer purchasedNum;
    @ExcelProperty({"已收件"})
    private Integer receivedNum;
    @ExcelProperty({"已检验"})
    private Integer checkedNum;
    @ExcelProperty({"已入库"})
    private Integer finishedNum;
    @ExcelProperty({"已领用"})
    private Integer usedNum;
    @ExcelProperty({"表面处理"})
    private String surfaceTreatment;
    @ExcelProperty({"材料"})
    private String rawMaterial;
    @ExcelProperty({"重量"})
    private String weight;
    @ColumnWidth(25)
    @DateTimeFormat("yyyy-MM-dd")
    @ExcelProperty("打印日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date printDate;
    /**
     * 加工单位
     */
    @ExcelProperty({"加工单位"})
    private String provider;
    /**
     * 加工纳期
     */
    @ColumnWidth(25)
    @DateTimeFormat("yyyy-MM-dd")
    @ExcelProperty("加工纳期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date processingTime;
    /**
     * 计划纳期
     */
    @ColumnWidth(25)
    @DateTimeFormat("yyyy-MM-dd")
    @ExcelProperty("计划纳期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planTime;
    /**
     * 收货日期
     */
    @ColumnWidth(25)
    @DateTimeFormat("yyyy-MM-dd")
    @ExcelProperty("收货日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date receivingTime;
    @ExcelProperty({"设计"})
    private String designer;
    @ExcelProperty({"检验人"})
    private String inspector;
    /**
     * 完成时间
     */
    @ColumnWidth(25)
    @DateTimeFormat("yyyy-MM-dd")
    @ExcelProperty("完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date finishTime;
}
