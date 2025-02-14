package com.greenstone.mes.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 仓库任务提醒人员
 *
 * @author wushaoqi
 * @date 2022-11-01-8:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("sys_warehouse_job_user")
public class SysWarehouseJobUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * 任务ID
     */
    private Long jobId;


    /**
     * 用户ID
     */
    private Long userId;
}
