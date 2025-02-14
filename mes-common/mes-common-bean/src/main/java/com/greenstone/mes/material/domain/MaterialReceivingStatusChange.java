package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 领料单状态变更记录表
 *
 * @author wushaoqi
 * @date 2022-08-15-8:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("material_receiving_status_change")
public class MaterialReceivingStatusChange extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 领料单ID
     */
    @TableField
    private Long receivingId;

    /**
     * 任务状态(0待接收、1备料中、2待领料、3已完成、4已关闭)
     */
    @TableField
    private Integer status;
}
