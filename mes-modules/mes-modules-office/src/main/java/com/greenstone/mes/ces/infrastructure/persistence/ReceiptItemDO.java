package com.greenstone.mes.ces.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;
import java.time.LocalDate;

/**
 * 收货单物品表
 *
 * @author wushaoqi
 * @date 2023-05-25-9:47
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "ces_receipt_item")
public class ReceiptItemDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 2300888629493298922L;

    @TableId(type = IdType.AUTO)
    private String id;
    @TableField
    private String serialNo;
    @TableField
    private String orderSerialNo;
    @TableField
    private String orderItemId;
    @TableField
    private Long orderNum;
    @TableField
    private String applicationItemId;
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
    @TableField
    private LocalDate invoiceDate;
    @TableField
    private String invoiceCode;
    @TableField
    private String remark;
    @TableField
    private String warehouseCode;

}
