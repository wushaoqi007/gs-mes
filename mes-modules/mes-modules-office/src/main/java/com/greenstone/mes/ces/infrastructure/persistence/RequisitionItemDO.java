package com.greenstone.mes.ces.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2023-11-08-11:31
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "ces_requisition_item")
public class RequisitionItemDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 382073180926230026L;

    @TableId(type = IdType.AUTO)
    private String id;
    private String serialNo;
    private String itemName;
    private String itemCode;
    private String typeName;
    private String specification;
    private Long requisitionNum;
    private Double unitPrice;
    private String unit;
    private Double totalPrice;
    @TableField("is_need_return")
    private String needReturn;
    private LocalDateTime returnDate;
    private Long returnNum;
    private Long lossNum;
    private String warehouseCode;
}
