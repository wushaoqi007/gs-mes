package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.util.Date;

/**
 * 采购单详情
 *
 * @author wushaoqi
 * @date 2022-05-16-13:00
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@TableName("purchase_order_change")
public class PurchaseOrderChange extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 机加工单号
     */
    @TableField
    private String partOrderCode;


    /**
     * 零件号
     */
    @TableField
    private String code;

    /**
     * 版本
     */
    @TableField
    private String version;

    /**
     * 零件名称
     */
    @TableField
    private String name;

    /**
     * 加工单详情id
     */
    @TableField
    private Long detailId;

    /**
     * 原来数量
     */
    @TableField
    private Long originalNumber;

    /**
     * 备注
     */
    @TableField
    private String remark;

    /**
     * 变更数量
     */
    @TableField
    private Long applyNumber;

    @TableField
    private String applyReason;

    /**
     * 实际变更数量
     */
    @TableField
    private Long actualChangeNumber;



}
