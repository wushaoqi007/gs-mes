package com.greenstone.mes.machine.application.dto.result;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.enums.poi.BorderStyleEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.greenstone.mes.common.utils.excel.ExcelDictConvert;
import com.greenstone.mes.common.utils.excel.ExcelDictType;
import com.greenstone.mes.system.consts.DictType;
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
public class MachineReceiveRecordExportR {
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
    private Long actualNumber;

    @ColumnWidth(25)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("收货时间")
    private LocalDateTime receiveTime;

    @ExcelDictType(DictType.RECEIVE_TYPE)
    @ExcelProperty(value = "收货类型", converter = ExcelDictConvert.class)
    private Integer operation;

    @ExcelProperty("收货人")
    private String receiver;

    @ExcelProperty("库位")
    private String warehouseCode;

    @ColumnWidth(25)
    @ExcelProperty("收货单号")
    private String serialNo;
}
