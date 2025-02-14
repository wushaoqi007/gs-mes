package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 任务工作报告
 *
 * @author wushaoqi
 * @date 2022-08-08-10:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("material_task_work_report")
public class MaterialTaskWorkReport extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    @TableField
    private Long taskId;

    /**
     * 工作时长
     */
    @TableField
    private Double takeTime;

    /**
     * 工作描述
     */
    @TableField
    private String description;
}
