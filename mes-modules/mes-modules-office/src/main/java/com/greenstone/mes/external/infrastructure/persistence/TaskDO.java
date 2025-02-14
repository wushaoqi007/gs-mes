package com.greenstone.mes.external.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.external.enums.TaskStatus;
import lombok.*;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2023/3/2 9:04
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("flow_task")
public class TaskDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -1308880444264311705L;

    @TableId
    private String taskId;
    @TableField
    private String processInstanceId;
    @TableField
    private TaskStatus taskStatus;
    @TableField
    private String comment;
    @TableField
    private String serialNo;
    @TableField
    private String formId;

    private String formName;
    @TableField
    private Long appliedBy;
    @TableField
    private String appliedByName;
    @TableField
    private LocalDateTime appliedTime;
    @TableField
    private Long approvedBy;
    @TableField
    private String approvedByName;
    @TableField
    private LocalDateTime approvedTime;

}
