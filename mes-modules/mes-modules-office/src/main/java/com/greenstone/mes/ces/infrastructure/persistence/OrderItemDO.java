package com.greenstone.mes.ces.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * 订单物品表
 *
 * @author wushaoqi
 * @date 2023-05-24-9:47
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "ces_order_item")
public class OrderItemDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 6728018395164057944L;

    @TableId(type = IdType.AUTO)
    private String id;
    @TableField
    private String serialNo;
    @TableField
    private String applicationSerialNo;
    @TableField
    private String applicationItemId;
    @TableField
    private Long applicationNum;
    @TableField
    private String itemName;
    @TableField
    private Long itemNum;
    @TableField
    private String itemCode;
    @TableField
    private Long receivedNum;
    @TableField
    private Double unitPrice;
    @TableField
    private String purchaseLink;
    @TableField
    private String specification;
    @TableField
    private String picturePath;
    @TableField
    private String unit;
    @TableField
    private Double totalPrice;
    @TableField
    private String provider;

}
