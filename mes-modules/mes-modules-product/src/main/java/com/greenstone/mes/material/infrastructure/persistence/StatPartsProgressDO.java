package com.greenstone.mes.material.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;
import java.util.Date;

/**
 * 项目零件进度统计表
 *
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("material_stat_parts_progress")
public class StatPartsProgressDO extends BaseEntity {


    @Serial
    private static final long serialVersionUID = 644444112531267447L;

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField
    private Long worksheetDetailId;
    @TableField
    private String customerName;
    @TableField
    private String customerShortName;
    @TableField
    private Integer deliverPartNum;
    @TableField
    private Integer deliverPaperNum;
    @TableField
    private String deliverRate;
    @TableField
    private Integer finishedPartNum;
    @TableField
    private Integer finishedPaperNum;
    @TableField
    private String finishedRate;
    @TableField
    private String projectCode;
    @TableField
    private String componentCode;
    @TableField
    private String componentName;
    @TableField
    private Date uploadTime;
    @TableField
    private Date planTime;
    @TableField
    private Integer paperNum;
    @TableField
    private Integer partNum;
    @TableField
    private String remark;

}
