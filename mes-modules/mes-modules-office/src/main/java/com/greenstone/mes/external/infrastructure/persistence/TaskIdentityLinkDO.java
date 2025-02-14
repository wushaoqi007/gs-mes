package com.greenstone.mes.external.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.external.infrastructure.enums.ApproveType;
import lombok.*;

import java.io.Serial;

/**
 * @author gu_renkai
 * @date 2023/3/7 15:33
 */

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("flow_task_identity_link")
public class TaskIdentityLinkDO extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 5110654128216034247L;
    /**
     * id
     */
    @TableId
    private String id;
    /**
     * 类型
     */
    private ApproveType type;
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户名称
     */
    private String userName;

}