package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.util.Date;

/**
 * 任务表
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
@TableName("material_task")
public class MaterialTask extends BaseEntity {

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
     * 项目代码
     */
    @TableField
    private String projectCode;

    /**
     * 任务名称
     */
    @TableField
    private String taskName;

    /**
     * 负责人（userID）
     */
    @TableField
    private Long leader;

    /**
     * 负责人姓名
     */
    @TableField
    private String leaderName;

    /**
     * 任务类型（1领料、2组装、3罩壳、4机构调试功能测试）
     */
    @TableField
    private Integer type;

    /**
     * 任务状态(0未开始、1进行中、2已完成、3已关闭)
     */
    @TableField
    private Integer status;

    /**
     * 任务进度
     */
    @TableField
    private Integer progress;

    /**
     * 纳期
     */
    @TableField
    private Date deadline;

    /**
     * 总耗时
     */
    @TableField
    private Double takeTime;

}
