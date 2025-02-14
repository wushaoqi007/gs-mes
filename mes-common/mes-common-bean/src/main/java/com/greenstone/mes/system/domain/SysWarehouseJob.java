package com.greenstone.mes.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.util.List;

/**
 * 仓库任务
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
@TableName("sys_warehouse_job")
public class SysWarehouseJob extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField
    private String jobName;

    /**
     * 仓库ID
     */
    @TableField
    private Long warehouseId;

    @TableField(exist = false)
    private String warehouseName;

    /**
     * 是否包含子仓库
     */
    @TableField
    private Boolean containsChildren;

    /**
     * 超时时间(分钟)
     */
    @TableField
    private Integer timeout;

    /**
     * cron表达式
     */
    @TableField
    private String cron;

    /**
     * 任务状态（0正常 1暂停）
     */
    @TableField
    private String status;

    @TableField(exist = false)
    private List<SysWarehouseJobUser> sendList;

}
