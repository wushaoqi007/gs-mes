package com.greenstone.mes.ces.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.ces.enums.ItemStatus;
import lombok.*;

import java.io.Serial;

/**
 * 物品型号
 *
 * @author wushaoqi
 * @date 2023-05-22-10:02
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("ces_item_specification")
public class ItemSpecDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -6863324521312592266L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField
    private String typeCode;

    @TableField
    private String typeName;

    @TableField
    private String itemCode;

    @TableField
    private String itemName;

    @TableField
    private ItemStatus status;

    @TableField
    private String specification;

    @TableField
    private String unit;

    @TableField
    private String barCode;

    @TableField
    private String brand;

    @TableField
    private Double defaultPrice;

    @TableField
    private Long maxSecureStock;

    @TableField
    private Long minSecureStock;

    @TableField
    private String remark;

    @TableField("is_need_return")
    private String needReturn;

    @TableField
    private String lossRatePerYear;
}
