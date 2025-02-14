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
public class MachineOrderExportR {

    @Excel(name = "订单号")
    private String serialNo;
    @Excel(name = "加工单位")
    private String provider;
    @Excel(name = "发图日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date orderTime;
    @Excel(name = "生产代码")
    private String projectCode;
    @Excel(name = "零件号")
    private String partCode;
    @Excel(name = "零件名称")
    private String partName;
    @Excel(name = "零件版本")
    private String partVersion;
    @Excel(name = "订单数量")
    private Long processNumber;
    @Excel(name = "收货数量")
    private Long receivedNumber;
    @Excel(name = "收货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date receiveTime;
    @Excel(name = "单价")
    private Double unitPrice;
    @Excel(name = "总价")
    private Double totalPrice;
    @Excel(name = "加工纳期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date processDeadline;
    @Excel(name = "计划纳期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date planDeadline;
    @Excel(name = "材料")
    private String rawMaterial;
    @Excel(name = "重量g")
    private String weight;
    @Excel(name = "机种名称")
    private String hierarchy;
    @Excel(name = "设计")
    private String designer;
    @Excel(name = "备注")
    private String remark;
}
