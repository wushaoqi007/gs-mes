package com.greenstone.mes.material.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.common.core.annotation.Excel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class MachinedPartImportDto {

    /**
     * 加工单位
     */
    @Excel(name = "加工单位")
    @NotEmpty(message = "加工单位不能为空")
    private String provider;

    /**
     * 组件编码
     */
    @Excel(name = "组件号")
    @NotEmpty(message = "组件号不能为空")
    private String componentCode;

    /**
     * 组件编码
     */
    @Excel(name = "组件版本")
    private String componentVersion;

    /**
     * 采购日期
     */
    @Excel(name = "零件号")
    @NotEmpty(message = "零件号不能为空")
    private String materialCode;

    /**
     * 物料版本
     */
    @Excel(name = "零件版本")
    private String materialVersion;

    /**
     * 采购数量
     */
    @Excel(name = "订单数量")
    @NotEmpty(message = "数量不能为空")
    private Long number;

    /**
     * 采购时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd")
    @Excel(name = "发图日期", width = 30, dateFormat = "yyyy/MM/dd")
    private Date purchaseTime;

    /**
     * 纳期
     */
    @JsonFormat(pattern = "yyyy/MM/dd")
    @Excel(name = "纳期", width = 30, dateFormat = "yyyy/MM/dd")
    private Date deliveryTime;

}
