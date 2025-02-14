package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.system.api.domain.FileRecord;
import lombok.*;

import java.util.List;

/**
 * 任务问题报告
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
@TableName("material_task_problem_report")
public class MaterialTaskProblemReport extends BaseEntity {

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
     * 问题类型（1设计、2采购、3机加工、4装配）
     */
    @TableField
    private Integer type;

    /**
     * 提出人
     */
    @TableField
    private Long questioner;

    /**
     * 提问人姓名
     */
    @TableField
    private String questionerName;


    /**
     * 问题描述
     */
    @TableField
    private String description;

    /**
     * 附件信息
     */
    @TableField(exist=false)
    private List<FileRecord> fileInfoList;
}
