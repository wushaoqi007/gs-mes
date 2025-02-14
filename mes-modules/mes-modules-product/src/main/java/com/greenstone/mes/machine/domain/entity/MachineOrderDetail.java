package com.greenstone.mes.machine.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.infrastructure.annotation.StreamField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:26
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MachineOrderDetail extends TableEntity {
    @StreamField("询价单号")
    private String inquiryPriceSerialNo;
    @StreamField("申请单号")
    private String requirementSerialNo;
    @StreamField("项目代码")
    private String projectCode;
    @StreamField("层级结构")
    private String hierarchy;
    private Long materialId;
    @StreamField("零件号")
    private String partCode;
    @StreamField("零件名称")
    private String partName;
    @StreamField("版本")
    private String partVersion;
    @StreamField("购买数量")
    private Long processNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @StreamField("加工纳期")
    private LocalDate processDeadline;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @StreamField("计划纳期")
    private LocalDate planDeadline;
    @StreamField("供应商")
    private String provider;
    @StreamField("表面处理")
    private String surfaceTreatment;
    @StreamField("材料")
    private String rawMaterial;
    @StreamField("质量")
    private String weight;
    @StreamField("设计")
    private String designer;
    @StreamField("备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @StreamField("收货时间")
    private LocalDateTime receiveTime;
    @StreamField("收货数量")
    private Long receivedNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @StreamField("入库时间")
    private LocalDateTime inStockTime;
    @StreamField("入库数量")
    private Long inStockNumber;

    @StreamField("单价")
    private BigDecimal unitPrice;
    @StreamField("总价")
    private BigDecimal totalPrice;

    @StreamField("附件名称")
    private String attachmentName;
    @StreamField("附件路径")
    private String attachmentPath;
}
