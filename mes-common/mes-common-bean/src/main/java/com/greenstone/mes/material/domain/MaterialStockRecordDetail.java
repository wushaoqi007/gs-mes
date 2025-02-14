package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 物料出入库记录明细对象 material_stock_record_detail
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
@TableName("material_stock_record_detail")
public class MaterialStockRecordDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 记录id
     */
    @Excel(name = "记录id")
    @TableField
    private Long recordId;

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
     * 出/入库
     */
    @Excel(name = "出/入库")
    @TableField
    private Integer operation;

    /**
     * 阶段操作（1 收件2 取件检验3 检验完成4 需要表处5 需要返工6 去表处7 表处完成8 去返工9 返工完成10 良品入库11 领用）
     */
    @TableField
    private Integer stageOperation;

    /**
     * 操作数量
     */
    @Excel(name = "操作数量")
    @TableField
    private Long number;

    /**
     * 操作后当前仓库数量
     */
    @Excel(name = "操作后当前仓库数量")
    @TableField
    private Long numberAfterOperation;

    /**
     * 经手人
     */
    @Excel(name = "经手人")
    @TableField
    private String sponsor;

}