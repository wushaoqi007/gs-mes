package com.greenstone.mes.ces.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * 入库单明细
 *
 * @author wushaoqi
 * @date 2023-06-2-9:47
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "ces_warehouse_in_detail")
public class WarehouseInDetailDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -634211833719777994L;

    @TableId(type = IdType.AUTO)
    private String id;
    @TableField
    private String serialNo;
    @TableField
    private String receiptSerialNo;
    @TableField
    private String returnSerialNo;
    @TableField
    private String itemCode;
    @TableField
    private String itemName;
    @TableField
    private String specification;
    @TableField
    private String typeName;
    @TableField
    private String unit;
    @TableField
    private Long inStockNum;
    @TableField
    private Double unitPrice;
    @TableField
    private Double totalPrice;
    @TableField
    private String picturePath;
    @TableField
    private String remark;

}
