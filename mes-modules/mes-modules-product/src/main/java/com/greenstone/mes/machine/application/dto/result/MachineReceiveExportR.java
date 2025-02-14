package com.greenstone.mes.machine.application.dto.result;

import com.greenstone.mes.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineReceiveExportR {

    @Excel(name = "收货单号")
    private String serialNo;
    @Excel(name = "加工单位")
    private String provider;
    @Excel(name = "生产代码")
    private String projectCode;
    @Excel(name = "零件号")
    private String partCode;
    @Excel(name = "零件名称")
    private String partName;
    @Excel(name = "零件版本")
    private String partVersion;
    @Excel(name = "订单数量")
    private Long expectedNumber;
    @Excel(name = "收货数量")
    private Long actualNumber;
    @Excel(name = "收货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date receiveTime;
    @Excel(name = "收货人")
    private String receiver;
    @Excel(name = "收货仓库")
    private String warehouseCode;
}
