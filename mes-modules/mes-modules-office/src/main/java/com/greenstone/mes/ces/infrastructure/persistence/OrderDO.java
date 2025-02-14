package com.greenstone.mes.ces.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.*;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 采购订单表
 *
 * @author wushaoqi
 * @date 2023-05-24-9:43
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "ces_order")
public class OrderDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -8262643950174044690L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @TableField
    private String serialNo;
    @TableField
    private ProcessStatus status;
    @TableField
    private LocalDate expectReceiveDate;
    @TableField
    private Long purchaserId;
    @TableField
    private String purchaserName;
    @TableField
    private LocalDateTime purchaseDate;
    @TableField
    private String remark;
}
