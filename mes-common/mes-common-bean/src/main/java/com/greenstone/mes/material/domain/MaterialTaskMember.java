package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 任务人员关联表
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
@TableName("material_task_member")
public class MaterialTaskMember extends BaseEntity {

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
     * 人员ID
     */
    @TableField
    private Long memberId;

    /**
     * 成员类型：0：负责人；1：成员
     */
    @TableField
    private Integer memberType;
}
