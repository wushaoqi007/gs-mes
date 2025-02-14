package com.greenstone.mes.ces.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * 出库单明细
 *
 * @author wushaoqi
 * @date 2023-06-5-9:47
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "ces_warehouse_out_detail")
public class WarehouseOutDetailDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 6170704482003580553L;

    @TableId(type = IdType.AUTO)
    private String id;
    @TableField
    private String serialNo;
    @TableField
    private String applicationSerialNo;
    @TableField
    private String requisitionSerialNo;
    @TableField
    private String clearSerialNo;
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
    private Long outStockNum;
    @TableField
    private Double unitPrice;
    @TableField
    private Double totalPrice;
    @TableField
    private String picturePath;
    @TableField
    private String remark;

}
