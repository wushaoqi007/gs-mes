package com.greenstone.mes.oa.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 设计部门项目对象 oa_design_project
 *
 * @author gu_renkai
 * @date 2022-05-24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("oa_design_project")
public class OaDesignProject extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 业务代码 */
    @TableField
    private String projectCode;

    @TableField
    private String projectName;

    /** 组长 */
    @TableField
    private String groupLeader;

    /** 设计担当 */
    @TableField
    private String designer;

    /** 类型 */
    @TableField
    private Integer projectType;

    /** 阶段 */
    @TableField
    private Integer phase;

    /** 3D造型完成日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("works_3d_finish_time")
    private Date works3dFinishTime;

    /** 方案评审日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField
    private Date planReviewTime;

    /** 细化评审 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField
    private Date detailReviewTime;

    /** 2D图完成日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("works_2d_finish_time")
    private Date works2dFinishTime;

    /** 输出评审 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField
    private Date outputReviewTime;

    /** 纳期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField
    private Date deadline;

}