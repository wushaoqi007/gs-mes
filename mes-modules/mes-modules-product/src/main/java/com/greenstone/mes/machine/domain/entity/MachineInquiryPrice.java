package com.greenstone.mes.machine.domain.entity;

import com.greenstone.mes.machine.infrastructure.enums.InquiryPriceStatus;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.infrastructure.annotation.StreamField;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MachineInquiryPrice extends TableEntity {

    @StreamField("处理状态")
    private InquiryPriceStatus handleStatus;

    @StreamField("零件种类数量")
    private Integer categoryTotal;

    @StreamField("零件总数")
    private Long partTotal;

    @StreamField("图纸总数")
    private Integer paperTotal;

    @StreamField("加急")
    private Boolean urgent;

    @StreamField("备注")
    private String remark;

    @StreamField("零件列表")
    @Builder.Default
    private List<MachineInquiryPriceDetail> parts = new ArrayList<>();
    ;
}
