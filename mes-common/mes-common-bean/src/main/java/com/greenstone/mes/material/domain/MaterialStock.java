package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 物料库存对象 material_stock
 *
 * @author gu_renkai
 * @date 2022-01-21
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("material_stock")
public class MaterialStock extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 仓库ID
     */
    @Excel(name = "仓库ID")
    @TableField
    private Long warehouseId;

    /**
     * 物料ID
     */
    @Excel(name = "物料ID")
    @TableField
    private Long materialId;

    /**
     * 库存数量
     */
    @Excel(name = "库存数量")
    @TableField
    private Long number;

    /**
     * 加工单编号
     */
    @TableField
    private String worksheetCode;

    /**
     * 组件号
     */
    @TableField
    private String componentCode;

    /**
     * 领料单详情ID
     */
    @TableField
    private Long receivingDetailId;

}
