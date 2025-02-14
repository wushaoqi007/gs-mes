package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.machine.infrastructure.enums.InquiryPriceStatus;
import com.greenstone.mes.table.TablePo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@TableName(value = "machine_inquiry_price")
public class MachineInquiryPriceDO extends TablePo {

    private InquiryPriceStatus handleStatus;
    private Integer categoryTotal;
    private Long partTotal;
    private Integer paperTotal;
    @TableField("is_urgent")
    private Boolean urgent;
    private String remark;
}
