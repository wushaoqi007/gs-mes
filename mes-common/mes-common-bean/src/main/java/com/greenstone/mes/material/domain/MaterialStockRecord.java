package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 物料出入库记录对象 material_stock_record
 *
 * @author gu_renkai
 * @date 2022-02-17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("material_stock_record")
public class MaterialStockRecord extends BaseEntity {

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
     * 出/入库
     */
    @Excel(name = "出/入库")
    @TableField
    private Integer operation;

    /**
     * 经手人
     */
    @Excel(name = "经手人")
    @TableField
    private String sponsor;

    @TableField
    private String remark;

}