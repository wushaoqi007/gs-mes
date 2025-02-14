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
 * 任务进度报告
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
@TableName("material_task_progress_report")
public class MaterialTaskProgressReport extends BaseEntity {

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
     * 任务进度
     */
    @TableField
    private Integer progress;

    /**
     * 备注
     */
    @TableField
    private String remark;

    /**
     * 附件信息
     */
    @TableField(exist=false)
    private List<FileRecord> fileInfoList;

}
