package com.greenstone.mes.product.domain.entity;

import com.greenstone.mes.table.TableChangeReason;
import com.greenstone.mes.table.infrastructure.annotation.ReasonField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ProductPlanChangeReason extends TableChangeReason {

    @ReasonField(value = "变更类型", necessary = true)
    private String changeType;

    @ReasonField("变更原因")
    private String changeReason;

    @ReasonField("责任部门")
    private String dept;

}
