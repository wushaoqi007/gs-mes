package com.greenstone.mes.material.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * 设计月统计表
 *
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("material_stat_result_designer")
public class StatResultDesignerDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -9124153051205841344L;

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField
    private String statisticDate;
    @TableField
    private String statisticMonth;
    @TableField
    private String projectCode;
    @TableField
    private Integer partTotal;
    @TableField
    private Integer paperTotal;
    @TableField("is_overdue")
    private Boolean overdue;
    @TableField
    private Integer overdueDays;
    @TableField
    private Integer partUpdateTotal;
    @TableField
    private Integer paperUpdateTotal;
    @TableField
    private Integer partUrgentTotal;
    @TableField
    private Integer paperUrgentTotal;
    @TableField
    private Integer partRepairTotal;
    @TableField
    private Integer paperRepairTotal;
}
