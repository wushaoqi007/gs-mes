package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineOrderProgressExportResult {

    @Excel(name = "供应商")
    private String provider;
    @Excel(name = "项目代码")
    private String projectCode;
    @Excel(name = "订单号")
    private String serialNo;
    @Excel(name = "订单日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime orderTime;
    @Excel(name = "零件号")
    private String partCode;
    @Excel(name = "版本")
    private String partVersion;
    @Excel(name = "零件名称")
    private String partName;
    @Excel(name = "订单数量")
    private Long processNumber;
    @Excel(name = "单价")
    private Double unitPrice;
    @Excel(name = "小计")
    private Double totalPrice;
    @Excel(name = "已收货")
    private Long receivedNumber;
    @Excel(name = "已入库")
    private Long inStockNumber;
    @Excel(name = "已出库")
    private Long outStockNumber;
    @Excel(name = "待收货")
    private Long waitReceivedNumber;
    @Excel(name = "待质检")
    private Long waitCheckedNumber;
    @Excel(name = "返工中")
    private Long reworkingNumber;
    @Excel(name = "待表处")
    private Long waitSurfaceTreatNumber;
    @Excel(name = "表处中")
    private Long treatingSurfaceNumber;
    @Excel(name = "待入库")
    private Long waitInStockNumber;
}
