package com.greenstone.mes.machine.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.table.FlowFieldType;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.WorkflowField;
import com.greenstone.mes.table.infrastructure.annotation.StreamField;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MachineOrder extends TableEntity {
    @StreamField("特殊订单")
    private Boolean special;
    @StreamField("合同号")
    private String contractNo;
    @StreamField("供应商")
    private String provider;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @StreamField("下单时间")
    @WorkflowField
    private LocalDate orderTime;
    @StreamField("备注")
    private String remark;
    @StreamField("总价")
    private BigDecimal totalPrice;
    @StreamField("总加工数量")
    private Long totalProcess;
    @StreamField("总收货数量")
    private Long totalReceived;

    @StreamField("零件列表")
    @Builder.Default
    private List<MachineOrderDetail> parts = new ArrayList<>();

    @StreamField("附件列表")
    @Builder.Default
    private List<MachineOrderAttachment> attachments = new ArrayList<>();

    @WorkflowField(fieldType = FlowFieldType.DETAIL_LINK)
    private String detailLink;

}
